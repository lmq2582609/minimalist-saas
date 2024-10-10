package com.minimalist.common.file;

public interface FileHandler {

    /**
     * 是否是对应的处理类
     * @param storageType 存储类型
     * @return 处理类
     */
    boolean isHandler(String storageType);

    /**
     * 参数校验
     * @param storageConfig JSON存储配置信息
     */
    void valid(String storageConfig);

}
