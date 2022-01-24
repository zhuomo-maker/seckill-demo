package com.zhuomo.seckill_demo.config;

import com.zhuomo.seckill_demo.pojo.User;

public class UserContext {
  private static ThreadLocal<User> userHolder = new ThreadLocal<>();
  public static void setUser(User user) {
   userHolder.set(user);
  }
  public static User getUser() {
   return userHolder.get();
  }
}