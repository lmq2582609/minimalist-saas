package com.minimalist.basic.config.tenant;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.minimalist.basic.utils.SafetyUtil;
import com.minimalist.basic.utils.TenantUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户数据源切换拦截器
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

        String requestURI = request.getRequestURI();
        log.info("切换数据源，访问非/basic接口，经过多数据源拦截器, 当前路径是：{}", requestURI);
        //切换到租户数据源
        String tenantId = StpUtil.getSession().getString(TenantIgnore.TENANT_ID);
        if (StrUtil.isBlank(tenantId)) {
            log.info("切换数据源，从session中未找到租户ID，继续从header中寻找。");
            //如果从session中未找到tenantId，继续从header中寻找
            long tid = SafetyUtil.getTenantIdByHeader();
            log.info("切换数据源，从header中拿到的租户ID：{}", tid);
            if (tid == -1) {
                //继续使用主数据源
                return HandlerInterceptor.super.preHandle(request, response, handler);
            } else {
                tenantId = String.valueOf(tid);
            }
        }

        //不是系统租户时，切换数据源
        if (StrUtil.isNotBlank(tenantId) && !SafetyUtil.checkIsSystemTenant()) {
            log.info("切换数据源，租户ID：{}", tenantId);
            DynamicDataSourceContextHolder.push(tenantId);
        } else {
            log.info("切换数据源，继续使用主数据源，租户ID：{}", tenantId);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //清空线程内数据源信息
        DynamicDataSourceContextHolder.clear();
        log.info("切换数据源，清空数据源");
    }

}
