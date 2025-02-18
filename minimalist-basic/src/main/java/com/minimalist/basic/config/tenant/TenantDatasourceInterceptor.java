package com.minimalist.basic.config.tenant;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.minimalist.basic.utils.SafetyUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户数据源切换拦截器
 */
@Component
public class TenantDatasourceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //切换到租户数据源
        String tenantId = StpUtil.getSession().getString(TenantIgnore.TENANT_ID);
        //不是系统租户时，切换数据源
        if (StrUtil.isNotBlank(tenantId) && !SafetyUtil.checkIsSystemTenant()) {
            DynamicDataSourceContextHolder.push(tenantId);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //清空线程内数据源信息
        DynamicDataSourceContextHolder.clear();
    }

}
