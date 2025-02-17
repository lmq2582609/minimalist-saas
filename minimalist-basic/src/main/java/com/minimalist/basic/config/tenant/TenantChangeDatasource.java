package com.minimalist.basic.config.tenant;

import java.lang.annotation.*;

/**
 * 注解加到方法上，标识此方法需要切换到当前登录用户所在租户的数据源
 * 如果是系统租户，则该注解无效，不切换
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantChangeDatasource {
}
