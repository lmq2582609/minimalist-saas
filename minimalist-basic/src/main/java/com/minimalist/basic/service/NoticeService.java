package com.minimalist.basic.service;


import com.minimalist.basic.entity.mybatis.PageResp;
import com.minimalist.basic.entity.mybatis.Pager;
import com.minimalist.basic.entity.vo.notice.NoticeQueryVO;
import com.minimalist.basic.entity.vo.notice.NoticeVO;

public interface NoticeService {

    /**
     * 添加公告
     * @param noticeVO 公告信息
     */
    void addNotice(NoticeVO noticeVO);

    /**
     * 删除公告 -> 根据公告ID删除
     * @param noticeId 公告ID
     */
    void deleteNoticeByNoticeId(Long noticeId);

    /**
     * 修改公告 -> 根据公告ID修改
     * @param noticeVO 公告信息
     */
    void updateNoticeByNoticeId(NoticeVO noticeVO);

    /**
     * 查询公告列表(分页) -> 公告管理使用
     * @param queryVO 查询条件
     * @return 公告分页数据
     */
    PageResp<NoticeVO> getPageNoticeList(NoticeQueryVO queryVO);

    /**
     * 根据公告ID查询公告
     * @param noticeId 公告ID
     * @return 公告信息
     */
    NoticeVO getNoticeByNoticeId(Long noticeId);

    /**
     * 查询公告列表(分页) -> 首页使用
     * @return 公告分页数据
     */
    PageResp<NoticeVO> getPageHomeNoticeList(Pager pager);

}
