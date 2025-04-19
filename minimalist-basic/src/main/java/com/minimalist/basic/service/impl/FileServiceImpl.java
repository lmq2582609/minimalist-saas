package com.minimalist.basic.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.fileHandler.FileManager;
import com.minimalist.basic.config.fileHandler.handler.FileHandler;
import com.minimalist.basic.entity.enums.FileEnum;
import com.minimalist.basic.entity.enums.StorageEnum;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.entity.po.MTenant;
import com.minimalist.basic.entity.vo.file.*;
import com.minimalist.basic.mapper.MFileMapper;
import com.minimalist.basic.mapper.MStorageMapper;
import com.minimalist.basic.mapper.MTenantMapper;
import com.minimalist.basic.service.FileService;
import com.minimalist.basic.utils.SafetyUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MFileMapper fileMapper;

    @Autowired
    private MStorageMapper storageMapper;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private MTenantMapper tenantMapper;

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
        fileMapper.insert(mFile, true);
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
        fileMapper.insertBatch(files);
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
        MStorage storage = getStorage(mFile.getStorageId());
        FileHandler fileHandler = fileManager.getFileHandler(storage.getStorageType());
        //删除文件
        boolean deleteFile = fileHandler.deleteFile(mFile, storage);
        if (!deleteFile) {
            throw new BusinessException(FileEnum.ErrorMsg.FILE_DELETE_FAIL.getDesc());
        }
        //删除数据库文件记录
        LogicDeleteManager.execWithoutLogicDelete(()->
                fileMapper.deleteByQuery(QueryWrapper.create().eq(MFile::getFileId, fileId))
        );
    }

    /**
     * 移动文件
     * 在前端文件选择组件上传文件时不需要指定文件来源，默认会上传到common目录，
     * 后端处理时可以将文件从common目录移动到对应业务的目录中
     * @param fileId     文件ID
     * @param fileSource 文件来源
     * @param status     文件状态
     * @return 是否移动成功
     */
    @Override
    public MFile moveFile(Long fileId, Integer fileSource, Integer status) {
        //查询文件
        MFile file = fileMapper.selectFileByFileId(fileId);
        Assert.notNull(file, () -> new BusinessException(FileEnum.ErrorMsg.NONENTITY_FILE.getDesc()));
        MStorage storage = getStorage(file.getStorageId());
        //设置文件来源 - 在文件选择中上传没有文件来源，所以这里要设置
        file.setFileSource(fileSource);
        //修改文件信息
        file.setStatus(status);
        file.setUpdateId(StpUtil.getLoginIdAsLong());
        file.setUpdateTime(LocalDateTime.now());
        //移动文件
        FileHandler fileHandler = fileManager.getFileHandler(storage.getStorageType());
        fileHandler.moveFile(file, storage);
        //更新文件信息
        fileMapper.updateByQuery(file, QueryWrapper.create().eq(MFile::getFileId, fileId));
        return file;
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
        return new PageResp<>(fileVOList, filePage.getTotalRow());
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
        //未指定存储，根据租户ID获取
        MTenant tenant = tenantMapper.selectTenantByTenantId(SafetyUtil.getLoginUserTenantId());
        Assert.notNull(tenant, () -> new BusinessException(TenantEnum.ErrorMsg.QUERY_NULL_TENANT.getDesc()));
        return storageMapper.selectStorageByStorageId(tenant.getStorageId());
    }

}
