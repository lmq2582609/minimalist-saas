package com.minimalist.basic.config.tenant;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.TenantUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户数据源切换拦截器
 * 根据当前用户的 tenant_id 动态切换数据源
 * tenant_id = 0 → 主库；tenant_id ≠ 0 → 租户数据源
 */
@Slf4j
public class TenantDatasourceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //校验系统多租户是否开启
        if (!TenantUtil.checkTenantOnOff()) {
            //未打开，忽略多租户，继续使用主数据源
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        //获取要操作的租户ID
        long tenantId = TenantUtil.getTenantId();

        //非系统租户，切换到租户数据源
        if (CommonConstant.ZERO != tenantId) {
            log.debug("切换数据源 => 租户数据源，租户ID：{}", tenantId);
            DynamicDataSourceContextHolder.push(String.valueOf(tenantId));
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //清空线程内数据源信息
        DynamicDataSourceContextHolder.clear();
    }

}
