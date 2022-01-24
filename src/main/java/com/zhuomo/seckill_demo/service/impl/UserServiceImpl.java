package com.zhuomo.seckill_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuomo.seckill_demo.exception.GlobalException;
import com.zhuomo.seckill_demo.mapper.UserMapper;
import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.service.IUserService;
import com.zhuomo.seckill_demo.utils.*;
import com.zhuomo.seckill_demo.vo.LoginVo;
import com.zhuomo.seckill_demo.vo.RespBean;
import com.zhuomo.seckill_demo.vo.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhuomo
 * @since 2022-01-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
   private UserMapper userMapper;
   @Autowired
   private RedisTemplate redisTemplate;
   /**
     * 登录
     * @param loginVo
     * @return
     */
   @Override
   public RespBean login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse
           response) {
   String mobile = loginVo.getMobile();
   String password = loginVo.getPassword();
   //根据手机号获取用户
   User user = userMapper.selectById(mobile);
   if (null==user){
      throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
   }
   //校验密码
   if (!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
      throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
   }
       String ticket = UUIDUtil.uuid();
      redisTemplate.opsForValue().set("user:" + ticket, JsonUtil.object2JsonStr(user));
      CookieUtil.setCookie(request, response, "userTicket", ticket);
      return RespBean.success(ticket);
   }

   @Override
   public User getByUserTicket(String userTicket, HttpServletRequest request, HttpServletResponse response) {
      if (StringUtils.isEmpty(userTicket)) {
         return null;
      }
      String userJson = (String) redisTemplate.opsForValue().get("user:" +
              userTicket);
      User user = JsonUtil.jsonStr2Object(userJson, User.class);
      if (null != user) {
         CookieUtil.setCookie(request,response,"userTicket",userTicket);
      }
      return user;
   }
   @Override
   public RespBean updatePassword(String userTicket, Long id, String password) {
    User user = userMapper.selectById(id);
    if (user == null) {
         throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
    }
    user.setPassword(MD5Util.inputPassToDbPass(password, user.getSalt()));
    int result = userMapper.updateById(user);
    if (1 == result) {
         //删除Redis
         redisTemplate.delete("user:" + userTicket);
         return RespBean.success();
    }
    return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
   }

}
