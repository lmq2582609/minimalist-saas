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

    /**
     * 是否做管理员校验，默认false
     * true时：如果有方法上加了该注解，并且设置为true，查询时不会添加租户查询条件(tenant_id = ?)
     * false时：如果有方法加了该注解，不设置默认为false，查询时添加租户查询条件(tenant_id = ?)
     */
    boolean checkAdmin() default false;

}
