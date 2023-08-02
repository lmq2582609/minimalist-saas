package com.minimalist.basic.entity.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "用户重置密码实体")
public class RePasswordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "旧密码不能为空")
    @Schema(name = "oldPassword", description = "旧密码", type = "string")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Schema(name = "newPassword", description = "新密码", type = "string")
    private String newPassword;

}
