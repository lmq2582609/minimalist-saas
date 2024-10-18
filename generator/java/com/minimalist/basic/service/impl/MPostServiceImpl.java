package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MPost;
import com.minimalist.basic.mapper.MPostMapper;
import com.minimalist.basic.service.MPostService;
import org.springframework.stereotype.Service;

/**
 * 岗位表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MPostServiceImpl extends ServiceImpl<MPostMapper, MPost> implements MPostService {

}
