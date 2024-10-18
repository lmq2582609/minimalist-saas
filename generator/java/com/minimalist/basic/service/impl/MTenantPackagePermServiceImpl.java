package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MTenantPackagePerm;
import com.minimalist.basic.mapper.MTenantPackagePermMapper;
import com.minimalist.basic.service.MTenantPackagePermService;
import org.springframework.stereotype.Service;

/**
 * 租户套餐与权限关联表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MTenantPackagePermServiceImpl extends ServiceImpl<MTenantPackagePermMapper, MTenantPackagePerm> implements MTenantPackagePermService {

}
