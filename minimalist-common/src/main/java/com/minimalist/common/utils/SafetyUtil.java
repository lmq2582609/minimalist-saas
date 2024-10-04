package com.minimalist.common.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.tenant.IgnoreTenant;
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
     * 忽略多租户本地变量
     */
    private static final TransmittableThreadLocal<Boolean> IGNORE_TENANT = new TransmittableThreadLocal<>();

    /**
     * 设置是否忽略多租户
     */
    public static void setIgnoreTenant(boolean ignoreTenant) {
        IGNORE_TENANT.set(ignoreTenant);
    }

    /**
     * 清除是否忽略多租户
     */
    public static void clearIgnoreTenant() {
        IGNORE_TENANT.remove();
    }

    /**
     * 检查是否忽略多租户
     * @return true忽略，false不忽略
     */
    public static boolean checkIgnoreTenant() {
        return Boolean.TRUE.equals(IGNORE_TENANT.get());
    }

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
        return Optional.ofNullable(StpUtil.getSession().getString(IgnoreTenant.TENANT_ID))
                .map(Long::valueOf)
                .orElse(-1L);
    }

    /**
     * 获取cookie中的租户ID
     * @return cookie中的租户ID
     */
    public static String getCookieTenantId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Cookie cookie = JakartaServletUtil.getCookie(request, IgnoreTenant.TENANT_ID);
        return Optional.ofNullable(cookie).map(Cookie::getValue).orElse(null);
    }

}
