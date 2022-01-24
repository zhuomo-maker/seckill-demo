package com.zhuomo.seckill_demo.exception;

import com.zhuomo.seckill_demo.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 全局异常
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException {
   private RespBeanEnum respBeanEnum; }
