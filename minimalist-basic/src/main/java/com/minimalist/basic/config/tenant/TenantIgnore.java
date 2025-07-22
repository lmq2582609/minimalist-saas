package com.minimalist.basic.config.tenant;

import java.lang.annotation.*;

/**
 * 注解加到方法上，标识此方法忽略多租户（异步方法无效）
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantIgnore {

    /** 多租户字段 */
    String TENANT_ID = "tenant_id";

    /** 租户切换，多租户字段 */
    String CHANGE_TENANT_ID = "change_tenant_id";

}
