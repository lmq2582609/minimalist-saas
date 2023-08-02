package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalist.basic.entity.po.MTenantPackagePerm;

import java.util.List;

/**
 * <p>
 * 租户套餐与权限关联表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-07-04
 */
public interface MTenantPackagePermMapper extends BaseMapper<MTenantPackagePerm> {

    /**
     * 根据租户套餐ID查询租户套餐与权限关联数据
     * @param tenantPackageId 租户套餐ID
     * @return 租户套餐与权限关联数据
     */
    default List<MTenantPackagePerm> selectTenantPackagePermByTenantPackageId(Long tenantPackageId) {
        return selectList(new LambdaQueryWrapper<MTenantPackagePerm>().eq(MTenantPackagePerm::getPackageId, tenantPackageId));
    }

}
