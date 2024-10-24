package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.vo.tenant.TenantQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MTenant;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 租户表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MTenantMapper extends BaseMapper<MTenant> {
    /**
     * 根据租户套餐ID查询租户数量
     * @param tenantPackageId 租户套餐ID
     * @return 租户数量
     */
    default long selectTenantCountByTenantPackageId(Long tenantPackageId) {
        return selectCountByQuery(QueryWrapper.create().eq(MTenant::getPackageId, tenantPackageId));
    }

    /**
     * 根据租户套餐ID查询租户
     * @param tenantPackageId 租户套餐ID
     * @return 租户数量
     */
    default List<MTenant> selectTenantByTenantPackageId(Long tenantPackageId) {
        return selectListByQuery(QueryWrapper.create().eq(MTenant::getPackageId, tenantPackageId));
    }

    /**
     * 根据租户名查询租户
     * @param tenantName 租户名
     * @return 租户实体
     */
    default MTenant selectTenantByTenantName(String tenantName) {
        return selectOneByQuery(QueryWrapper.create().eq(MTenant::getTenantName, tenantName));
    }

    /**
     * 根据租户ID查询租户
     * @param tenantId 租户ID
     * @return 租户实体
     */
    default MTenant selectTenantByTenantId(Long tenantId) {
        return selectOneByQuery(QueryWrapper.create().eq(MTenant::getTenantId, tenantId));
    }

    /**
     * 根据租户ID删除租户
     * @param tenantId 租户ID
     */
    default void deleteTenantByTenantId(Long tenantId) {
        deleteByQuery(QueryWrapper.create().eq(MTenant::getTenantId, tenantId));
    }

    /**
     * 根据租户ID更新租户
     * @param tenant 租户信息
     */
    default void updateTenantByTenantId(MTenant tenant) {
        updateByQuery(tenant, QueryWrapper.create().eq(MTenant::getTenantId, tenant.getTenantId()));
    }

    /**
     * 查询租户(分页)
     * @param queryVO 查询条件
     * @return 租户分页数据
     */
    default Page<TenantVO> selectPageTenantList(TenantQueryVO queryVO) {
        return paginateAs(queryVO.getPageNum(), queryVO.getPageSize(),
                QueryWrapper.create()
                        .eq(MTenant::getStatus, queryVO.getStatus())
                        .like(MTenant::getTenantName, queryVO.getTenantName()),
                TenantVO.class
        );
    }

    /**
     * 租户列表 -> 字典查询
     * @return 部门列表
     */
    default List<MTenant> selectTenantDict() {
        return selectListByQuery(QueryWrapper.create().eq(MTenant::getStatus, StatusEnum.STATUS_1.getCode()));
    }

}
