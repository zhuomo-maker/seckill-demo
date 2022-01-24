package com.zhuomo.seckill_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuomo.seckill_demo.pojo.Order;
import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.vo.GoodsVo;
import com.zhuomo.seckill_demo.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhuomo
 * @since 2022-01-13
 */
public interface IOrderService extends IService<Order> {
    Order seckill(User user, GoodsVo goods);
    OrderDetailVo detail(Long orderId);
    /**
     * 验证秒杀地址
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkPath(User user, Long goodsId, String path);
    /**
     * 生成秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
