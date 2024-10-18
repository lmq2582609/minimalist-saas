package com.minimalist.basic.config.tenant;

import cn.hutool.core.util.ObjectUtil;
import com.mybatisflex.core.tenant.TenantManager;
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
public class TenantIgnoreAspect {

    @Around("@annotation(tenantIgnore)")
    public Object around(ProceedingJoinPoint joinPoint, TenantIgnore tenantIgnore) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        TenantIgnore it = method.getAnnotation(TenantIgnore.class);
        if (ObjectUtil.isNull(it)) {
            return joinPoint.proceed();
        }
        try {
            //忽略多租户
            TenantManager.ignoreTenantCondition();
            //执行目标方法
            return joinPoint.proceed();
        } finally {
            //恢复多租户
            TenantManager.restoreTenantCondition();
        }
    }

}
