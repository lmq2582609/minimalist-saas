package com.minimalist.basic.config.mybatis;

import com.minimalist.basic.utils.TenantUtil;
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
            if (!TenantUtil.checkTenantOnOff()) {
                //未打开，忽略多租户
                return null;
            }

            //放回当前要操作的租户ID
            return new Object[]{ TenantUtil.getTenantId() };
        });

    }

}
