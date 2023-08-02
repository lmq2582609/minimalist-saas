package com.minimalist.basic.entity.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "用户登录请求实体")
public class UserLoginReqVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    @Schema(name = "username", description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(name = "password", description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
    private String password;

    @Schema(name = "captchaId", description = "验证码ID，与验证码一一对应", type = "string")
    private String captchaId;

    @Schema(name = "captcha", description = "验证码", type = "string")
    private String captcha;

}
