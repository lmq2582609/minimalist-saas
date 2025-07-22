package com.minimalist.basic.config.tenant;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.utils.CommonConstant;
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

        //获取要操作的租户ID
        long tenantId = TenantUtil.getTenantId();

        //如果是系统租户，使用主数据源
        if (CommonConstant.ZERO == tenantId) {
            log.info("切换数据源，[系统租户] => 继续使用主数据源，租户ID：{}", tenantId);
        } else {
            //如果非系统租户，检查是否需要切换到租户数据源
            TenantVO tenantVO = CommonConstant.tenantMap.get(tenantId);
            //该租户数据隔离方式 != master，表示需要切换到租户自己的数据源
            if (ObjectUtil.isNotNull(tenantVO) && !TenantEnum.MASTER.equals(tenantVO.getDatasource())) {
                log.info("切换数据源，[其他租户] => 切换到租户数据源，租户ID：{}", tenantId);
                DynamicDataSourceContextHolder.push(String.valueOf(tenantId));
            }
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
