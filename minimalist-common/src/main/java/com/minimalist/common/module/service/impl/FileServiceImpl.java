package com.minimalist.common.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.common.enums.StatusEnum;
import com.minimalist.common.fileHandler.FileManager;
import com.minimalist.common.fileHandler.handler.FileHandler;
import com.minimalist.common.module.entity.enums.FileEnum;
import com.minimalist.common.module.entity.enums.StorageEnum;
import com.minimalist.common.module.entity.po.MFile;
import com.minimalist.common.module.entity.po.MStorage;
import com.minimalist.common.module.mapper.MFileMapper;
import com.minimalist.common.module.mapper.MStorageMapper;
import com.minimalist.common.module.service.FileService;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.module.entity.vo.file.*;
import com.minimalist.common.mybatis.EntityService;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.utils.UnqIdUtil;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.upload.UploadPretreatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MFileMapper fileMapper;

    @Autowired
    private EntityService entityService;

    @Autowired
    private MStorageMapper storageMapper;

    @Autowired
    private FileManager fileManager;

    /**
     * 单文件上传
     * @param fileUploadVO 文件及携带参数
     * @return 文件信息
     */
    @Override
    public FileUploadRespVO uploadFile(FileUploadVO fileUploadVO) {
        MStorage storage = getStorage(fileUploadVO.getStorageId());
        Assert.notNull(storage, () -> new BusinessException(StorageEnum.ErrorMsg.NONENTITY_STORAGE.getDesc()));
        FileHandler fileHandler = fileManager.getFileHandler(storage.getStorageType());
        //上传文件
        MFile mFile = fileHandler.uploadFile(fileUploadVO.getFile(), fileUploadVO.getFileSource(), storage);
        fileMapper.insert(mFile);
        return BeanUtil.copyProperties(mFile, FileUploadRespVO.class);
    }

    /**
     * 多上传文件
     * @param uploadBatchVO 文件及携带参数
     * @return 文件信息
     */
    @Override
    public List<FileUploadRespVO> uploadFileBatch(FileUploadBatchVO uploadBatchVO) {
        MStorage storage = getStorage(uploadBatchVO.getStorageId());
        Assert.notNull(storage, () -> new BusinessException(StorageEnum.ErrorMsg.NONENTITY_STORAGE.getDesc()));
        FileHandler fileHandler = fileManager.getFileHandler(storage.getStorageType());
        //循环上传多个文件
        List<MFile> files = CollectionUtil.list(false);
        for (MultipartFile file : uploadBatchVO.getFiles()) {
            MFile mFile = fileHandler.uploadFile(file, uploadBatchVO.getFileSource(), storage);
            files.add(mFile);
        }
        //批量插入文件信息
        entityService.insertBatch(files);
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
        Assert.isFalse(StatusEnum.STATUS_1.getCode().equals(mFile.getStatus()),
                () -> new BusinessException(FileEnum.ErrorMsg.FILE_USED.getDesc()));
        FileHandler fileHandler = fileManager.getFileHandler(mFile.getStorageType());
        //删除文件
        fileHandler.deleteFile(fileId);
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
                uploadPretreatment.thumbnailOf(file);
            }
            //上传
            fileInfo = uploadPretreatment.upload();
        } catch (Exception e) {
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
        //mFile.setFilePlatform(fileInfo.getPlatform());
        mFile.setFileThUrl(fileInfo.getThUrl());
        mFile.setFileThFilename(fileInfo.getThFilename());
        mFile.setFileThSize(fileInfo.getThSize());
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
        //fileInfo.setPlatform(file.getFilePlatform());
        fileInfo.setThUrl(file.getFileThUrl());
        fileInfo.setThFilename(file.getFileThFilename());
        fileInfo.setThSize(file.getFileThSize());
        return fileInfo;
    }

    /**
     * 获取存储信息
     * @param storageId 存储ID
     * @return 存储信息
     */
    private MStorage getStorage(Long storageId) {
        if (ObjectUtil.isNotNull(storageId)) {
            return storageMapper.selectStorageByStorageId(storageId);
        }
        //未指定存储，取默认
        return storageMapper.selectStorageByDefault();
    }

}
