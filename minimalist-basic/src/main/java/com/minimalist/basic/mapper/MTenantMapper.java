package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.po.MTenant;
import com.minimalist.basic.entity.vo.tenant.TenantQueryVO;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.mybatis.QueryCondition;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-07-04
 */
public interface MTenantMapper extends BaseMapper<MTenant> {

    /**
     * 根据租户套餐ID查询租户数量
     * @param tenantPackageId 租户套餐ID
     * @return 租户数量
     */
    default long selectTenantCountByTenantPackageId(Long tenantPackageId) {
        return selectCount(new LambdaQueryWrapper<MTenant>().eq(MTenant::getPackageId, tenantPackageId));
    }

    /**
     * 根据租户套餐ID查询租户
     * @param tenantPackageId 租户套餐ID
     * @return 租户数量
     */
    default List<MTenant> selectTenantByTenantPackageId(Long tenantPackageId) {
        return selectList(new LambdaQueryWrapper<MTenant>().eq(MTenant::getPackageId, tenantPackageId));
    }

    /**
     * 根据租户名查询租户
     * @param tenantName 租户名
     * @return 租户实体
     */
    default MTenant selectTenantByTenantName(String tenantName) {
        return selectOne(new LambdaQueryWrapper<MTenant>().eq(MTenant::getTenantName, tenantName));
    }

    /**
     * 根据租户ID查询租户
     * @param tenantId 租户ID
     * @return 租户实体
     */
    default MTenant selectTenantByTenantId(Long tenantId) {
        return selectOne(new LambdaQueryWrapper<MTenant>().eq(MTenant::getTenantId, tenantId));
    }

    /**
     * 根据租户ID删除租户
     * @param tenantId 租户ID
     * @return 受影响行数
     */
    default int deleteTenantByTenantId(Long tenantId) {
        return delete(new LambdaQueryWrapper<MTenant>().eq(MTenant::getTenantId, tenantId));
    }

    /**
     * 根据租户ID更新租户
     * @param tenant 租户信息
     * @return 受影响行数
     */
    default int updateTenantByTenantId(MTenant tenant) {
        return update(tenant, new LambdaUpdateWrapper<MTenant>().eq(MTenant::getTenantId, tenant.getTenantId()));
    }

    /**
     * 查询租户(分页)
     * @param queryVO 查询条件
     * @return 租户分页数据
     */
    default Page<MTenant> selectPageTenantList(TenantQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MTenant>()
                        .likeNotNull(MTenant::getTenantName, queryVO.getTenantName())
                        .eqNotNull(MTenant::getStatus, queryVO.getStatus())
                );
    }

    /**
     * 根据用户ID查询租户
     * @param userId 用户ID
     * @return 租户实体
     */
    default MTenant selectTenantByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapper<MTenant>().eq(MTenant::getUserId, userId));
    }

    /**
     * 租户列表 -> 字典查询
     * @return 部门列表
     */
    default List<MTenant> selectTenantDict() {
        LambdaQueryWrapper<MTenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(MTenant::getTenantId, MTenant::getTenantName);
        queryWrapper.eq(MTenant::getStatus, StatusEnum.STATUS_1.getCode());
        return selectList(queryWrapper);
    }

}
