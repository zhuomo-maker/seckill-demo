package com.zhuomo.seckill_demo.vo;

import com.zhuomo.seckill_demo.utils.ValidatorUtil;
import com.zhuomo.seckill_demo.validator.IsMobile;
import org.springframework.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * 手机号码验证规则
 *
 * @author zhoubin
 * @since 1.0.0
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
   private boolean required = false;
   @Override
   public void initialize(IsMobile constraintAnnotation) {
      required = constraintAnnotation.required();
   }
   @Override
   public boolean isValid(String value, ConstraintValidatorContext context) {
      if (required){
         return ValidatorUtil.isMobile(value);
     }else {
         if (StringUtils.isEmpty(value)){
            return true;
         }else {
            return ValidatorUtil.isMobile(value);
         }
     }
   }
}