package com.minimalist.common.tenant;

import java.lang.annotation.*;

/**
 * 注解加到方法上，标识此方法忽略多租户（异步方法无效）
 * 注：忽略多租户的方法中，如果涉及到必须按照租户查询数据时，需要手动加入tenant_id条件
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreTenant {
}
