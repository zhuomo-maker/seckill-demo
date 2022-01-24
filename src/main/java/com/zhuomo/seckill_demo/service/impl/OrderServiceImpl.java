package com.zhuomo.seckill_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuomo.seckill_demo.exception.GlobalException;
import com.zhuomo.seckill_demo.mapper.GoodsMapper;
import com.zhuomo.seckill_demo.mapper.OrderMapper;
import com.zhuomo.seckill_demo.pojo.Order;
import com.zhuomo.seckill_demo.pojo.SeckillGoods;
import com.zhuomo.seckill_demo.pojo.SeckillOrder;
import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.service.IGoodsService;
import com.zhuomo.seckill_demo.service.IOrderService;
import com.zhuomo.seckill_demo.service.ISeckillGoodsService;
import com.zhuomo.seckill_demo.service.ISeckillOrderService;
import com.zhuomo.seckill_demo.utils.JsonUtil;
import com.zhuomo.seckill_demo.utils.MD5Util;
import com.zhuomo.seckill_demo.utils.UUIDUtil;
import com.zhuomo.seckill_demo.vo.GoodsVo;
import com.zhuomo.seckill_demo.vo.OrderDetailVo;
import com.zhuomo.seckill_demo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
   @Autowired
   private ISeckillGoodsService seckillGoodsService;
   @Autowired
   RedisTemplate redisTemplate;
   @Autowired
   IGoodsService goodsService;
   @Autowired
   private OrderMapper orderMapper;
   @Autowired
   private ISeckillOrderService seckillOrderService;
   /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
   @Override
   @Transactional
   public Order seckill(User user, GoodsVo goods) {
      ValueOperations valueOperations = redisTemplate.opsForValue();
    //秒杀商品表减库存
    SeckillGoods seckillGoods = seckillGoodsService.getOne(new
              QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
    boolean seckillGoodsResult = seckillGoodsService.update(
                          new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count- 1").eq("goods_id", goods.getId()).gt("stock_count", 0));
                 // seckillGoodsService.updateById(seckillGoods);
                 if (seckillGoods.getStockCount() < 1) {
         //判断是否还有库存
         valueOperations.set("isStockEmpty:" + goods.getId(), "0");
         return null;
    }
    //生成订单
    Order order = new Order();
    order.setUserId(user.getId());
    order.setGoodsId(goods.getId());
    order.setDeliveryAddrId(0L);
    order.setGoodsName(goods.getGoodsName());
    order.setGoodsCount(1);
    order.setGoodsPrice(seckillGoods.getSeckillPrice());
    order.setOrderChannel(1);
    order.setStatus(0);
    order.setCreateDate(new Date());
    orderMapper.insert(order);
    //生成秒杀订单
    SeckillOrder seckillOrder = new SeckillOrder();
    seckillOrder.setOrderId(order.getId());
    seckillOrder.setUserId(user.getId());
    seckillOrder.setGoodsId(goods.getId());
    seckillOrderService.save(seckillOrder);
    valueOperations.set("order:" + user.getId() + ":" + goods.getId(),
                          JsonUtil.object2JsonStr(seckillOrder));
    return order;}

   @Override
   public OrderDetailVo detail(Long orderId) {
      if (null==orderId){
      throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
   }
   Order order = orderMapper.selectById(orderId);
   GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
   OrderDetailVo detail = new OrderDetailVo();
   detail.setGoodsVo(goodsVo);
   detail.setOrder(order);
   return detail;
   }
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
   if (user==null|| StringUtils.isEmpty(path)){
      return false;
   }
   String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" +
                user.getId() + ":" + goodsId);
   return path.equals(redisPath);
    }
    /**
     * 生成秒杀地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
   String str = MD5Util.md5(UUIDUtil.uuid() + "123456");

   redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" +
                goodsId, str, 60, TimeUnit.SECONDS);
   return str; }
    /**
     * 校验验证码
     *
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
  if (StringUtils.isEmpty(captcha)||null==user||goodsId<0){
   return false;
  }
  String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" +
                user.getId() + ":" + goodsId);
  return redisCaptcha.equals(captcha);
    }

}