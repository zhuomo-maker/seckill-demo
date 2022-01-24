package com.zhuomo.seckill_demo.rabbitmq;

import com.zhuomo.seckill_demo.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author zhoubin
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {
  private User user;
  private Long goodsId; }