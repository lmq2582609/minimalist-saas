package com.minimalist.basic.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.minimalist.basic.entity.enums.FileEnum;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.enums.NoticeEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.po.MNotice;
import com.minimalist.basic.entity.vo.file.FileVO;
import com.minimalist.basic.entity.vo.notice.NoticeQueryVO;
import com.minimalist.basic.entity.vo.notice.NoticeVO;
import com.minimalist.basic.mapper.MFileMapper;
import com.minimalist.basic.mapper.MNoticeMapper;
import com.minimalist.basic.service.FileService;
import com.minimalist.basic.service.NoticeService;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.config.mybatis.bo.PageReq;
import com.minimalist.basic.utils.TextUtil;
import com.minimalist.basic.utils.UnqIdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private MNoticeMapper noticeMapper;

    @Autowired
    private MFileMapper fileMapper;

    @Autowired
    private FileService fileService;

    /**
     * 添加公告
     * @param noticeVO 公告信息
     */
    @Override
    @DSTransactional(rollbackFor = Exception.class)
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
        //公告相关文件处理
        String fileIds = addNoticeFileHandler(noticeVO);
        mNotice.setNoticePicFileId(fileIds);
        //内容处理 -> 编码
        mNotice.setNoticeContent(TextUtil.encode(noticeVO.getNoticeContent()));
        noticeMapper.insert(mNotice, true);
    }

    /**
     * 删除公告 -> 根据公告ID删除
     * @param noticeId 公告ID
     */
    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void deleteNoticeByNoticeId(Long noticeId) {
        //查询公告
        MNotice mNotice = noticeMapper.selectNoticeByNoticeId(noticeId);
        Assert.notNull(mNotice, () -> new BusinessException(NoticeEnum.ErrorMsg.NONENTITY_NOTICE.getDesc()));
        //公告相关文件处理
        deleteNoticeFileHandler(mNotice);
        //删除公告
        noticeMapper.deleteNoticeByNoticeId(noticeId);
    }

    /**
     * 修改公告 -> 根据公告ID修改
     * @param noticeVO 公告信息
     */
    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void updateNoticeByNoticeId(NoticeVO noticeVO) {
        //查询公告
        MNotice mNotice = noticeMapper.selectNoticeByNoticeId(noticeVO.getNoticeId());
        Assert.notNull(mNotice, () -> new BusinessException(NoticeEnum.ErrorMsg.NONENTITY_NOTICE.getDesc()));
        MNotice newNotice = BeanUtil.copyProperties(noticeVO, MNotice.class);
        //延期发布处理
        if (ObjectUtil.isNotNull(noticeVO.getNoticeTimeInterval())) {
            //延期发布 -> 发布时间置为延期发布的时间
            newNotice.setPublishTime(noticeVO.getNoticeTimeInterval());
        } else {
            //不延期 -> 不处理，不修改发布时间
        }
        //公告相关文件处理
        String fileIds = updateNoticeFileHandler(mNotice, noticeVO);
        newNotice.setNoticePicFileId(fileIds);
        //内容处理 -> 编码
        newNotice.setNoticeContent(TextUtil.encode(noticeVO.getNoticeContent()));
        //修改
        noticeMapper.updateNoticeByNoticeId(newNotice);
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
        return new PageResp<>(noticeVOS, mNoticePage.getTotalRow());
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
    public PageResp<NoticeVO> getPageHomeNoticeList(PageReq pageReq) {
        //分页查询
        Page<MNotice> mNoticePage = noticeMapper.selectHomePageNoticeList(pageReq);
        //数据转换
        List<NoticeVO> noticeVOS = BeanUtil.copyToList(mNoticePage.getRecords(), NoticeVO.class);
        //将内容清空，因为列表不需要展示内容
        noticeVOS.forEach(n -> n.setNoticeContent(null));
        return new PageResp<>(noticeVOS, mNoticePage.getTotalRow());
    }

    /**
     * 新增公告时文件的处理
     * @param newNotice 新增的公告信息
     * @return 封面图文件ID
     */
    private String addNoticeFileHandler(NoticeVO newNotice) {
        //处理富文本中的图片 - 从富文本内容中提取文件名
        Set<String> fileUrls = TextUtil.extractFileUrl(newNotice.getNoticeContent());
        for (String fileUrl : fileUrls) {
            //从url中提取文件名
            String cleanUrl = StrUtil.subBefore(fileUrl, "?", true);
            String fileName = StrUtil.subAfter(cleanUrl, "/", true);
            //根据文件名查询文件
            MFile file = fileMapper.selectOneByQuery(QueryWrapper.create().eq(MFile::getNewFileName, fileName));
            //未查询到文件，跳过
            if (ObjectUtil.isNull(file)) {
                log.warn("公告文件处理，从富文本内容中提取文件名，未查询到文件，文件名：{}", fileName);
                continue;
            }
            //已指定文件来源-将状态修改为启用
            if (ObjectUtil.isNotNull(file.getFileSource()) && file.getFileSource() != -1) {
                MFile mFile = new MFile();
                mFile.setFileId(file.getFileId());
                mFile.setStatus(StatusEnum.STATUS_1.getCode());
                fileMapper.updateByQuery(mFile, QueryWrapper.create().eq(MFile::getFileId, file.getFileId()));
                continue;
            }
            //移动文件到对应的目录 - 并将文件状态置为已使用
            MFile newFile = fileService.moveFile(file.getFileId(), FileEnum.FileSource.NOTICE_CONTENT_IMG.getCode(), StatusEnum.STATUS_1.getCode());
            //将新的url替换富文本内容中的旧url
            String replace = StrUtil.replace(newNotice.getNoticeContent(), fileUrl, newFile.getFileUrl());
            newNotice.setNoticeContent(replace);
        }
        StringJoiner fileIds = new StringJoiner(",");
        //处理公告封面图片
        if (CollectionUtil.isNotEmpty(newNotice.getNoticePicFile())) {
            for (FileVO fileVO : newNotice.getNoticePicFile()) {
                //已指定文件来源-将状态修改为启用
                if (ObjectUtil.isNotNull(fileVO.getFileSource()) && fileVO.getFileSource() != -1) {
                    MFile mFile = new MFile();
                    mFile.setFileId(fileVO.getFileId());
                    mFile.setStatus(StatusEnum.STATUS_1.getCode());
                    fileMapper.updateByQuery(mFile, QueryWrapper.create().eq(MFile::getFileId, fileVO.getFileId()));
                    //文件ID，逗号分隔
                    fileIds.add(fileVO.getFileId().toString());
                    continue;
                }
                //移动文件到对应的目录 - 并将文件状态置为已使用
                MFile newFile = fileService.moveFile(fileVO.getFileId(), FileEnum.FileSource.NOTICE_COVER_IMG.getCode(), StatusEnum.STATUS_1.getCode());
                //文件ID，逗号分隔
                fileIds.add(fileVO.getFileId().toString());
            }
            //图片文件ID，逗号分隔
            newNotice.setNoticePicFileId(fileIds.toString());
        }
        return fileIds.toString();
    }

    /**
     * 删除公告时文件的处理
     * @param notice 删除的公告信息
     */
    private void deleteNoticeFileHandler(MNotice notice) {
        //将公告封面图的状态置为未使用
        if (StrUtil.isNotBlank(notice.getNoticePicFileId())) {
            List<Long> noticePicFileIdList = TextUtil.splitAndListStrToListLong(notice.getNoticePicFileId());
            if (CollectionUtil.isNotEmpty(noticePicFileIdList)) {
                fileMapper.updateFileStatusByFileIds(StpUtil.getLoginIdAsLong(), noticePicFileIdList, StatusEnum.STATUS_0.getCode(), null);
            }
        }
        //处理富文本中的图片 - 从富文本内容中提取文件名
        Set<String> fileUrls = TextUtil.extractFileUrl(notice.getNoticeContent());
        //文件ID列表
        List<Long> fileIds = CollectionUtil.list(false);
        for (String fileUrl : fileUrls) {
            //从url中提取文件名
            String cleanUrl = StrUtil.subBefore(fileUrl, "?", true);
            String fileName = StrUtil.subAfter(cleanUrl, "/", true);
            //根据文件名查询文件
            MFile file = fileMapper.selectOneByQuery(QueryWrapper.create().eq(MFile::getNewFileName, fileName));
            //未查询到文件，跳过
            if (ObjectUtil.isNull(file)) {
                log.warn("公告文件处理，从富文本内容中提取文件名，未查询到文件，文件名：{}", fileName);
                continue;
            }
            fileIds.add(file.getFileId());
        }
        //将公告内容中的文件状态置为未使用
        fileMapper.updateFileStatusByFileIds(StpUtil.getLoginIdAsLong(), fileIds, StatusEnum.STATUS_0.getCode(), null);
    }

    /**
     * 修改公告时文件的处理
     * @param oldNotice 修改前的公告信息
     * @param newNotice 修改后的公告信息
     */
    private String updateNoticeFileHandler(MNotice oldNotice, NoticeVO newNotice) {
        //删除原公告相关的文件
        deleteNoticeFileHandler(oldNotice);
        //新增新公告相关的文件
        return addNoticeFileHandler(newNotice);
    }

}
