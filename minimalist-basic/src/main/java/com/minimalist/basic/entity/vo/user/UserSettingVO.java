package com.minimalist.basic.entity.vo.user;

import com.minimalist.common.security.user.UserEnum;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "用户设置实体")
public class UserSettingVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户昵称不能为空")
    @Schema(name = "nickname", description = "用户昵称", type = "string")
    private String nickname;

    @NotBlank(message = "用户真实姓名不能为空")
    @Schema(name = "userRealName", description = "用户真实姓名", type = "string")
    private String userRealName;

    @NotBlank(message = "手机号不能为空")
    @Schema(name = "phone", description = "手机号码", type = "string")
    private String phone;

    @Schema(name = "email", description = "用户邮箱，可为空", type = "string")
    private String email;

    @NotNull(message = "用户性别不能为空")
    @SchemaEnum(implementation = UserEnum.UserSex.class)
    @Schema(name = "userSex", description = "用户性别", type = "integer")
    private Integer userSex;

}
