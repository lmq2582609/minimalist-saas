package com.minimalist.basic.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.common.enums.StatusEnum;
import com.minimalist.basic.entity.enums.NoticeEnum;
import com.minimalist.common.module.entity.po.MFile;
import com.minimalist.basic.entity.po.MNotice;
import com.minimalist.common.module.entity.vo.file.FileVO;
import com.minimalist.basic.entity.vo.notice.NoticeQueryVO;
import com.minimalist.basic.entity.vo.notice.NoticeVO;
import com.minimalist.common.module.mapper.MFileMapper;
import com.minimalist.basic.mapper.MNoticeMapper;
import com.minimalist.basic.service.NoticeService;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.mybatis.bo.Pager;
import com.minimalist.common.utils.TextUtil;
import com.minimalist.common.utils.UnqIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private MNoticeMapper noticeMapper;

    @Autowired
    private MFileMapper fileMapper;

    /**
     * 添加公告
     * @param noticeVO 公告信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addNotice(NoticeVO noticeVO) {
        MNotice mNotice = BeanUtil.copyProperties(noticeVO, MNotice.class);
        //公告ID
        long noticeId = UnqIdUtil.uniqueId();
        mNotice.setNoticeId(noticeId);
        //延期发布处理
        if (ObjectUtil.isNotNull(noticeVO.getNoticeTimeInterval())) {
            //延期发布 -> 发布时间置为延期发布的时间
            mNotice.setPublishTime(noticeVO.getNoticeTimeInterval());
        } else {
            //不延期 -> 发布时间置为当前时间
            mNotice.setPublishTime(LocalDateTime.now());
        }
        //内容处理 -> 编码
        mNotice.setNoticeContent(TextUtil.encode(noticeVO.getNoticeContent()));
        //插入
        int insertCount = noticeMapper.insert(mNotice);
        Assert.isTrue(insertCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //公告相关文件处理
        fileStatusHandler(null, noticeVO, StatusEnum.STATUS_1.getCode());
    }

    /**
     * 删除公告 -> 根据公告ID删除
     * @param noticeId 公告ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNoticeByNoticeId(Long noticeId) {
        //查询公告
        MNotice mNotice = noticeMapper.selectNoticeByNoticeId(noticeId);
        Assert.notNull(mNotice, () -> new BusinessException(NoticeEnum.ErrorMsg.NONENTITY_NOTICE.getDesc()));
        //公告相关文件处理
        fileStatusHandler(null, BeanUtil.copyProperties(mNotice, NoticeVO.class), StatusEnum.STATUS_0.getCode());
        //删除公告
        noticeMapper.deleteNoticeByNoticeId(noticeId);
    }

    /**
     * 修改公告 -> 根据公告ID修改
     * @param noticeVO 公告信息
     */
    @Override
    public void updateNoticeByNoticeId(NoticeVO noticeVO) {
        //查询公告
        MNotice mNotice = noticeMapper.selectNoticeByNoticeId(noticeVO.getNoticeId());
        Assert.notNull(mNotice, () -> new BusinessException(NoticeEnum.ErrorMsg.NONENTITY_NOTICE.getDesc()));
        MNotice newNotice = BeanUtil.copyProperties(noticeVO, MNotice.class);
        //乐观锁字段赋值
        newNotice.updateBeforeSetVersion(mNotice.getVersion());
        //延期发布处理
        if (ObjectUtil.isNotNull(noticeVO.getNoticeTimeInterval())) {
            //延期发布 -> 发布时间置为延期发布的时间
            newNotice.setPublishTime(noticeVO.getNoticeTimeInterval());
        } else {
            //不延期 -> 不处理，不修改发布时间
        }
        //内容处理 -> 编码
        newNotice.setNoticeContent(TextUtil.encode(noticeVO.getNoticeContent()));
        //修改
        int updateCount = noticeMapper.updateNoticeByNoticeId(newNotice);
        Assert.isTrue(updateCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //公告相关文件处理
        fileStatusHandler(mNotice, BeanUtil.copyProperties(newNotice, NoticeVO.class), null);
    }

    /**
     * 查询公告列表(分页) -> 公告管理使用
     * @param queryVO 查询条件
     * @return 公告分页数据
     */
    @Override
    public PageResp<NoticeVO> getPageNoticeList(NoticeQueryVO queryVO) {
        //分页查询
        Page<MNotice> mNoticePage = noticeMapper.selectPageNoticeList(queryVO);
        //数据转换
        List<NoticeVO> noticeVOS = BeanUtil.copyToList(mNoticePage.getRecords(), NoticeVO.class);
        //汇总封面图文件ID
        List<Long> noticePicFileIdList = noticeVOS.stream().filter(n -> StrUtil.isNotBlank(n.getNoticePicFileId()))
                .flatMap(n -> {
                    List<Long> fileIdList = TextUtil.splitAndListStrToListLong(n.getNoticePicFileId());
                    return Arrays.stream(fileIdList.toArray(Long[]::new));
                }).toList();
        if (CollectionUtil.isNotEmpty(noticePicFileIdList)) {
            //根据文件ID查询文件
            List<MFile> fileList = fileMapper.selectFileByFileIds(noticePicFileIdList);
            //将文件按照URL转Map，key：文件ID，value：文件实体
            Map<Long, MFile> fileMap = fileList.stream().collect(Collectors.toMap(MFile::getFileId, Function.identity(), (v1, v2) -> v1));
            noticeVOS.forEach(n -> {
                //内容清空
                n.setNoticeContent(null);
                //封面图文件处理
                if (StrUtil.isNotBlank(n.getNoticePicFileId())) {
                    List<Long> fileIdList = TextUtil.splitAndListStrToListLong(n.getNoticePicFileId());
                    List<FileVO> fileVOList = CollectionUtil.list(false);
                    fileIdList.forEach(fileId -> {
                        if (fileMap.containsKey(fileId)) {
                            MFile mFile = fileMap.get(fileId);
                            fileVOList.add(BeanUtil.copyProperties(mFile, FileVO.class));
                        }
                    });
                    //设置封面图文件
                    n.setNoticePicFile(fileVOList);
                }
            });
        } else {
            //将内容清空，因为列表不需要展示内容
            noticeVOS.forEach(n -> n.setNoticeContent(null));
        }
        return new PageResp<>(noticeVOS, mNoticePage.getTotal());
    }

    /**
     * 根据公告ID查询公告
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public NoticeVO getNoticeByNoticeId(Long noticeId) {
        MNotice mNotice = noticeMapper.selectNoticeByNoticeId(noticeId);
        //内容 -> 解码
        mNotice.setNoticeContent(TextUtil.decode(mNotice.getNoticeContent()));
        NoticeVO noticeVO = BeanUtil.copyProperties(mNotice, NoticeVO.class);
        //查询公告封面图
        if (StrUtil.isNotBlank(mNotice.getNoticePicFileId())) {
            List<Long> noticePicFileIdList = TextUtil.splitAndListStrToListLong(mNotice.getNoticePicFileId());
            List<MFile> mFiles = fileMapper.selectFileByFileIds(noticePicFileIdList);
            List<FileVO> fileVOList = BeanUtil.copyToList(mFiles, FileVO.class);
            noticeVO.setNoticePicFile(fileVOList);
        }
        return noticeVO;
    }

    /**
     * 查询公告列表(分页) -> 首页使用
     * @return 公告分页数据
     */
    @Override
    public PageResp<NoticeVO> getPageHomeNoticeList(Pager pager) {
        //分页查询
        Page<MNotice> mNoticePage = noticeMapper.selectHomePageNoticeList(pager);
        //数据转换
        List<NoticeVO> noticeVOS = BeanUtil.copyToList(mNoticePage.getRecords(), NoticeVO.class);
        //将内容清空，因为列表不需要展示内容
        noticeVOS.forEach(n -> n.setNoticeContent(null));
        return new PageResp<>(noticeVOS, mNoticePage.getTotal());
    }

    /**
     * 修改公告相关的文件信息
     * @param oldNotice 公告信息(旧)
     * @param newNotice 公告信息(新)
     * @param fileStatus 文件状态
     */
    private void fileStatusHandler(MNotice oldNotice, NoticeVO newNotice, Integer fileStatus) {
        //公告插入、删除的文件处理
        if (ObjectUtil.isNull(oldNotice)) {
            //图片文件ID
            List<Long> fileIdList = CollectionUtil.list(false);
            //处理公告封面图片和富文本中的图片
            if (StrUtil.isNotBlank(newNotice.getNoticePicFileId())) {
                List<Long> noticePicFileIdList = TextUtil.splitAndListStrToListLong(newNotice.getNoticePicFileId());
                fileIdList.addAll(noticePicFileIdList);
            }
            //将公告封面图片的状态置为已使用
            fileMapper.updateFileStatusByFileIds(StpUtil.getLoginIdAsLong(), fileIdList, fileStatus);
            //处理富文本编辑器中的图片，富文本内容解码
            String decodeOldContent = TextUtil.decode(newNotice.getNoticeContent());
            List<String> imgUrlList = TextUtil.getImgUrlByRichText(decodeOldContent);
            if (CollectionUtil.isNotEmpty(imgUrlList)) {
                //将富文本中的图片的状态置为已使用
                fileMapper.updateStatusByFileUrl(StpUtil.getLoginIdAsLong(), imgUrlList, fileStatus);
            }
        } else {
            //旧公告封面图片文件ID
            List<Long> oldNoticePicFileIdList = CollectionUtil.list(false);
            //旧公告所使用的封面图
            if (StrUtil.isNotBlank(oldNotice.getNoticePicFileId())) {
                List<Long> noticePicFileIdList = TextUtil.splitAndListStrToListLong(oldNotice.getNoticePicFileId());
                oldNoticePicFileIdList.addAll(noticePicFileIdList);
            }
            //将旧公告封面图的状态置为未使用
            fileMapper.updateFileStatusByFileIds(StpUtil.getLoginIdAsLong(), oldNoticePicFileIdList, StatusEnum.STATUS_0.getCode());
            //旧公告富文本中的图片
            String decodeOldContent = TextUtil.decode(oldNotice.getNoticeContent());
            List<String> oldContentImgUrlList = TextUtil.getImgUrlByRichText(decodeOldContent);
            //将旧公告富文本中的图片状态置为未使用
            if (CollectionUtil.isNotEmpty(oldContentImgUrlList)) {
                fileMapper.updateStatusByFileUrl(StpUtil.getLoginIdAsLong(), oldContentImgUrlList, StatusEnum.STATUS_0.getCode());
            }

            //新公告封面图片文件ID
            List<Long> newNoticePicIdList = CollectionUtil.list(false);
            if (StrUtil.isNotBlank(newNotice.getNoticePicFileId())) {
                List<Long> noticePicFileIdList = TextUtil.splitAndListStrToListLong(newNotice.getNoticePicFileId());
                newNoticePicIdList.addAll(noticePicFileIdList);
            }
            //将新公告封面图的状态置为已使用
            fileMapper.updateFileStatusByFileIds(StpUtil.getLoginIdAsLong(), newNoticePicIdList, StatusEnum.STATUS_1.getCode());
            //新公告富文本中的图片
            String decodeNewContent = TextUtil.decode(newNotice.getNoticeContent());
            List<String> newContentImgUrlList = TextUtil.getImgUrlByRichText(decodeNewContent);
            //将新公告富文本中的图片状态置为已使用
            if (CollectionUtil.isNotEmpty(newContentImgUrlList)) {
                fileMapper.updateStatusByFileUrl(StpUtil.getLoginIdAsLong(), newContentImgUrlList, StatusEnum.STATUS_1.getCode());
            }
        }
    }

}
