package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.FileEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.vo.file.*;
import com.minimalist.basic.mapper.MFileMapper;
import com.minimalist.basic.service.FileService;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.mybatis.EntityService;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.utils.UnqIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MFileMapper fileMapper;

    @Autowired
    private EntityService entityService;

    /**
     * 单文件上传
     * @param fileUploadVO 文件及携带参数
     * @return 文件信息
     */
    @Override
    public FileUploadRespVO uploadFile(FileUploadVO fileUploadVO) {
        //上传文件
        MFile mFile = uploadFile(fileUploadVO.getFile(), fileUploadVO.getFileSource(), FileEnum.FilePlatform.LOCAL_1.getCode());
        int insertCount = fileMapper.insert(mFile);
        log.info("单文件上传，插入数据条数：{}", insertCount);
        return BeanUtil.copyProperties(mFile, FileUploadRespVO.class);
    }

    /**
     * 上传文件
     * @param uploadBatchVO 文件及携带参数
     * @return 文件信息
     */
    @Override
    public List<FileUploadRespVO> uploadFileBatch(FileUploadBatchVO uploadBatchVO) {
        List<MFile> files = CollectionUtil.list(false);
        //循环上传多个文件
        for (MultipartFile file : uploadBatchVO.getFiles()) {
            //上传文件
            files.add(uploadFile(file, uploadBatchVO.getFileSource(), FileEnum.FilePlatform.LOCAL_1.getCode()));
        }
        //批量插入文件信息
        int insertBatchCount = entityService.insertBatch(files);
        log.info("批量文件上传，插入数据条数：{}", insertBatchCount);
        return BeanUtil.copyToList(files, FileUploadRespVO.class);
    }

    /**
     * 删除文件
     * @param fileId 文件ID
     */
    @Override
    public void deleteFile(Long fileId) {
        //查询文件
        MFile mFile = fileMapper.selectFileByFileId(fileId);
        Assert.notNull(mFile, () -> new BusinessException(FileEnum.ErrorMsg.NONENTITY_FILE.getDesc()));
        //文件已使用，不允许删除
        Assert.isFalse(FileEnum.FileStatus.NOTICE_STATUS_1.getCode() == mFile.getStatus().intValue(),
                () -> new BusinessException(FileEnum.ErrorMsg.FILE_USED.getDesc()));
        //文件信息
        FileInfo fileInfo = fileToFileInfo(mFile);
        //删除文件
        boolean deleteFile = fileStorageService.delete(fileInfo);
        log.info("删除文件，操作是否成功：{}", deleteFile);
        //删除数据库文件记录
        entityService.delete(MFile::getFileId, fileId);
    }

    /**
     * 查询文件列表(分页)
     * @param queryVO 查询条件
     * @return 文件分页数据
     */
    @Override
    public PageResp<FileVO> getPageFileList(FileQueryVO queryVO) {
        Page<MFile> filePage = fileMapper.selectPageFileList(queryVO);
        //数据转换
        List<FileVO> fileVOList = BeanUtil.copyToList(filePage.getRecords(), FileVO.class);
        return new PageResp<>(fileVOList, filePage.getTotal());
    }

    /**
     * 上传文件
     * @param file 文件
     * @param fileSource 文件来源
     * @param platform 文件存储平台
     * @return 文件实体
     */
    private MFile uploadFile(MultipartFile file, Integer fileSource, String platform) {
        //文件后缀
        String fileSuffix = FileNameUtil.extName(file.getOriginalFilename());
        //原文件名
        String oldFileName = file.getOriginalFilename();
        //新文件名
        String newFileName = IdUtil.objectId() + "." + fileSuffix;
        //文件ID
        long fileId = UnqIdUtil.uniqueId();
        //上传文件前配置
        FileInfo fileInfo = null;
        try {
            UploadPretreatment uploadPretreatment = fileStorageService.of(file)
                    //设置原文件名
                    .setOriginalFilename(oldFileName)
                    //设置新文件名
                    .setName(newFileName)
                    //设置存储平台，可以自行选择
                    .setPlatform(platform)
                    //设置相对路径
                    .setPath(FileEnum.FileSource.getPath(fileSource));
            //如果是图片，则生成缩略图
            if (StrUtil.isNotBlank(file.getContentType()) && file.getContentType().contains("image")) {
                uploadPretreatment.thumbnail(th -> th.size(350, 350), file);
            }
            //上传
            fileInfo = uploadPretreatment.upload();
        } catch (Exception e) {
            log.warn("文件上传失败：", e);
            throw new BusinessException(FileEnum.ErrorMsg.FILE_UPLOAD_FAIL.getDesc());
        }
        //文件实体
        MFile mFile = new MFile();
        mFile.setFileId(fileId);
        mFile.setFileName(oldFileName);
        mFile.setNewFileName(fileInfo.getFilename());
        mFile.setFileSize(fileInfo.getSize());
        mFile.setFileType(fileInfo.getContentType());
        mFile.setFilePath(fileInfo.getPath());
        mFile.setFileBasePath(fileInfo.getBasePath());
        mFile.setFileUrl(fileInfo.getUrl());
        mFile.setFileSource(fileSource);
        mFile.setFilePlatform(fileInfo.getPlatform());
        mFile.setFileThUrl(fileInfo.getThUrl());
        mFile.setFileThFilename(fileInfo.getThFilename());
        mFile.setFileThSize(fileInfo.getThSize());
        mFile.setFileThType(fileInfo.getThContentType());
        //返回文件实体
        return mFile;
    }

    /**
     * 文件实体转换为FileInfo
     * @param file 文件
     * @return FileInfo
     */
    private FileInfo fileToFileInfo(MFile file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUrl(file.getFileUrl());
        fileInfo.setSize(file.getFileSize());
        fileInfo.setFilename(file.getNewFileName());
        fileInfo.setOriginalFilename(file.getFileName());
        fileInfo.setBasePath(file.getFileBasePath());
        fileInfo.setPath(file.getFilePath());
        fileInfo.setContentType(file.getFileType());
        fileInfo.setPlatform(file.getFilePlatform());
        fileInfo.setThUrl(file.getFileThUrl());
        fileInfo.setThFilename(file.getFileThFilename());
        fileInfo.setThSize(file.getFileThSize());
        fileInfo.setThContentType(file.getFileThType());
        return fileInfo;
    }

}
