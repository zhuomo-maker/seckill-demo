package com.zhuomo.seckill_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuomo.seckill_demo.pojo.User;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhuomo
 * @since 2022-01-09
 */
public interface UserMapper extends BaseMapper<User> {
    User testUser(String id);
}
