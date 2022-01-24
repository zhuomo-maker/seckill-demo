package com.zhuomo.seckill_demo.controller;

import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.service.IGoodsService;
import com.zhuomo.seckill_demo.service.IUserService;
import com.zhuomo.seckill_demo.vo.DetailVo;
import com.zhuomo.seckill_demo.vo.GoodsVo;
import com.zhuomo.seckill_demo.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 商品
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    /**
     * 跳转登录页
     *
     * @return
     */

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    /**
     * 跳转商品列表页
     *
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toLogin(HttpServletRequest request, HttpServletResponse
            response, Model model, User user) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //Redis中获取页面，如果不为空，直接返回页面
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        // return "goodsList";
        //如果为空，手动渲染，存入Redis并返回
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",
                context);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }
    /**
     * 跳转商品详情页
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
//    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
//    @ResponseBody
//    public String toDetail(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable Long goodsId) {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //Redis中获取页面，如果不为空，直接返回页面
//        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
//        model.addAttribute("user", user);
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods", goods);
//        Date startDate = goods.getStartDate();
//        Date endDate = goods.getEndDate();
//        Date nowDate = new Date();
//        //秒杀状态
//        int secKillStatus = 0;
//        //剩余开始时间
//        int remainSeconds = 0;
//        //秒杀还未开始
//        if (nowDate.before(startDate)) {
//            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) /
//                    1000);
//            // 秒杀已结束
//        } else if (nowDate.after(endDate)) {
//            secKillStatus = 2;
//            remainSeconds = -1;
//            // 秒杀中
//        } else {
//            secKillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("secKillStatus", secKillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        // return "goodsDetail";
//        //如果为空，手动渲染，存入Redis并返回
//        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(),model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",
//                context);
//        if (!StringUtils.isEmpty(html)) {
//            valueOperations.set("goodsDetail:" + goodsId, html, 60,
//                    TimeUnit.SECONDS);
//        }
//        return html;
//    }
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable Long goodsId) {
    GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
    Date startDate = goods.getStartDate();
    Date endDate = goods.getEndDate();
    Date nowDate = new Date();
    //秒杀状态
    int secKillStatus = 0;
    //剩余开始时间
    int remainSeconds = 0;
    //秒杀还未开始
    if (nowDate.before(startDate)) {
         remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
         // 秒杀已结束
    } else if (nowDate.after(endDate)) {
         secKillStatus = 2;
         remainSeconds = -1;
         // 秒杀中
    } else {
         secKillStatus = 1;
         remainSeconds = 0;
    }
    DetailVo detailVo = new DetailVo();
    detailVo.setGoodsVo(goods);
    detailVo.setUser(user);
    detailVo.setRemainSeconds(remainSeconds);
    detailVo.setSecKillStatus(secKillStatus);
    return RespBean.success(detailVo);
    }

}