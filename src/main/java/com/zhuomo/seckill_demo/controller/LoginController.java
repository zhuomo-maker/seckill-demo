package com.zhuomo.seckill_demo.controller;

import com.zhuomo.seckill_demo.service.IUserService;
import com.zhuomo.seckill_demo.vo.LoginVo;
import com.zhuomo.seckill_demo.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 登录
 *
 * @author zhuomo
 * @since 1.0.0
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
   @Autowired
   private IUserService userService;
   /**
     * 跳转登录页
     *
     * @return
     */
   @RequestMapping("/toLogin")
   public String toLogin() {
      return "login";
   }
   /**
     * 登录
     * @return
     */
   @RequestMapping("/doLogin")
   @ResponseBody
   public RespBean doLogin(@Valid LoginVo loginVo,HttpServletRequest request, HttpServletResponse
           response) {
      log.info(loginVo.toString());
      return userService.login(loginVo,request,response);
   }
}