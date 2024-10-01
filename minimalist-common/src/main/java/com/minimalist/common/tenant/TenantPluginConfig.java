package com.minimalist.common.tenant;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.module.entity.vo.config.ConfigVO;
import com.minimalist.common.module.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 多租户开关 - 默认开启
 */
@Component
public class TenantPluginConfig implements ApplicationRunner {

    @Autowired
    private ConfigService configService;

    /** 多租户开关 */
    public static boolean onOff = true;

    /** 多租户忽略的表 */
    public static Set<String> tenantIgnoreTable;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //多租户配置
        ConfigVO tenantConfig = configService.getConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_TENANT);
        if (ObjectUtil.isNotNull(tenantConfig)) {
            onOff = Boolean.TRUE.equals(Boolean.valueOf(tenantConfig.getConfigValue()));
        }
        //多租户忽略的表
        ConfigVO titConfig = configService.getConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_TENANT_IGNORE_TABLE);
        if (ObjectUtil.isNotNull(titConfig) && StrUtil.isNotBlank(titConfig.getConfigValue())) {
            tenantIgnoreTable = new HashSet<>(Arrays.asList(titConfig.getConfigValue().split(",")));
        } else {
            tenantIgnoreTable = new HashSet<>();
        }
    }

}
