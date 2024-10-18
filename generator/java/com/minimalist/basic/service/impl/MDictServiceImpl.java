package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MDict;
import com.minimalist.basic.mapper.MDictMapper;
import com.minimalist.basic.service.MDictService;
import org.springframework.stereotype.Service;

/**
 * 字典表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MDictServiceImpl extends ServiceImpl<MDictMapper, MDict> implements MDictService {

}
