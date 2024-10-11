package com.minimalist.common.fileHandler.handler.impl;

import cn.hutool.json.JSONUtil;
import com.minimalist.common.module.entity.enums.FileEnum;
import com.minimalist.common.fileHandler.entity.MinIOFileEntity;
import com.minimalist.common.fileHandler.handler.FileHandler;
import com.minimalist.common.module.entity.po.MFile;
import com.minimalist.common.module.entity.po.MStorage;
import com.minimalist.common.utils.ValidateUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinIOFileHandler implements FileHandler {

    /**
     * 是否是对应的处理类
     * @param storageType 存储类型
     * @return 处理类
     */
    @Override
    public boolean isHandler(String storageType) {
        return FileEnum.StorageType.MINIO.getCode().equals(storageType);
    }

    /**
     * 参数校验
     * @param storageConfig JSON存储配置信息
     */
    @Override
    public String valid(String storageConfig) {
        MinIOFileEntity minIOFileEntity = JSONUtil.toBean(storageConfig, MinIOFileEntity.class);
        ValidateUtil.valid(minIOFileEntity);
        return storageConfig;
    }

    /**
     * 单文件上传
     * @param multipartFile 文件
     * @param fileSource 文件来源
     * @param storage 存储信息
     * @return 文件信息
     */
    @Override
    public MFile uploadFile(MultipartFile multipartFile, Integer fileSource, MStorage storage) {
        return null;
    }

    /**
     * 删除文件
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(Long fileId) {
        return false;
    }

}
