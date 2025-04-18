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
     * 在前端文件选择组件上传文件时不需要指定文件来源，默认会上传到common目录，
     * 后端处理时可以将文件从common目录移动到对应业务的目录中
     * @param fileId 文件ID
     * @param fileSource 文件来源
     * @param status 文件状态
     * @param userId 操作人ID
     * @return 是否移动成功
     */
    boolean moveFile(Long fileId, Integer fileSource, Integer status, Long userId);

}
