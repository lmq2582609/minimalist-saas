package com.minimalist.basic.mapper;

import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MTenantPackagePerm;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 租户套餐与权限关联表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MTenantPackagePermMapper extends BaseMapper<MTenantPackagePerm> {

    /**
     * 根据租户套餐ID查询租户套餐与权限关联数据
     * @param tenantPackageId 租户套餐ID
     * @return 租户套餐与权限关联数据
     */
    default List<MTenantPackagePerm> selectTenantPackagePermByTenantPackageId(Long tenantPackageId) {
        return selectListByQuery(QueryWrapper.create().eq(MTenantPackagePerm::getPackageId, tenantPackageId));
    }

}
