package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MPerms;
import com.minimalist.basic.mapper.MPermsMapper;
import com.minimalist.basic.service.MPermsService;
import org.springframework.stereotype.Service;

/**
 * 权限表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MPermsServiceImpl extends ServiceImpl<MPermsMapper, MPerms> implements MPermsService {

}
