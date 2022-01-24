package com.zhuomo.seckill_demo;

import com.zhuomo.seckill_demo.mapper.GoodsMapper;
import com.zhuomo.seckill_demo.mapper.UserMapper;
import com.zhuomo.seckill_demo.service.IGoodsService;
import com.zhuomo.seckill_demo.service.impl.GoodsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillDemoApplicationTests {
    @Autowired
    IGoodsService goodsService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedisScript<Long> script;

    @Test
    void contextLoads() {
        goodsService.selectById(new Long(1));
    }
    @Test
    void testVo(){
        goodsMapper.findGoodsVo();
    }
    @Test
    void testGoods(){
        goodsMapper.findGoodsVoByGoodsId(new Long(1));
    }
    @Test
    public void testLock03(){
   ValueOperations valueOperations = redisTemplate.opsForValue();
   String value = UUID.randomUUID().toString();
   //给锁添加一个过期时间，防止应用在运行过程中抛出异常导致锁无法及时得到释放
   Boolean isLock = valueOperations.setIfAbsent("k1",value,5, TimeUnit.SECONDS);
   //没人占位
   if (isLock){
      valueOperations.set("name","xxxx");
      String name = (String) valueOperations.get("name");
      System.out.println(name);
      System.out.println(valueOperations.get("k1"));
      //释放锁
      Boolean result = (Boolean) redisTemplate.execute(script,
                    Collections.singletonList("k1"), value);
      System.out.println(result);
   }else {
      //有人占位，停止/暂缓 操作
      System.out.println("有线程在使用，请稍后");
   }
    }
    @Test
    public void scriptTest(){
        Long stock = (Long) redisTemplate.execute(script,
                Collections.singletonList("seckillGoods:" + new Long("1")), Collections.EMPTY_LIST);
    }


}
