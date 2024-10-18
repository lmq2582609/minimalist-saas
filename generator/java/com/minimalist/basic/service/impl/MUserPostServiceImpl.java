package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MUserPost;
import com.minimalist.basic.mapper.MUserPostMapper;
import com.minimalist.basic.service.MUserPostService;
import org.springframework.stereotype.Service;

/**
 * 用户与岗位关联表 1用户-N岗位 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MUserPostServiceImpl extends ServiceImpl<MUserPostMapper, MUserPost> implements MUserPostService {

}
