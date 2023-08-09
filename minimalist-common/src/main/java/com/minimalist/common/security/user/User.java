package com.minimalist.common.security.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Schema(name = "用户认证实体 Spring Security使用")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "userId", description = "用户ID", type = "string")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @Schema(name = "username", description = "用户账号", type = "string")
    private String username;

    /** 用户密码 */
    @JsonIgnore
    private String password;

    @Schema(name = "nickname", description = "用户昵称", type = "string")
    private String nickname;

    @SchemaEnum(implementation = UserEnum.UserStatus.class)
    @Schema(name = "status", description = "用户状态", type = "integer")
    private Integer status;

    @Schema(name = "tenantId", description = "租户编号", type = "string")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    @Schema(name = "perms", description = "用户权限标识符", type = "array")
    private Set<String> perms;

    @Schema(name = "roles", description = "用户角色标识符", type = "array")
    private Set<String> roles;

}
