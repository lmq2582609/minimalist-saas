package com.minimalist.common.tenant;

import cn.hutool.core.util.ObjectUtil;
import com.minimalist.common.utils.SafetyUtil;
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
        //是否为 系统租户
        boolean checkAdmin = it.checkAdmin();
        if (checkAdmin) {
            //是系统租户，则忽略租户查询条件
            boolean isAdmin = SafetyUtil.checkIsAdminByTenantId();
            if (isAdmin) {
                SafetyUtil.setIgnoreTenant(true);
            } else {
                //不是系统租户，不忽略租户查询条件
                SafetyUtil.setIgnoreTenant(false);
            }
        } else {
            //不忽略租户查询条件
            SafetyUtil.setIgnoreTenant(false);
        }
        try {
            return joinPoint.proceed();
        } finally {
            //清除
            SafetyUtil.clearIgnoreTenant();
        }
    }

}
