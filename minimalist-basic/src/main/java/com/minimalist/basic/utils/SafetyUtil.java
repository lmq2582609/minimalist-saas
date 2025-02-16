package com.minimalist.basic.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.minimalist.basic.config.tenant.TenantIgnore;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Optional;

/**
 * 安全校验
 */
public class SafetyUtil {

    /**
     * 检查是否为系统租户，系统租户ID = 0
     * @return 是/否
     */
    public static boolean checkIsSystemTenant() {
        return CommonConstant.ZERO == getLoginUserTenantId();
    }

    /**
     * 获取当前登陆人的租户ID
     * @return 租户ID
     */
    public static long getLoginUserTenantId() {
        return Optional.ofNullable(StpUtil.getSession().getString(TenantIgnore.TENANT_ID))
                .map(Long::valueOf)
                .orElse(-1L);
    }

    /**
     * 切换租户数据源
     * @param name 数据源名称。为空时切换到当前登录人租户数据源
     * @param forceChange 是否强制切换。
     *                    true：name不为空按照name切换，name为空按照租户切换
     *                    false：name不为空按照name切换，name为空时若不是系统租户则切换，是系统租户则不切换
     */
    public static void changeTenantDatasource(String name, boolean forceChange) {
        //如果指定了数据源名称，直接切换
        if (StrUtil.isNotBlank(name)) {
            DynamicDataSourceContextHolder.push(name);
            return;
        }
        //从session中获取租户ID
        String tenantId = StpUtil.getSession().getString(TenantIgnore.TENANT_ID);
        //强制切换
        if (forceChange && StrUtil.isNotBlank(tenantId)) {
            DynamicDataSourceContextHolder.push(tenantId);
            return;
        }
        //非强制切换
        if (!forceChange) {
            //不是系统租户时，切换数据源
            if (StrUtil.isNotBlank(tenantId) && !checkIsSystemTenant()) {
                DynamicDataSourceContextHolder.push(tenantId);
            }
        }
    }

    /**
     * 获取cookie中的租户ID
     * @return cookie中的租户ID
     */
    public static Long getCookieTenantId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Cookie cookie = JakartaServletUtil.getCookie(request, TenantIgnore.TENANT_ID);
        return Optional.ofNullable(cookie).map(Cookie::getValue).filter(StrUtil::isNotBlank).map(Long::valueOf).orElse(null);
    }

    /**
     * 获取操作的租户ID
     * 因为管理员可能操作其他租户的数据
     * @return 租户ID
     */
    public static Long getOperationTenantId() {
        Long cookieTenantId = getCookieTenantId();
        if (ObjectUtil.isNotNull(cookieTenantId)) {
            return cookieTenantId;
        }
        return getLoginUserTenantId();
    }

}
