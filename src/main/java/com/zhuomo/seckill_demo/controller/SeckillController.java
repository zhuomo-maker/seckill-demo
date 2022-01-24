package com.zhuomo.seckill_demo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.wf.captcha.ArithmeticCaptcha;
import com.zhuomo.seckill_demo.config.AccessLimit;
import com.zhuomo.seckill_demo.exception.GlobalException;
import com.zhuomo.seckill_demo.pojo.Order;
import com.zhuomo.seckill_demo.pojo.SeckillOrder;
import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.rabbitmq.MQSender;
import com.zhuomo.seckill_demo.rabbitmq.SeckillMessage;
import com.zhuomo.seckill_demo.service.IGoodsService;
import com.zhuomo.seckill_demo.service.IOrderService;
import com.zhuomo.seckill_demo.service.ISeckillOrderService;
import com.zhuomo.seckill_demo.utils.JsonUtil;
import com.zhuomo.seckill_demo.vo.GoodsVo;
import com.zhuomo.seckill_demo.vo.RespBean;
import com.zhuomo.seckill_demo.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    private int count = 0;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DefaultRedisScript<Long> script;
    @Autowired
    private MQSender mqSender;
    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
  @ResponseBody
  public RespBean doSeckill(@PathVariable String path, User user, Long goodsId)
    {
   if (user == null) {
     return RespBean.error(RespBeanEnum.SESSION_ERROR);
   }
   ValueOperations valueOperations = redisTemplate.opsForValue();
   //校验秒杀地址
   boolean check = orderService.checkPath(user,goodsId,path);
   if (!check){
     return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
   }
   //判断是否重复抢购
   String seckillOrderJson = (String) valueOperations.get("order:" +
            user.getId() + ":" + goodsId);
   if (!StringUtils.isEmpty(seckillOrderJson)) {
     return RespBean.error(RespBeanEnum.REPEATE_ERROR);
   }
   //内存标记,减少Redis访问
   if (EmptyStockMap.get(goodsId)) {
     return RespBean.error(RespBeanEnum.EMPTY_STOCK);
   }
   //预减库存
   // Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
   // if (stock < 0) {
   //   EmptyStockMap.put(goodsId,true);
   //   valueOperations.increment("seckillGoods:" + goodsId);
   //   return RespBean.error(RespBeanEnum.EMPTY_STOCK);
   // }
   Long stock = (Long) redisTemplate.execute(script,
            Collections.singletonList("seckillGoods:" + goodsId),
                 Collections.EMPTY_LIST);
   if (stock <= 0) {
     EmptyStockMap.put(goodsId, true);
     return RespBean.error(RespBeanEnum.EMPTY_STOCK);
   }
   // 请求入队，立即返回排队中
   SeckillMessage message = new SeckillMessage(user, goodsId);
   mqSender.sendsecKillMessage(JsonUtil.object2JsonStr(message));
   return RespBean.success(0);
  }
  /**
   * 获取秒杀地址
   *
   * @param user
   * @param goodsId
   * @return
   */
  @AccessLimit(second = 5, maxCount = 5, needLogin = true)
  @RequestMapping(value = "/path", method = RequestMethod.GET)
  @ResponseBody
  public RespBean getPath(User user, Long goodsId,String captcha) {
  if (user == null) {
   return RespBean.error(RespBeanEnum.SESSION_ERROR);
  }
  boolean check = orderService.checkCaptcha(user, goodsId, captcha);
  if (!check){
   return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
  }
  String str = orderService.createPath(user, goodsId);
  return RespBean.success(str);
  }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(),
                    goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }

    /**
     * 获取秒杀结果
     *
     * @param user
     * @param goodsId
     * @return orderId:成功，-1：秒杀失败，0：排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
  if (null==user||goodsId<0){
   throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
  }
  // 设置请求头为输出图片类型
  response.setContentType("image/jpg");
  response.setHeader("Pragma", "No-cache");
  response.setHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 0);
  //生成验证码，将结果放入redis
  ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
 
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text
                (),300, TimeUnit.SECONDS);
  try {
   captcha.out(response.getOutputStream());
  } catch (IOException e) {
   log.error("验证码生成失败",e.getMessage());
  }
    }


}

