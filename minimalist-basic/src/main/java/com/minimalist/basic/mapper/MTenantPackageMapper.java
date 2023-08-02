package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.config.mybatis.QueryCondition;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.po.MTenantPackage;
import com.minimalist.basic.entity.vo.tenant.TenantPackageQueryVO;

import java.util.List;

/**
 * <p>
 * 租户套餐表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-07-04
 */
public interface MTenantPackageMapper extends BaseMapper<MTenantPackage> {

    /**
     * 根据租户套餐ID查询租户套餐
     * @param tenantPackageId 租户套餐ID
     * @return 租户套餐实体
     */
    default MTenantPackage selectTenantPackageByTenantPackageId(Long tenantPackageId) {
        return selectOne(new LambdaQueryWrapper<MTenantPackage>().eq(MTenantPackage::getPackageId, tenantPackageId));
    }

    /**
     * 根据租户套餐ID删除租户套餐
     * @param tenantPackageId 租户套餐ID
     * @return 受影响行数
     */
    default long deleteTenantPackageByTenantPackageId(Long tenantPackageId) {
        return delete(new LambdaQueryWrapper<MTenantPackage>().eq(MTenantPackage::getPackageId, tenantPackageId));
    }

    /**
     * 根据租户套餐ID修改租户套餐
     * @param tenantPackage 租户套餐实体
     * @return 受影响行数
     */
    default int updateTenantPackageByTenantPackageId(MTenantPackage tenantPackage) {
        return update(tenantPackage,
                new LambdaUpdateWrapper<MTenantPackage>()
                        .eq(MTenantPackage::getPackageId, tenantPackage.getPackageId()));
    }

    /**
     * 查询租户套餐(分页)
     * @param queryVO 查询条件
     * @return 租户套餐分页数据
     */
    default Page<MTenantPackage> selectPageTenantPackageList(TenantPackageQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MTenantPackage>()
                        .likeNotNull(MTenantPackage::getPackageName, queryVO.getPackageName())
                        .eqNotNull(MTenantPackage::getStatus, queryVO.getStatus())
                );
    }

    /**
     * 查询全部租户套餐 -> 字典查询
     * @return 用户列表
     */
    default List<MTenantPackage> selectTenantPackageDict() {
        return selectList(new LambdaQueryWrapper<MTenantPackage>().eq(MTenantPackage::getStatus, TenantEnum.TenantPackageStatus.DEPT_STATUS_1.getCode()));
    }

}
