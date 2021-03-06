package com.zhuomo.seckill_demo.controller;


import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.service.IOrderService;
import com.zhuomo.seckill_demo.vo.OrderDetailVo;
import com.zhuomo.seckill_demo.vo.RespBean;
import com.zhuomo.seckill_demo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Controller
@RequestMapping("/order")
public class OrderController {
   @Autowired
   private IOrderService orderService;
   /**
     * 订单详情
     * @param user
     * @param orderId
     * @return
     */
           @RequestMapping("/detail")
   @ResponseBody
   public RespBean detail(User user, Long orderId){
      if (null==user){
         return RespBean.error(RespBeanEnum.SESSION_ERROR);
     }
      OrderDetailVo detail = orderService.detail(orderId);
      return RespBean.success(detail);
   }
}