package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MRole;
import com.minimalist.basic.mapper.MRoleMapper;
import com.minimalist.basic.service.MRoleService;
import org.springframework.stereotype.Service;

/**
 * 角色表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MRoleServiceImpl extends ServiceImpl<MRoleMapper, MRole> implements MRoleService {

}
