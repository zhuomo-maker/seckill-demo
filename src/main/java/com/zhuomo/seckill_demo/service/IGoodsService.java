package com.zhuomo.seckill_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuomo.seckill_demo.pojo.Goods;
import com.zhuomo.seckill_demo.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhuomo
 * @since 2022-01-13
 */
public interface IGoodsService extends IService<Goods> {
    List<GoodsVo> findGoodsVo();
    Goods selectById(Long id);
    public GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
