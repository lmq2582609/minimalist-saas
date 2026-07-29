package com.minimalist.basic.service;

import com.minimalist.basic.entity.vo.tenant.TenantDatasourceVO;

/**
 * 租户数据库初始化服务
 */
public interface TenantDbInitService {

    /**
     * 初始化租户数据库（执行建表模板SQL）
     * @param datasourceVO 数据源连接信息
     */
    void initTenantDatabase(TenantDatasourceVO datasourceVO);

}
