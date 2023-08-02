package com.minimalist.basic.utils;

import cn.hutool.core.lang.Assert;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.entity.constant.CommonConstant;
import com.minimalist.basic.entity.enums.RespEnum;
import com.minimalist.basic.entity.vo.user.UserInfoVO;

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
     * 检查租户ID是否与登录人的租户ID一致
     * @param tenantId 租户ID
     */
    public static void checkTenantIdIsTamperWithData(Long tenantId) {
        UserInfoVO userVO = SpringSecurityUtil.getUserVO();
        Assert.notNull(userVO, () -> new BusinessException(RespEnum.TAMPER_WITH_DATA.getDesc()));
        Assert.isTrue(tenantId.equals(userVO.getTenantId()), () -> new BusinessException(RespEnum.TAMPER_WITH_DATA.getDesc()));
    }

    /**
     * 根据租户ID校验是否为管理员
     * @return true管理员 false租户
     */
    public static boolean checkIsAdminByTenantId() {
        return CommonConstant.ZERO == SpringSecurityUtil.getTenantId().intValue();
    }

    /**
     * 检查用户ID是否与登陆人的用户ID一致
     * @param userId 用户ID
     * @return true一致，false不一致
     */
    public static boolean checkUserId(Long userId) {
        return SpringSecurityUtil.getUserId().equals(userId);
    }
}
