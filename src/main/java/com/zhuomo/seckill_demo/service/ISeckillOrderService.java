package com.zhuomo.seckill_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuomo.seckill_demo.pojo.SeckillOrder;
import com.zhuomo.seckill_demo.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhuomo
 * @since 2022-01-13
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {
    Long getResult(User user, Long goodsId);
}
