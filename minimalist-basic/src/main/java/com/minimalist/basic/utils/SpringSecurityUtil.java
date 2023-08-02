package com.minimalist.basic.utils;

import cn.hutool.core.lang.Assert;
import com.minimalist.basic.entity.bo.SecurityUserDetails;
import com.minimalist.basic.entity.enums.RespEnum;
import com.minimalist.basic.entity.vo.user.UserInfoVO;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityUtil {

    /**
     * 获取用户ID
     * @return userId
     */
    public static Long getUserId() {
        return getSecurityUserDetails().getUser().getUserId();
    }

    /**
     * 获取租户ID
     * @return tenantId
     */
    public static Long getTenantId() {
        return getSecurityUserDetails().getUser().getTenantId();
    }

    /**
     * 获取用户信息
     * @return 用户实体
     */
    public static UserInfoVO getUserVO() {
        return getSecurityUserDetails().getUser();
    }

    /**
     * 获取 SecurityUserDetails
     * @return SecurityUserDetails
     */
    private static SecurityUserDetails getSecurityUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) authentication.getPrincipal();
        Assert.notNull(securityUserDetails, () -> new BadCredentialsException(RespEnum.REQUEST_UNAUTH.getDesc()));
        return securityUserDetails;
    }

}
