package com.minimalist.basic.config.mybatis;

import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.SafetyUtil;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.tenant.TenantManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisFlexConfiguration {

    private static final Logger logger = LoggerFactory.getLogger("mybatis-flex-sql");

    public MyBatisFlexConfiguration() {
        //开启审计功能
        AuditManager.setAuditEnable(true);
        //设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage ->
                logger.info("{},{} ms", auditMessage.getFullSql(), auditMessage.getElapsedTime())
        );

        //获取租户ID，目前支持返回一个租户ID
        TenantManager.setTenantFactory(() -> {
            //校验系统多租户是否开启
            ConfigVO configVO = CommonConstant.systemConfigMap.get(CommonConstant.SYSTEM_CONFIG_TENANT);
            if (ObjectUtil.isNotNull(configVO)) {
                Boolean tenantOnOff = Boolean.valueOf(configVO.getConfigValue());
                //忽略多租户
                if (Boolean.FALSE.equals(tenantOnOff)) {
                    //未打开，忽略多租户
                    return null;
                }
            }

            //项目中后台管理端是将token存储在cookie中
            //如果是系统租户，并且cookie中携带其他租户ID参数，表示查询其他租户数据
            if (SafetyUtil.checkIsSystemTenant()) {
                Long cookieTenantId = SafetyUtil.getCookieTenantId();
                if (ObjectUtil.isNotNull(cookieTenantId)) {
                    //返回cookie中的租户ID
                    return new Object[]{ cookieTenantId };
                }
            }
            //当前登陆人的租户ID
            long loginUserTenantId = SafetyUtil.getLoginUserTenantId();
            if (loginUserTenantId != -1) {
                //返回当前登陆人的租户ID
                return new Object[]{ loginUserTenantId };
            }

            //项目中其他端，可能将租户ID存储在header中
            long tenantId = SafetyUtil.getTenantIdByHeader();
            return new Object[]{ tenantId };
        });

    }

}
