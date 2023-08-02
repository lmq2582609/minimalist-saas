package com.minimalist.basic.config.tenant;

import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.utils.SafetyUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

/**
 * 忽略多租户处理
 */
@Slf4j
@Aspect
@Component
public class IgnoreTenantAspect {

    @Around("@annotation(ignoreTenant)")
    public Object around(ProceedingJoinPoint joinPoint, IgnoreTenant ignoreTenant) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        IgnoreTenant it = method.getAnnotation(IgnoreTenant.class);
        if (ObjectUtil.isNull(it)) {
            return joinPoint.proceed();
        }
        //有@IgnoreTenant注解，忽略多租户
        SafetyUtil.setIgnoreTenant(true);
        try {
            return joinPoint.proceed();
        } finally {
            //清除忽略多租户
            SafetyUtil.clearIgnoreTenant();
        }
    }

}
