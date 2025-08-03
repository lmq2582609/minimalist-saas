package com.minimalist.basic.config.fileHandler.handler.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.fileHandler.FileManager;
import com.minimalist.basic.config.fileHandler.entity.LocalFileEntity;
import com.minimalist.basic.config.fileHandler.handler.FileHandler;
import com.minimalist.basic.entity.enums.FileEnum;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.enums.StorageEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.mapper.MFileMapper;
import com.minimalist.basic.mapper.MStorageMapper;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.UnqIdUtil;
import com.minimalist.basic.utils.ValidateUtil;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
@Service
public class LocalFileHandler implements FileHandler {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private MFileMapper fileMapper;

    @Autowired
    private MStorageMapper storageMapper;

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
        fileInfo.setStorageId(storage.getStorageId());
        //如果未指定文件来源，将状态置为正常
        //因为这是从文件选择组件中上传的文件，不置为正常，在选择文件时查询条件是正常的才能被查出来
        if (fileSource < CommonConstant.ZERO) {
            fileInfo.setStatus(StatusEnum.STATUS_1.getCode());
        }
        try {
            log.info("上传文件，路径：{}", path);
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
            log.warn("上传文件，异常：", e);
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
    public boolean deleteFile(MFile file, MStorage storage) {
        String filePath = file.getFilePath() + file.getNewFileName();
        boolean result = FileUtil.del(filePath);
        //如果有缩略图，删除缩略图
        if (StrUtil.isNotBlank(file.getFileThFilename())) {
            String fileThPath = file.getFilePath() + file.getFileThFilename();
            FileUtil.del(fileThPath);
        }
        return result;
    }

    /**
     * 移动文件
     * @param file 文件信息
     * @param storage 存储信息
     * @return 是否成功
     */
    @Override
    public boolean moveFile(MFile file, MStorage storage) {
        //根据文件来源，获取相对路径
        String fileSourcePath = fileManager.getPathByFileSource(file.getFileSource());
        //本地存储路径
        LocalFileEntity localFileEntity = JSONUtil.toBean(storage.getStorageConfig(), LocalFileEntity.class);
        String storagePath = localFileEntity.getStoragePath();
        //request请求信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/files/";
        //源文件路径
        Path sourcePath = Paths.get(FileUtil.normalize(file.getFilePath() + "/" + file.getNewFileName()));
        //目标文件路径
        Path targetPath = Paths.get(FileUtil.normalize(storagePath + "/" + fileSourcePath + "/" + file.getNewFileName()));
        try {
            FileUtil.move(sourcePath, targetPath, true);
            //修改文件url
            file.setFileUrl(url + fileSourcePath + file.getNewFileName());
            //如果有缩略图，需要将缩略图移动
            if (StrUtil.isNotBlank(file.getFileThFilename())) {
                //源文件路径
                Path sourcePathTh = Paths.get(FileUtil.normalize(file.getFilePath() + "/" + file.getFileThFilename()));
                //目标文件路径
                Path targetPathTh = Paths.get(FileUtil.normalize(storagePath + "/" + fileSourcePath + "/" + file.getFileThFilename()));
                //移动缩略图
                FileUtil.move(sourcePathTh, targetPathTh, true);
                //修改缩略图url
                file.setFileThUrl(url + fileSourcePath + file.getFileThFilename());
            }
            //修改文件路径
            file.setFilePath(FileUtil.normalize(storagePath + "/" + fileSourcePath));
            return true;
        } catch (Exception e) {
            log.warn("移动文件，异常：", e);
        }
        return false;
    }

}
