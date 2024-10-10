package com.minimalist.common.file;

import cn.hutool.json.JSONUtil;
import com.minimalist.common.file.entity.MinIOFileEntity;
import com.minimalist.common.utils.ValidateUtil;
import org.springframework.stereotype.Service;

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
    public void valid(String storageConfig) {
        MinIOFileEntity minIOFileEntity = JSONUtil.toBean(storageConfig, MinIOFileEntity.class);
        ValidateUtil.valid(minIOFileEntity);
    }

}
