package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MUserDept;
import com.minimalist.basic.mapper.MUserDeptMapper;
import com.minimalist.basic.service.MUserDeptService;
import org.springframework.stereotype.Service;

/**
 * 用户与岗位关联表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MUserDeptServiceImpl extends ServiceImpl<MUserDeptMapper, MUserDept> implements MUserDeptService {

}
