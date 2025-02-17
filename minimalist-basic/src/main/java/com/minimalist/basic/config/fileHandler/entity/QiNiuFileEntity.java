package com.minimalist.basic.config.fileHandler.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QiNiuFileEntity {

    @NotBlank(message = "访问密钥不能为空")
    private String accessKey;

    @NotBlank(message = "私有密钥不能为空")
    private String secretKey;

}
