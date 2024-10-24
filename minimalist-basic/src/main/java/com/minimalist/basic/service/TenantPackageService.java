package com.minimalist.basic.service;

import com.minimalist.basic.entity.po.MPerms;
import com.minimalist.basic.entity.vo.tenant.TenantPackageQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantPackageVO;
import com.minimalist.basic.config.mybatis.bo.PageResp;

import java.util.List;

public interface TenantPackageService {

    /**
     * 添加租户套餐
     * @param tenantPackageVO 租户套餐信息
     */
    void addTenantPackage(TenantPackageVO tenantPackageVO);

    /**
     * 删除租户套餐 -> 根据租户套餐ID删除租户套餐
     * @param tenantPackageId 租户套餐ID
     */
    void deleteTenantPackageByTenantPackageId(Long tenantPackageId);

    /**
     * 修改租户套餐 -> 根据租户套餐ID修改
     * @param tenantPackageVO 租户套餐信息
     */
    void updateTenantPackageByTenantPackageId(TenantPackageVO tenantPackageVO);

    /**
     * 查询租户套餐(分页)
     * @param queryVO 查询条件
     * @return 租户套餐分页数据
     */
    PageResp<TenantPackageVO> getPageTenantPackageList(TenantPackageQueryVO queryVO);

    /**
     * 根据租户套餐ID查询租户套餐
     * @param tenantPackageId 租户套餐ID
     * @return 租户套餐数据
     */
    TenantPackageVO getTenantPackageByTenantPackageId(Long tenantPackageId);

}
