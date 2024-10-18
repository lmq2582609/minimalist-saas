package com.minimalist.basic.config.tenant;

import cn.hutool.core.util.StrUtil;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.basic.service.ConfigService;
import com.minimalist.basic.utils.CommonConstant;
import com.mybatisflex.core.tenant.TenantManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * 多租户配置处理
 */
@Order(-1)
@Component
public class TenantInit {

    @Autowired
    private ApplicationContext applicationContext;

    /** 配置处理 */
    private static ConfigService configService;

    @PostConstruct
    public void init() {
        configService = applicationContext.getBean(ConfigService.class);
        //此处忽略多租户 - 刷新配置缓存
        TenantManager.withoutTenantCondition(() -> configService.refreshConfigCache());
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

}
