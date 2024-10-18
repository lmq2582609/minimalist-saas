package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MDept;
import com.minimalist.basic.mapper.MDeptMapper;
import com.minimalist.basic.service.MDeptService;
import org.springframework.stereotype.Service;

/**
 * 部门表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MDeptServiceImpl extends ServiceImpl<MDeptMapper, MDept> implements MDeptService {

}
