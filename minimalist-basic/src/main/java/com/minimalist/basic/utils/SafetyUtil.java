package com.minimalist.basic.utils;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.tenant.TenantIgnore;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
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
        try {
            return Optional.ofNullable(StpUtil.getSession().getString(TenantIgnore.TENANT_ID))
                    .map(Long::valueOf)
                    .orElse(-1L);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取当前登录人租户ID，租户ID为空会抛出异常
     * 可以返回字符串的租户ID或者Long类型的租户ID
     * @return 租户ID
     */
    public static <T> T getLoginUserTenantIdThrowException(Class<T> clazz) {
        String tenantId = StpUtil.getSession().getString(TenantIgnore.TENANT_ID);
        if (StrUtil.isBlank(tenantId)) {
            throw new BusinessException("获取租户ID为空，请检查");
        }
        if (clazz == String.class) {
            return clazz.cast(tenantId);
        }
        if (clazz == Long.class || clazz == long.class) {
            return clazz.cast(Long.parseLong(tenantId));
        }
        return null;
    }

    /**
     * 获取cookie中的租户ID
     * @return cookie中的租户ID
     */
    public static Long getCookieTenantId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            Cookie cookie = JakartaServletUtil.getCookie(request, TenantIgnore.TENANT_ID);
            return Optional.ofNullable(cookie).map(Cookie::getValue).filter(StrUtil::isNotBlank).map(Long::valueOf).orElse(null);
        } catch (Exception e) {
            return null;
        }
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

    /**
     * 从header中获取租户ID
     * @return 租户ID
     */
    public static long getTenantIdByHeader() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String tenantId = JakartaServletUtil.getHeader(request, TenantIgnore.TENANT_ID, StandardCharsets.UTF_8);
            return Optional.ofNullable(tenantId)
                    .map(Long::valueOf)
                    .orElse(-1L);
        } catch (Exception e) {
            return -1;
        }
    }

}
