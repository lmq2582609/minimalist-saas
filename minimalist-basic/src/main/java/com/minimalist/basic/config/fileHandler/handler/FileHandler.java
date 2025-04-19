package com.minimalist.basic.config.fileHandler.handler;

import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.po.MStorage;
import org.springframework.web.multipart.MultipartFile;

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
     * @return 格式化后的配置信息
     */
    String valid(String storageConfig);

    /**
     * 单文件上传
     * @param multipartFile 文件
     * @param fileSource 文件来源
     * @param storage 存储信息
     * @return 文件信息
     */
    MFile uploadFile(MultipartFile multipartFile, Integer fileSource, MStorage storage);

    /**
     * 删除文件
     * @param file 文件信息
     * @return 是否删除成功
     */
    boolean deleteFile(MFile file, MStorage storage);

    /**
     * 移动文件
     * @param file 文件信息
     * @param storage 存储信息
     * @return 是否成功
     */
    boolean moveFile(MFile file, MStorage storage);

}
