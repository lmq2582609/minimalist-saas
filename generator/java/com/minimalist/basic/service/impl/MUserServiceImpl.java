package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.basic.service.MUserService;
import org.springframework.stereotype.Service;

/**
 * 用户表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MUserServiceImpl extends ServiceImpl<MUserMapper, MUser> implements MUserService {

}
