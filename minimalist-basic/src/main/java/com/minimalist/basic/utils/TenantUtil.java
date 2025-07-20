package com.minimalist.basic.utils;

import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import lombok.extern.slf4j.Slf4j;

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

}
