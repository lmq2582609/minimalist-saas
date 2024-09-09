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
     * true时：如果有方法上加了该注解，并且设置为true，查询时会校验，如果为管理员查询全部数据，如果不是管理员，按租户查询
     * false时：如果有方法加了该注解，默认为false，查询时会跳过租户查询条件
     */
    boolean checkAdmin() default false;

    public static final String TENANT_ID = "tenant_id";

}
