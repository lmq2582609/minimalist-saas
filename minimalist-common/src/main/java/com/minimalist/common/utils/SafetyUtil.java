package com.minimalist.common.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.tenant.IgnoreTenant;

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
     * 根据租户ID校验是否为管理员
     * @return true管理员 false租户
     */
    public static boolean checkIsAdminByTenantId() {
        Long tenantId = Optional.ofNullable(StpUtil.getSession().getString(IgnoreTenant.TENANT_ID))
                .map(Long::valueOf)
                .orElse(-1L);
        return CommonConstant.ZERO == tenantId.intValue();
    }

}
