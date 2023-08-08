package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.FileEnum;
import com.minimalist.basic.entity.enums.NoticeEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.po.MNotice;
import com.minimalist.basic.entity.vo.file.FileVO;
import com.minimalist.basic.entity.vo.notice.NoticeQueryVO;
import com.minimalist.basic.entity.vo.notice.NoticeVO;
import com.minimalist.basic.mapper.MFileMapper;
import com.minimalist.basic.mapper.MNoticeMapper;
import com.minimalist.basic.service.NoticeService;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.mybatis.bo.Pager;
import com.minimalist.common.utils.SpringSecurityUtil;
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
        fileStatusHandler(null, noticeVO, FileEnum.FileStatus.NOTICE_STATUS_1.getCode().byteValue());
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
        fileStatusHandler(null, BeanUtil.copyProperties(mNotice, NoticeVO.class), FileEnum.FileStatus.NOTICE_STATUS_0.getCode().byteValue());
        //删除公告
        int deleteCount = noticeMapper.deleteNoticeByNoticeId(noticeId);
        Assert.isTrue(deleteCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
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
        //汇总封面图URL
        List<String> urlList = noticeVOS.stream().filter(n -> StrUtil.isNotBlank(n.getNoticePic()))
                .flatMap(n -> {
                    List<String> picUrlList = StrUtil.split(n.getNoticePic(), "|");
                    return Arrays.stream(picUrlList.toArray(String[]::new));
                }).toList();
        if (CollectionUtil.isNotEmpty(urlList)) {
            //根据url查询文件
            List<MFile> fileList = fileMapper.selectFileByFileUrl(urlList);
            //将文件按照URL转Map，key：url，value：文件实体
            Map<String, MFile> fileMap = fileList.stream().collect(Collectors.toMap(MFile::getFileUrl, Function.identity(), (v1, v2) -> v1));
            noticeVOS.forEach(n -> {
                //内容清空
                n.setNoticeContent(null);
                //封面图文件处理
                if (StrUtil.isNotBlank(n.getNoticePic())) {
                    List<String> picUrlList = StrUtil.split(n.getNoticePic(), "|");
                    List<FileVO> fileVOList = CollectionUtil.list(false);
                    picUrlList.forEach(picUrl -> {
                        if (fileMap.containsKey(picUrl)) {
                            MFile mFile = fileMap.get(picUrl);
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
        //封面图
        if (StrUtil.isNotBlank(mNotice.getNoticePic())) {
            //封面图URL
            List<String> urlList = StrUtil.split(mNotice.getNoticePic(), "|");
            //根据url查询文件
            List<MFile> fileList = fileMapper.selectFileByFileUrl(urlList);
            //封面图文件信息
            noticeVO.setNoticePicFile(BeanUtil.copyToList(fileList, FileVO.class));
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
     * @param mNotice 公告信息(旧)
     * @param noticeVO 公告信息(新)
     */
    private void fileStatusHandler(MNotice mNotice, NoticeVO noticeVO, Byte status) {
        //公告插入、删除的文件处理
        if (ObjectUtil.isNull(mNotice)) {
            //图片URL
            List<String> urlList = CollectionUtil.list(false);
            //处理公告封面图片和富文本中的图片，将这些图片的状态置为已使用
            if (StrUtil.isNotBlank(noticeVO.getNoticePic())) {
                //图片URL
                urlList.addAll(StrUtil.split(noticeVO.getNoticePic(), "|"));
            }
            //处理富文本编辑器中的图片
            String decodeOldContent = TextUtil.decode(noticeVO.getNoticeContent());
            List<String> imgUrlList = TextUtil.getImgUrlByRichText(decodeOldContent);
            if (CollectionUtil.isNotEmpty(imgUrlList)) {
                urlList.addAll(imgUrlList);
            }
            //将数据库中的status更新成已使用
            if (CollectionUtil.isNotEmpty(urlList)) {
                fileMapper.updateStatusByFileUrl(SpringSecurityUtil.getUserId(), status, urlList);
            }
        } else {
            //公告修改的文件处理，判断封面图是否有变化，判断富文本内容中的图片是否有变化
            List<String> oldImgUrlList = CollectionUtil.list(false);
            //更新前 -> 公告所使用的封面图
            if (StrUtil.isNotBlank(mNotice.getNoticePic())) {
                oldImgUrlList.addAll(StrUtil.split(mNotice.getNoticePic(), "|"));
            }
            //更新前 -> 公告富文本内容所使用的图片
            String decodeOldContent = TextUtil.decode(mNotice.getNoticeContent());
            List<String> oldContentImgUrlList = TextUtil.getImgUrlByRichText(decodeOldContent);
            if (CollectionUtil.isNotEmpty(oldContentImgUrlList)) {
                oldImgUrlList.addAll(oldContentImgUrlList);
            }
            //将公告旧数据图片，更新为未使用
            if (CollectionUtil.isNotEmpty(oldImgUrlList)) {
                fileMapper.updateStatusByFileUrl(SpringSecurityUtil.getUserId(), FileEnum.FileStatus.NOTICE_STATUS_0.getCode().byteValue(), oldImgUrlList);
            }
            //公告新数据图片处理
            List<String> newImgUrlList = CollectionUtil.list(false);
            //更新后 -> 公告所使用的封面图
            if (StrUtil.isNotBlank(noticeVO.getNoticePic())) {
                newImgUrlList.addAll(StrUtil.split(noticeVO.getNoticePic(), "|"));
            }
            //更新后 -> 公告富文本内容所使用的图片
            String decodeNewContent = TextUtil.decode(noticeVO.getNoticeContent());
            List<String> newContentImgUrlList = TextUtil.getImgUrlByRichText(decodeNewContent);
            if (CollectionUtil.isNotEmpty(newContentImgUrlList)) {
                newImgUrlList.addAll(newContentImgUrlList);
            }
            //将公告新数据图片，更新为已使用
            if (CollectionUtil.isNotEmpty(newImgUrlList)) {
                fileMapper.updateStatusByFileUrl(SpringSecurityUtil.getUserId(), FileEnum.FileStatus.NOTICE_STATUS_1.getCode().byteValue(), newImgUrlList);
            }
        }
    }

}
