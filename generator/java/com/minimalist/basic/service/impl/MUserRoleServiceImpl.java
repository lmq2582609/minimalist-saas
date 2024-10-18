package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MUserRole;
import com.minimalist.basic.mapper.MUserRoleMapper;
import com.minimalist.basic.service.MUserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户与角色关联表 1用户-N角色 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MUserRoleServiceImpl extends ServiceImpl<MUserRoleMapper, MUserRole> implements MUserRoleService {

}
