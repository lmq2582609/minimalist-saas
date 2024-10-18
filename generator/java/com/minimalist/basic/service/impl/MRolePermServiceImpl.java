package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MRolePerm;
import com.minimalist.basic.mapper.MRolePermMapper;
import com.minimalist.basic.service.MRolePermService;
import org.springframework.stereotype.Service;

/**
 * 角色与权限关联表 1角色-N权限 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MRolePermServiceImpl extends ServiceImpl<MRolePermMapper, MRolePerm> implements MRolePermService {

}
