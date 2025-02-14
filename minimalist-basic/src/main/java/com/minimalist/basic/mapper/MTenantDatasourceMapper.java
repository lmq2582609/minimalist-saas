package com.minimalist.basic.mapper;

import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MTenantDatasource;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * 租户数据源表 映射层。
 *
 * @author asus
 * @since 2025-02-14
 */
public interface MTenantDatasourceMapper extends BaseMapper<MTenantDatasource> {

    /**
     * 根据租户ID删除数据源信息
     * @param tenantId 租户ID
     */
    default void deleteTenantDatasourceByTenantId(Long tenantId) {
        deleteByQuery(QueryWrapper.create().eq(MTenantDatasource::getTenantId, tenantId));
    }

    /**
     * 根据租户ID查询数据源信息
     * @param tenantId 租户ID
     * @return 数据源信息
     */
    default MTenantDatasource selectTenantDatasourceByTenantId(Long tenantId) {
        return selectOneByQuery(QueryWrapper.create().eq(MTenantDatasource::getTenantId, tenantId));
    }

}
