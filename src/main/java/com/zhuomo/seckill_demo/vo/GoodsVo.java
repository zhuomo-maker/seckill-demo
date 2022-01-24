package com.zhuomo.seckill_demo.vo;
import com.zhuomo.seckill_demo.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 商品返回对象
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo extends Goods{


  private BigDecimal seckillPrice;

  /**
   * 商品库存
   */
  private Integer stockCount;
  private Date startDate;

  private Date endDate;

}