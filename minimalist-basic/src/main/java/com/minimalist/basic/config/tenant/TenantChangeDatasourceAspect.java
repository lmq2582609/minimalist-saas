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

@Slf4j
@Aspect
@Component
public class TenantChangeDatasourceAspect {

    @Around("@annotation(changeDatasource)")
    public Object around(ProceedingJoinPoint joinPoint, TenantChangeDatasource changeDatasource) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        TenantChangeDatasource tcd = method.getAnnotation(TenantChangeDatasource.class);
        if (ObjectUtil.isNull(tcd)) {
            return joinPoint.proceed();
        }
        //切换租户数据源
        SafetyUtil.changeTenantDatasource(null, false);
        //执行目标方法
        return joinPoint.proceed();
    }

}
