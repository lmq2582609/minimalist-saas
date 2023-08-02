package com.minimalist.basic.entity.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "图形验证码响应实体")
public class ImageCaptchaVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "enable", description = "是否启用验证码", type = "boolean")
    private boolean enable;

    @Schema(name = "captchaId", description = "验证码ID", type = "string")
    private String captchaId;

    @Schema(name = "captchaImg", description = "验证码图片Base64编码", type = "string")
    private String captchaImg;

}
