package com.minimalist.basic.config.fileHandler.handler.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.fileHandler.FileManager;
import com.minimalist.basic.config.fileHandler.entity.LocalFileEntity;
import com.minimalist.basic.config.fileHandler.handler.FileHandler;
import com.minimalist.basic.entity.enums.FileEnum;
import com.minimalist.basic.entity.enums.StorageEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.utils.UnqIdUtil;
import com.minimalist.basic.utils.ValidateUtil;
import jakarta.servlet.http.HttpServletRequest;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class LocalFileHandler implements FileHandler {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private FileManager fileManager;

    /**
     * 是否是对应的处理类
     * @param storageType 存储类型
     * @return 处理类
     */
    @Override
    public boolean isHandler(String storageType) {
        return StorageEnum.StorageType.LOCAL.getCode().equals(storageType);
    }

    /**
     * 参数校验
     * @param storageConfig JSON存储配置信息
     */
    @Override
    public String valid(String storageConfig) {
        LocalFileEntity localFileEntity = JSONUtil.toBean(storageConfig, LocalFileEntity.class);
        ValidateUtil.valid(localFileEntity);
        //格式化路径
        String normalize = FileUtil.normalize(localFileEntity.getStoragePath());
        localFileEntity.setStoragePath(normalize);
        return JSONUtil.toJsonStr(localFileEntity);
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
        //根据文件来源，获取相对路径
        String fileSourcePath = fileManager.getPathByFileSource(fileSource);
        //本地存储路径
        LocalFileEntity localFileEntity = JSONUtil.toBean(storage.getStorageConfig(), LocalFileEntity.class);
        String storagePath = localFileEntity.getStoragePath();
        //文件后缀
        String fileSuffix = FileNameUtil.extName(multipartFile.getOriginalFilename());
        //新文件名
        String newFileName = IdUtil.objectId() + "." + fileSuffix;
        //拼接路径
        String path = FileUtil.normalize(storagePath + "/" + fileSourcePath + newFileName);
        //request请求信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/files/";
        //返回的文件信息
        MFile fileInfo = new MFile();
        fileInfo.setFileId(UnqIdUtil.uniqueId());
        fileInfo.setFileName(multipartFile.getOriginalFilename());
        fileInfo.setNewFileName(newFileName);
        fileInfo.setFileSize(multipartFile.getSize());
        fileInfo.setFileType(multipartFile.getContentType());
        fileInfo.setFileBasePath(storagePath);
        fileInfo.setFilePath(FileUtil.normalize(storagePath + "/" + fileSourcePath));
        fileInfo.setFileUrl(url + fileSourcePath + newFileName);
        fileInfo.setFileSource(fileSource);
        fileInfo.setStorageType(storage.getStorageType());
        try {
            //上传文件
            File file = FileUtil.touch(path);
            multipartFile.transferTo(file);
            if (StrUtil.isNotBlank(multipartFile.getContentType()) && multipartFile.getContentType().contains("image")) {
                //如果是图片，生成缩略图
                String thFileName = "thumbnail-" + newFileName;
                String thPath = FileUtil.normalize(storagePath + "/" + fileSourcePath + "/" + thFileName);
                File thFile = FileUtil.touch(thPath);
                Thumbnails.of(file)
                        .scale(0.4)
                        .toFile(thFile);
                fileInfo.setFileThUrl(url + fileSourcePath + thFileName);
                fileInfo.setFileThFilename(thFileName);
                fileInfo.setFileThSize(thFile.length());
            }
        } catch (IOException e) {
            FileUtil.del(path);
            throw new BusinessException(FileEnum.ErrorMsg.FILE_UPLOAD_FAIL.getDesc());
        }


        return fileInfo;
    }

    /**
     * 删除文件
     * @param file 文件信息
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(MFile file) {
        String filePath = file.getFilePath() + file.getNewFileName();
        boolean result = FileUtil.del(filePath);
        //如果有缩略图，删除缩略图
        if (StrUtil.isNotBlank(file.getFileThFilename())) {
            String fileThPath = file.getFilePath() + file.getFileThFilename();
            FileUtil.del(fileThPath);
        }
        return result;
    }

}
