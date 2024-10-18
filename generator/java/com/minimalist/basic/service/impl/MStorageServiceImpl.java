package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.mapper.MStorageMapper;
import com.minimalist.basic.service.MStorageService;
import org.springframework.stereotype.Service;

/**
 * 存储管理表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MStorageServiceImpl extends ServiceImpl<MStorageMapper, MStorage> implements MStorageService {

}
