package com.zhuomo.seckill_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuomo.seckill_demo.pojo.Goods;
import com.zhuomo.seckill_demo.pojo.User;
import com.zhuomo.seckill_demo.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhuomo
 * @since 2022-01-13
 */
public interface GoodsMapper extends BaseMapper<Goods> {
    List<GoodsVo> findGoodsVo();
    List<GoodsVo> findGoods();
    User testUser(String id);
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
