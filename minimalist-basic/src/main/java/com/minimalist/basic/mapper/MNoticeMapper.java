package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.NoticeEnum;
import com.minimalist.basic.entity.po.MNotice;
import com.minimalist.basic.entity.vo.notice.NoticeQueryVO;
import com.minimalist.common.enums.StatusEnum;
import com.minimalist.common.mybatis.QueryCondition;
import com.minimalist.common.mybatis.bo.Pager;
import java.time.LocalDateTime;

/**
 * <p>
 * 通知公告表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-07-18
 */
public interface MNoticeMapper extends BaseMapper<MNotice> {

    /**
     * 根据公告ID删除公告
     * @param noticeId 公告ID
     * @return 受影响行数
     */
    default int deleteNoticeByNoticeId(Long noticeId) {
        return delete(new LambdaQueryWrapper<MNotice>().eq(MNotice::getNoticeId, noticeId));
    }

    /**
     * 根据公告ID修改公告
     * @param notice 公告数据
     * @return 受影响行数
     */
    default int updateNoticeByNoticeId(MNotice notice) {
        return update(notice, new LambdaUpdateWrapper<MNotice>().eq(MNotice::getNoticeId, notice.getNoticeId()));
    }

    /**
     * 查询公告列表(分页) -> 公告管理使用
     * @param queryVO 查询条件
     * @return 公告分页数据
     */
    default Page<MNotice> selectPageNoticeList(NoticeQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MNotice>()
                        .likeNotNull(MNotice::getNoticeTitle, queryVO.getNoticeTitle())
                        .eqNotNull(MNotice::getStatus, queryVO.getStatus())
                        .eqNotNull(MNotice::getNoticeType, queryVO.getNoticeType())
                        .orderByAscc(MNotice::getNoticeSort)
                        .orderByDescc(MNotice::getNoticeTop)
        );
    }

    /**
     * 根据公告ID查询公告
     * @param noticeId 公告ID
     * @return 公告实体
     */
    default MNotice selectNoticeByNoticeId(Long noticeId) {
        return selectOne(new LambdaQueryWrapper<MNotice>().eq(MNotice::getNoticeId, noticeId));
    }

    /**
     * 查询公告列表(分页) -> 首页使用
     * @return
     */
    default Page<MNotice> selectHomePageNoticeList(Pager pager) {
        return selectPage(new Page<>(pager.getPageNum(), pager.getPageSize()),
                new LambdaQueryWrapper<MNotice>()
                        .eq(MNotice::getStatus, StatusEnum.STATUS_1.getCode())
                        .eq(MNotice::getNoticeType, NoticeEnum.NoticeType.NOTICE.getCode())
                        .orderByAsc(MNotice::getNoticeSort)
                        .orderByDesc(MNotice::getNoticeTop)
                        .le(MNotice::getPublishTime, LocalDateTime.now())
        );
    }
}
