package com.minimalist.basic.config.datasource;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 强制主数据源切面
 * 配合 @UseMasterDS 注解使用（支持类级别和方法级别），确保系统级数据查询走主库
 */
@Slf4j
@Aspect
@Component
@Order(-1)
public class UseMasterDSAspect {

    @Around("@within(useMasterDS) || @annotation(useMasterDS)")
    public Object around(ProceedingJoinPoint joinPoint, UseMasterDS useMasterDS) throws Throwable {
        try {
            DynamicDataSourceContextHolder.push("master");
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

}
