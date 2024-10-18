package com.minimalist.basic.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.minimalist.basic.entity.po.MNotice;
import com.minimalist.basic.mapper.MNoticeMapper;
import com.minimalist.basic.service.MNoticeService;
import org.springframework.stereotype.Service;

/**
 * 通知公告表 服务层实现。
 *
 * @author asus
 * @since 2024-10-18
 */
@Service
public class MNoticeServiceImpl extends ServiceImpl<MNoticeMapper, MNotice> implements MNoticeService {

}
