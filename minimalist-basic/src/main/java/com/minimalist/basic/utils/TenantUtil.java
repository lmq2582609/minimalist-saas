package com.minimalist.basic.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.tenant.TenantIgnore;
import com.minimalist.basic.entity.enums.UserEnum;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public class TenantUtil {

    /**
     * 校验系统是否开启多租户
     * @return true开启，false关闭
     */
    public static boolean checkTenantOnOff() {
        //校验系统多租户是否开启
        ConfigVO configVO = CommonConstant.systemConfigMap.get(CommonConstant.SYSTEM_CONFIG_TENANT);
        if (ObjectUtil.isNull(configVO)) {
            log.warn("校验系统多租户开关，获取CommonConstant.systemConfigMap缓存为空，请检查！！！");
            return true;
        }
        return Boolean.parseBoolean(configVO.getConfigValue());
    }

    /**
     * 获取要操作的租户ID
     * @return 租户ID
     */
    public static long getTenantId() {
        //1. 从header中获取租户ID，获取到直接返回
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String tenantId = JakartaServletUtil.getHeader(request, TenantIgnore.TENANT_ID, StandardCharsets.UTF_8);
        if (StrUtil.isNotBlank(tenantId)) {
            return Long.parseLong(tenantId);
        }

        //2. 从当前登陆人身上获取租户ID
        long loginUserTenantId = getLoginUserTenantId();
        if (loginUserTenantId == -1) {
            //未登录，抛出异常
            throw new BusinessException(UserEnum.ErrorMsg.AUTH_EXPIRED.getDesc());
        }

        //3. 校验当前登陆人是否为系统租户
        boolean isSystemTenant = CommonConstant.ZERO == loginUserTenantId;
        if (isSystemTenant) {
            //是系统租户，校验是查系统数据，还是查其他租户数据
            //前端在租户切换后，会将切换的租户ID放到cookie中
            Long changeTenantId = TenantUtil.getCookieChangeTenantId();
            if (ObjectUtil.isNull(changeTenantId)) {
                //changeTenantId为空，表示租户未进行切换，直接返回系统租户ID
                return loginUserTenantId;
            }

            //租户切换，返回切换后的租户ID，这表示虽然当前登录的是系统租户，但系统租户下要查询其他租户的数据
            return changeTenantId;
        } else {
            //非系统租户，直接返回
            return loginUserTenantId;
        }
    }

    /**
     * 检查是否要查询其他租户数据
     * 前端在租户切换后，会将切换的租户ID放到cookie中
     * @return true是，false否
     */
    public static boolean checkQueryTenantData() {
        return TenantUtil.getCookieChangeTenantId() != null;
    }


    /**
     * 检查是否为系统租户，系统租户ID = 0
     * @return 是/否
     */
    public static boolean checkIsSystemTenant() {
        return CommonConstant.ZERO == getLoginUserTenantId();
    }

    /**
     * 获取当前登陆人的租户ID
     * 当前登陆人的租户ID是在登录系统后通过 `StpUtil.getSession().set(TenantIgnore.TENANT_ID, tenantId);` 设置进去的
     * 所以取的时候，还需要从 session 中取
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
     * 获取cookie中，租户切换的租户ID
     * @return cookie中租户切换的租户ID
     */
    public static Long getCookieChangeTenantId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            Cookie cookie = JakartaServletUtil.getCookie(request, TenantIgnore.CHANGE_TENANT_ID);
            return Optional.ofNullable(cookie).map(Cookie::getValue).filter(StrUtil::isNotBlank).map(Long::valueOf).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

}
