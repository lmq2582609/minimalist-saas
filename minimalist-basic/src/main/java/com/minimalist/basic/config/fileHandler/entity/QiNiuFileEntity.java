package com.minimalist.basic.config.fileHandler.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QiNiuFileEntity {

    @NotBlank(message = "访问密钥不能为空")
    private String accessKey;

    @NotBlank(message = "私有密钥不能为空")
    private String secretKey;

    @NotBlank(message = "桶名称不能为空")
    private String bucketName;

    @NotBlank(message = "域名不能为空")
    private String endPoint;

    @NotBlank(message = "七牛云存储区域ID不能为空")
    private String regionId;

}
