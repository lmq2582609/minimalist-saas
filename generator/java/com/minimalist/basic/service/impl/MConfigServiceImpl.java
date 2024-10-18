package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MConfig;
import com.minimalist.basic.mapper.MConfigMapper;
import com.minimalist.basic.service.MConfigService;
import org.springframework.stereotype.Service;

/**
 * 参数配置表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MConfigServiceImpl extends ServiceImpl<MConfigMapper, MConfig> implements MConfigService {

}
