package com.zhuomo.seckill_demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuomo.seckill_demo.mapper.GoodsMapper;
import com.zhuomo.seckill_demo.pojo.Goods;
import com.zhuomo.seckill_demo.service.IGoodsService;
import com.zhuomo.seckill_demo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 获取商品列表
     * @return
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    @Override
    public Goods selectById(Long id) {
        return goodsMapper.selectById(id);
    }
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
  return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }

}