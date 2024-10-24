package com.minimalist.basic.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
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
