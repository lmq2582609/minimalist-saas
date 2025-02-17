package com.minimalist.basic.config.fileHandler.handler.impl;

import com.minimalist.basic.config.fileHandler.handler.FileHandler;
import com.minimalist.basic.entity.enums.StorageEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.po.MStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class QiNiuFileHandler implements FileHandler {

    /**
     * 是否是对应的处理类
     * @param storageType 存储类型
     * @return 处理类
     */
    @Override
    public boolean isHandler(String storageType) {
        return StorageEnum.StorageType.QINIU.getCode().equals(storageType);
    }

    /**
     * 参数校验
     * @param storageConfig JSON存储配置信息
     */
    @Override
    public String valid(String storageConfig) {
        return null;
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
     * @param file 文件信息
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(MFile file) {
        return false;
    }

}
