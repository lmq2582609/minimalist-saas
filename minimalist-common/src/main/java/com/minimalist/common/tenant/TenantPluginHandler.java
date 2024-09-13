package com.minimalist.common.tenant;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.utils.SafetyUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.schema.Column;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * 多租户插件
 */
@Configuration
public class TenantPluginHandler implements TenantLineHandler {

    /**
     * 获取租户 ID 值表达式，只支持单个 ID 值
     * @return 租户 ID 值表达式
     */
    @Override
    public Expression getTenantId() {
        return new LongValue(SafetyUtil.getLonginUserTenantId());
    }

    /**
     * 获取租户字段名
     * 默认字段名叫: tenant_id
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return IgnoreTenant.TENANT_ID;
    }

    /**
     * 根据表名判断是否忽略拼接多租户条件
     * 默认都要进行解析并拼接多租户条件
     * @param tableName 表名
     * @return 是否忽略, true:表示忽略，false:需要解析并拼接多租户条件
     */
    @Override
    public boolean ignoreTable(String tableName) {
        //多租户忽略
        if (SafetyUtil.checkIgnoreTenant()) {
            return true;
        }
        //多租户开启
        if (TenantPluginConfig.onOff) {
            //如果是系统用户，则忽略，用户ID <= 0 表示系统用户
            if (StpUtil.getLoginIdAsLong() <= CommonConstant.ZERO) {
                return true;
            }
            //某张表忽略
            return TenantPluginConfig.tenantIgnoreTable.contains(tableName);
        }
        //多租户未开启，忽略
        return true;
    }

    /**
     * 插入数据时，是否插入tenant_id字段，插入时tenant_id字段的值来自于 getTenantId()
     * @param columns 租户字段名，一般是 tenant_id
     * @param tenantIdColumn 插入时表中所有列名
     * @return true插入时不自动填充tenant_id，false将 getTenantId() 获取到的租户ID自动填充
     */
    @Override
    public boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
        //多租户开启
        if (TenantPluginConfig.onOff) {
            return SafetyUtil.checkIgnoreTenant() || TenantLineHandler.super.ignoreInsert(columns, tenantIdColumn);
        }
        //多租户未开启，忽略
        return true;
    }

}
