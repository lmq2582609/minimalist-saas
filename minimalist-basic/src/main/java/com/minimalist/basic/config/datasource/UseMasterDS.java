package com.minimalist.basic.config.datasource;

import java.lang.annotation.*;

/**
 * 标记此方法或类强制使用主数据源
 * 用于系统级数据查询：字典、配置、通知、存储等
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UseMasterDS {
}
