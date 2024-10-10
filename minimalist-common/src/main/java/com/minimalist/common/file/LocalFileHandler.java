package com.minimalist.common.file;

import cn.hutool.json.JSONUtil;
import com.minimalist.common.file.entity.LocalFileEntity;
import com.minimalist.common.utils.ValidateUtil;
import org.springframework.stereotype.Service;

@Service
public class LocalFileHandler implements FileHandler {

    /**
     * 是否是对应的处理类
     * @param storageType 存储类型
     * @return 处理类
     */
    @Override
    public boolean isHandler(String storageType) {
        return FileEnum.StorageType.LOCAL.getCode().equals(storageType);
    }

    /**
     * 参数校验
     * @param storageConfig JSON存储配置信息
     */
    @Override
    public void valid(String storageConfig) {
        LocalFileEntity localFileEntity = JSONUtil.toBean(storageConfig, LocalFileEntity.class);
        ValidateUtil.valid(localFileEntity);
    }

}
