package com.zhuomo.seckill_demo.vo;
import com.zhuomo.seckill_demo.pojo.Order;
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
public class OrderDetailVo {
   private Order order;
   private GoodsVo goodsVo; }