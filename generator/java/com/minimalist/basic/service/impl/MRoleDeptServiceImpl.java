package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MRoleDept;
import com.minimalist.basic.mapper.MRoleDeptMapper;
import com.minimalist.basic.service.MRoleDeptService;
import org.springframework.stereotype.Service;

/**
 * 角色与部门关联表 1角色-N部门 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MRoleDeptServiceImpl extends ServiceImpl<MRoleDeptMapper, MRoleDept> implements MRoleDeptService {

}
