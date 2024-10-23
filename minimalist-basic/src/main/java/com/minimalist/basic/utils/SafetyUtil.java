package com.minimalist.basic.utils;

import cn.dev33.satoken.stp.StpUtil;
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
        return CommonConstant.ZERO == getLonginUserTenantId();
    }

    /**
     * 获取当前登陆人的租户ID
     * @return 租户ID
     */
    public static long getLonginUserTenantId() {
        return Optional.ofNullable(StpUtil.getSession().getString(TenantIgnore.TENANT_ID))
                .map(Long::valueOf)
                .orElse(-1L);
    }

    /**
     * 获取cookie中的租户ID
     * @return cookie中的租户ID
     */
    public static String getCookieTenantId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Cookie cookie = JakartaServletUtil.getCookie(request, TenantIgnore.TENANT_ID);
        return Optional.ofNullable(cookie).map(Cookie::getValue).orElse(null);
    }

}
