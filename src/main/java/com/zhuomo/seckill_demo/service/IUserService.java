package com.zhuomo.seckill_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.vo.LoginVo;
import com.zhuomo.seckill_demo.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhuomo
 * @since 2022-01-09
 */
public interface IUserService extends IService<User> {
    public RespBean login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
    public User getByUserTicket(String userTicket, HttpServletRequest request, HttpServletResponse response);
    public RespBean updatePassword(String userTicket, Long id, String password);
}
