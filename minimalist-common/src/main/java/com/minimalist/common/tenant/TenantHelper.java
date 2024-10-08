package com.minimalist.common.tenant;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.module.entity.vo.config.ConfigVO;
import com.minimalist.common.module.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 多租户配置处理
 */
@Component
public class TenantHelper implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    /** 配置处理 */
    private static ConfigService configService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        configService = applicationContext.getBean(ConfigService.class);
    }

    /**
     * 获取多租户开关
     * @return true打开，false关闭
     */
    public static boolean getTenantOnOff() {
        ConfigVO tenantConfig = configService.getConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_TENANT);
        Boolean onOff = Optional.ofNullable(tenantConfig)
                .filter(c -> StrUtil.isNotBlank(c.getConfigValue()))
                .map(c -> Boolean.valueOf(c.getConfigValue()))
                .orElse(false);
        return Boolean.TRUE.equals(onOff);
    }

    /**
     * 获取多租户忽略的表
     * @return set集合
     */
    public static Set<String> getTenantIgnoreTable() {
        ConfigVO titConfig = configService.getConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_TENANT_IGNORE_TABLE);
        if (ObjectUtil.isNotNull(titConfig) && StrUtil.isNotBlank(titConfig.getConfigValue())) {
            return new HashSet<>(Arrays.asList(titConfig.getConfigValue().split(",")));
        }
        return new HashSet<>();
    }

}
