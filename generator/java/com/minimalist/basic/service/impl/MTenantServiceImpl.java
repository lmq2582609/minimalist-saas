package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MTenant;
import com.minimalist.basic.mapper.MTenantMapper;
import com.minimalist.basic.service.MTenantService;
import org.springframework.stereotype.Service;

/**
 * 租户表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MTenantServiceImpl extends ServiceImpl<MTenantMapper, MTenant> implements MTenantService {

}
