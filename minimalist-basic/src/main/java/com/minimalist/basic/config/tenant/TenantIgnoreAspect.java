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
public class TenantIgnoreAspect {

    @Around("@annotation(tenantIgnore)")
    public Object around(ProceedingJoinPoint joinPoint, TenantIgnore tenantIgnore) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        TenantIgnore it = method.getAnnotation(TenantIgnore.class);
        if (ObjectUtil.isNull(it)) {
            return joinPoint.proceed();
        }
        boolean checkAdmin = it.checkAdmin();
        //需要校验是否为 系统租户
        if (checkAdmin) {
            //是系统租户，查询全部数据
            boolean isAdmin = SafetyUtil.checkIsSystemTenant();
            //不是系统租户，按租户查询
            SafetyUtil.setIgnoreTenant(!isAdmin);
        } else {
            //忽略租户查询条件
            SafetyUtil.setIgnoreTenant(true);
        }
        try {
            return joinPoint.proceed();
        } finally {
            //清除
            SafetyUtil.clearIgnoreTenant();
        }
    }

}
