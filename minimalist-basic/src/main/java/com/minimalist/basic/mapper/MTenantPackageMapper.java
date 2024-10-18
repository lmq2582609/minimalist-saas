package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.vo.tenant.TenantPackageQueryVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MTenantPackage;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 租户套餐表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MTenantPackageMapper extends BaseMapper<MTenantPackage> {

    /**
     * 根据租户套餐ID查询租户套餐
     * @param tenantPackageId 租户套餐ID
     * @return 租户套餐实体
     */
    default MTenantPackage selectTenantPackageByTenantPackageId(Long tenantPackageId) {
        return selectOneByQuery(QueryWrapper.create().eq(MTenantPackage::getPackageId, tenantPackageId));
    }

    /**
     * 根据租户套餐ID删除租户套餐
     * @param tenantPackageId 租户套餐ID
     */
    default void deleteTenantPackageByTenantPackageId(Long tenantPackageId) {
        deleteByQuery(QueryWrapper.create().eq(MTenantPackage::getPackageId, tenantPackageId));
    }

    /**
     * 根据租户套餐ID修改租户套餐
     * @param tenantPackage 租户套餐实体
     */
    default void updateTenantPackageByTenantPackageId(MTenantPackage tenantPackage) {
        updateByQuery(tenantPackage, QueryWrapper.create().eq(MTenantPackage::getPackageId, tenantPackage.getPackageId()));
    }

    /**
     * 查询租户套餐(分页)
     * @param queryVO 查询条件
     * @return 租户套餐分页数据
     */
    default Page<MTenantPackage> selectPageTenantPackageList(TenantPackageQueryVO queryVO) {
        return paginate(queryVO.getPageNum(), queryVO.getPageSize(),
                QueryWrapper.create()
                        .eq(MTenantPackage::getStatus, queryVO.getStatus())
                        .like(MTenantPackage::getPackageName, queryVO.getPackageName())
        );
    }

    /**
     * 查询全部租户套餐 -> 字典查询
     * @return 用户列表
     */
    default List<MTenantPackage> selectTenantPackageDict() {
        return selectListByQuery(QueryWrapper.create().eq(MTenantPackage::getStatus, StatusEnum.STATUS_1.getCode()));
    }

}
