package com.minimalist.basic.config.fileHandler.handler.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.fileHandler.FileManager;
import com.minimalist.basic.config.fileHandler.entity.MinIOFileEntity;
import com.minimalist.basic.config.fileHandler.entity.QiNiuFileEntity;
import com.minimalist.basic.config.fileHandler.handler.FileHandler;
import com.minimalist.basic.entity.enums.FileEnum;
import com.minimalist.basic.entity.enums.StorageEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.mapper.MFileMapper;
import com.minimalist.basic.mapper.MStorageMapper;
import com.minimalist.basic.utils.SafetyUtil;
import com.minimalist.basic.utils.UnqIdUtil;
import com.minimalist.basic.utils.ValidateUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class QiNiuFileHandler implements FileHandler {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private MStorageMapper storageMapper;

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
        QiNiuFileEntity qiNiuFileEntity = JSONUtil.toBean(storageConfig, QiNiuFileEntity.class);
        ValidateUtil.valid(qiNiuFileEntity);
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
        //根据文件来源，获取相对路径
        String fileSourcePath = fileManager.getPathByFileSource(fileSource);
        //文件后缀
        String fileSuffix = FileNameUtil.extName(multipartFile.getOriginalFilename());
        //新文件名
        String newFileName = IdUtil.objectId() + "." + fileSuffix;
        //文件信息
        QiNiuFileEntity qnConfig = JSONUtil.toBean(storage.getStorageConfig(), QiNiuFileEntity.class);
        MFile fileInfo = new MFile();
        fileInfo.setFileId(UnqIdUtil.uniqueId());
        fileInfo.setFileName(multipartFile.getOriginalFilename());
        fileInfo.setNewFileName(newFileName);
        fileInfo.setFileSize(multipartFile.getSize());
        fileInfo.setFileType(multipartFile.getContentType());
        //基础路径 = 租户ID
        String basePath = SafetyUtil.getLoginUserTenantIdThrowException(String.class);
        fileInfo.setFileBasePath(basePath);
        String fileKey = basePath + "/" + fileSourcePath + newFileName;
        fileInfo.setFilePath(basePath + "/" + fileSourcePath);
        fileInfo.setFileUrl(qnConfig.getEndPoint() + fileKey);
        fileInfo.setFileSource(fileSource);
        fileInfo.setStorageId(storage.getStorageId());
        try {
            Auth auth = Auth.create(qnConfig.getAccessKey(), qnConfig.getSecretKey());
            String upToken = auth.uploadToken(qnConfig.getBucketName());
            Configuration cfg = new Configuration(Region.createWithRegionId(qnConfig.getRegionId()));
            UploadManager uploadManager = new UploadManager(cfg);
            StringMap params = new StringMap();
            params.put("tenantId", basePath);
            Response response = uploadManager.put(multipartFile.getInputStream(), fileKey, upToken, params, null);
            if (!response.isOK()) {
                log.error("上传文件失败：{}", JSONUtil.toJsonStr(response));
                throw new BusinessException(FileEnum.ErrorMsg.FILE_UPLOAD_FAIL.getDesc());
            }
            //生成图片缩略图
            if (StrUtil.isNotBlank(multipartFile.getContentType()) && multipartFile.getContentType().contains("image")) {
                ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream();
                Thumbnails.of(multipartFile.getInputStream())
                        .scale(0.4)
                        .toOutputStream(thumbnailOutputStream);
                byte[] fileByte = thumbnailOutputStream.toByteArray();
                String thFileName = "thumbnail-" + newFileName;
                String thumbnailsFileKey = basePath + "/" + fileSourcePath + thFileName;
                Response thumbnailsResponse = uploadManager.put(fileByte, thumbnailsFileKey, upToken, params, null, false);
                if (thumbnailsResponse.isOK()) {
                    fileInfo.setFileThUrl(qnConfig.getEndPoint() + thumbnailsFileKey);
                    fileInfo.setFileThFilename(thFileName);
                    fileInfo.setFileThSize((long) fileByte.length);
                } else {
                    log.error("上传缩略图失败：{}", JSONUtil.toJsonStr(thumbnailsResponse));
                    throw new BusinessException(FileEnum.ErrorMsg.FILE_THUMBNAILS_UPLOAD_FAIL.getDesc());
                }
            }
        } catch (Exception e) {
            //删除刚上传的图片
            deleteFile(fileInfo, storage);
            log.error("上传文件，异常：", e);
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
        try {
            QiNiuFileEntity qnConfig = JSONUtil.toBean(storage.getStorageConfig(), QiNiuFileEntity.class);
            Auth auth = Auth.create(qnConfig.getAccessKey(), qnConfig.getSecretKey());
            Configuration cfg = new Configuration(Region.createWithRegionId(qnConfig.getRegionId()));
            BucketManager bucketManager = new BucketManager(auth, cfg);
            Response response = bucketManager.delete(qnConfig.getBucketName(), file.getFilePath() + file.getNewFileName());
            if (!response.isOK()) {
                log.warn("删除文件失败：{}", JSONUtil.toJsonStr(response));
                return false;
            }
            //如果存在缩略图，删除
            if (StrUtil.isNotBlank(file.getFileThFilename())) {
                //删除缩略图
                bucketManager.delete(qnConfig.getBucketName(), file.getFilePath() + file.getFileThFilename());
            }
            return true;
        } catch (Exception e) {
            log.error("删除文件失败：", e);
        }
        return false;
    }

}
