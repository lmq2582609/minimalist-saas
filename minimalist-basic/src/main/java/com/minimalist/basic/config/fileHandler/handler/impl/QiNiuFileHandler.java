package com.minimalist.basic.config.fileHandler.handler.impl;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.fileHandler.FileManager;
import com.minimalist.basic.config.fileHandler.entity.QiNiuFileEntity;
import com.minimalist.basic.config.fileHandler.handler.FileHandler;
import com.minimalist.basic.entity.enums.FileEnum;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.enums.StorageEnum;
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.mapper.MFileMapper;
import com.minimalist.basic.mapper.MStorageMapper;
import com.minimalist.basic.utils.CommonConstant;
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
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Slf4j
@Service
public class QiNiuFileHandler implements FileHandler {

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
        fileInfo.setFileUrl(URLUtil.normalize(qnConfig.getEndPoint() + "/" + fileKey));
        fileInfo.setFileSource(fileSource);
        fileInfo.setStorageId(storage.getStorageId());
        //如果未指定文件来源，将状态置为正常
        //因为这是从文件选择组件中上传的文件，不置为正常，在选择文件时查询条件是正常的才能被查出来
        if (fileSource < CommonConstant.ZERO) {
            fileInfo.setStatus(StatusEnum.STATUS_1.getCode());
        }
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
    public boolean moveFile(Long fileId, Integer fileSource, Integer status, Long userId) {
        MFile file = fileMapper.selectFileByFileId(fileId);
        Assert.notNull(file, () -> new BusinessException(FileEnum.ErrorMsg.NONENTITY_FILE.getDesc()));
        MStorage storage = storageMapper.selectStorageByStorageId(file.getStorageId());
        Assert.notNull(storage, () -> new BusinessException(StorageEnum.ErrorMsg.NONENTITY_STORAGE.getDesc()));
        try {
            QiNiuFileEntity qnConfig = JSONUtil.toBean(storage.getStorageConfig(), QiNiuFileEntity.class);
            Auth auth = Auth.create(qnConfig.getAccessKey(), qnConfig.getSecretKey());
            Configuration cfg = new Configuration(Region.createWithRegionId(qnConfig.getRegionId()));
            BucketManager bucketManager = new BucketManager(auth, cfg);
            //源文件名
            String fromKey = file.getFilePath() + "/" + file.getNewFileName();
            //基础路径 = 租户ID
            String basePath = SafetyUtil.getLoginUserTenantIdThrowException(String.class);
            //根据文件来源，获取相对路径
            String fileSourcePath = fileManager.getPathByFileSource(fileSource);
            //目标文件名
            String toKey = basePath + "/" + fileSourcePath + file.getNewFileName();
            //移动文件
            Response response = bucketManager.move(qnConfig.getBucketName(), fromKey, qnConfig.getBucketName(), toKey);
            if (!response.isOK()) {
                log.warn("移动文件失败：{}", JSONUtil.toJsonStr(response));
                return false;
            }
            //修改文件信息
            file.setFileSource(fileSource);
            file.setFilePath(basePath + "/" + fileSourcePath);
            file.setFileUrl(URLUtil.normalize(qnConfig.getEndPoint() + "/" + toKey));
            file.setStatus(status);
            file.setUpdateId(userId);
            file.setUpdateTime(LocalDateTime.now());
            //如果有缩略图，需要将缩略图移动
            if (StrUtil.isNotBlank(file.getFileThFilename())) {
                //源文件名
                String fromKeyTh = file.getFilePath() + "/" + file.getFileThFilename();
                //目标文件名
                String toKeyTh = basePath + "/" + fileSourcePath + file.getFileThFilename();
                //移动文件
                Response responseTh = bucketManager.move(qnConfig.getBucketName(), fromKeyTh, qnConfig.getBucketName(), toKeyTh);
                //修改缩略图url
                file.setFileThUrl(URLUtil.normalize(qnConfig.getEndPoint() + "/" + toKeyTh));
                if (!responseTh.isOK()) {
                    log.warn("移动文件失败：{}", JSONUtil.toJsonStr(responseTh));
                    return false;
                }
            }
            //更新文件信息
            fileMapper.updateByQuery(file, QueryWrapper.create().eq(MFile::getFileId, fileId));
        } catch (QiniuException ex) {
            log.error("移动文件失败，错误码：{}，错误信息：{}", ex.code(), ex.response.toString());
            log.error("移动文件失败：", ex);
        } catch (Exception e) {
            log.error("移动文件失败：", e);
        }
        return false;
    }

}
