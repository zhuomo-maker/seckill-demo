package com.zhuomo.seckill_demo.controller;


import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.rabbitmq.MQSender;
import com.zhuomo.seckill_demo.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhuomo
 * @since 2022-01-09
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MQSender mqSender;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }
    /**
     * 测试发送RabbitMQ消息
     */
}

