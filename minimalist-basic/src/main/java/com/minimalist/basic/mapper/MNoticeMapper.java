package com.minimalist.basic.mapper;

import com.minimalist.basic.config.mybatis.bo.PageReq;
import com.minimalist.basic.entity.enums.NoticeEnum;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.vo.notice.NoticeQueryVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MNotice;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import java.time.LocalDateTime;

/**
 * 通知公告表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MNoticeMapper extends BaseMapper<MNotice> {
    /**
     * 根据公告ID删除公告
     * @param noticeId 公告ID
     */
    default void deleteNoticeByNoticeId(Long noticeId) {
        deleteByQuery(QueryWrapper.create().eq(MNotice::getNoticeId, noticeId));
    }

    /**
     * 根据公告ID修改公告
     * @param notice 公告数据
     */
    default void updateNoticeByNoticeId(MNotice notice) {
        updateByQuery(notice, QueryWrapper.create().eq(MNotice::getNoticeId, notice.getNoticeId()));
    }

    /**
     * 查询公告列表(分页) -> 公告管理使用
     * @param queryVO 查询条件
     * @return 公告分页数据
     */
    default Page<MNotice> selectPageNoticeList(NoticeQueryVO queryVO) {
        return paginate(queryVO.getPageNum(), queryVO.getPageSize(),
                QueryWrapper.create()
                        .eq(MNotice::getStatus, queryVO.getStatus())
                        .eq(MNotice::getNoticeType, queryVO.getNoticeType())
                        .like(MNotice::getNoticeTitle, queryVO.getNoticeTitle())
                        .orderBy(MNotice::getNoticeTop, false)
                        .orderBy(MNotice::getNoticeSort, true));
    }

    /**
     * 根据公告ID查询公告
     * @param noticeId 公告ID
     * @return 公告实体
     */
    default MNotice selectNoticeByNoticeId(Long noticeId) {
        return selectOneByQuery(QueryWrapper.create().eq(MNotice::getNoticeId, noticeId));
    }

    /**
     * 查询公告列表(分页) -> 首页使用
     * @return 公告列表
     */
    default Page<MNotice> selectHomePageNoticeList(PageReq pageReq) {
        return paginate(pageReq.getPageNum(), pageReq.getPageSize(),
                QueryWrapper.create()
                        .eq(MNotice::getStatus, StatusEnum.STATUS_1.getCode())
                        .eq(MNotice::getNoticeType, NoticeEnum.NoticeType.NOTICE.getCode())
                        .le(MNotice::getPublishTime, LocalDateTime.now())
                        .orderBy(MNotice::getNoticeTop, false)
                        .orderBy(MNotice::getNoticeSort, true)
        );
    }
}
