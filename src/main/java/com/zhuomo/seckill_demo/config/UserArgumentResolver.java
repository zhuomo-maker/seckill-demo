package com.zhuomo.seckill_demo.config;

import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.service.IUserService;
import com.zhuomo.seckill_demo.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @author zhoubin
 * @since 1.0.0
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private IUserService userService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer
            mavContainer,
               NativeWebRequest webRequest, WebDataBinderFactory
                                          binderFactory) throws Exception {
  return UserContext.getUser();
    }

}