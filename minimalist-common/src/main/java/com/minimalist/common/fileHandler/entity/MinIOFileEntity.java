package com.minimalist.common.fileHandler.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MinIOFileEntity {

    @NotBlank(message = "访问密钥不能为空")
    private String accessKey;

    @NotBlank(message = "私有密钥不能为空")
    private String secretKey;

    @NotBlank(message = "域名不能为空")
    private String endPoint;

    @NotBlank(message = "桶名称不能为空")
    private String bucketName;

}
