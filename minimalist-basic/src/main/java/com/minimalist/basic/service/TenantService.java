package com.minimalist.basic.service;

import com.minimalist.basic.entity.vo.tenant.TenantQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.config.mybatis.bo.PageResp;

public interface TenantService {

    /**
     * 添加租户
     * @param tenantVO 租户信息
     */
    void addTenant(TenantVO tenantVO);

    /**
     * 删除租户 -> 根据租户ID删除
     * @param tenantId 租户ID
     */
    void deleteTenantByTenantId(Long tenantId);

    /**
     * 修改租户 -> 根据租户ID修改
     * @param tenantVO 租户信息
     */
    void updateTenantByTenantId(TenantVO tenantVO);

    /**
     * 查询租户(分页)
     * @param queryVO 查询条件
     * @return 租户分页数据
     */
    PageResp<TenantVO> getPageTenantList(TenantQueryVO queryVO);

    /**
     * 根据租户ID查询租户
     * @param tenantId 租户ID
     * @return 租户数据
     */
    TenantVO getTenantByTenantId(Long tenantId);

}
