package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.mapper.MFileMapper;
import com.minimalist.basic.service.MFileService;
import org.springframework.stereotype.Service;

/**
 * 文件表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MFileServiceImpl extends ServiceImpl<MFileMapper, MFile> implements MFileService {

}
