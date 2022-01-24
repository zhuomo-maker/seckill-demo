package com.zhuomo.seckill_demo.vo;
import com.zhuomo.seckill_demo.validator.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 登录入参
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo {
   @NotNull
   @IsMobile
   private String mobile;
   @NotNull
   @Length(min = 32)
   private String password;
}