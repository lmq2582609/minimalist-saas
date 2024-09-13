package com.minimalist.basic.entity.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.common.convert.LongArrJsonSerializer;
import com.minimalist.basic.entity.enums.UserEnum;
import com.minimalist.common.swagger.SchemaEnum;
import com.minimalist.common.valid.Add;
import com.minimalist.common.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Schema(name = "用户管理实体")
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "用户ID不能为空", groups = {Update.class})
    @Schema(name = "userId", description = "用户ID", type = "string")
    private Long userId;

    @NotBlank(message = "用户账号不能为空", groups = {Add.class, Update.class})
    @Schema(name = "username", description = "用户账号", type = "string")
    private String username;

    @JsonSerialize(using = NullSerializer.class)
    @Schema(name = "password", description = "密码", type = "string")
    private String password;

    @NotBlank(message = "用户昵称不能为空", groups = {Add.class, Update.class})
    @Schema(name = "nickname", description = "用户昵称", type = "string")
    private String nickname;

    @NotBlank(message = "用户真实姓名不能为空", groups = {Add.class, Update.class})
    @Schema(name = "userRealName", description = "用户真实姓名", type = "string")
    private String userRealName;

    @NotBlank(message = "手机号不能为空", groups = {Add.class, Update.class})
    @Schema(name = "phone", description = "手机号码", type = "string")
    private String phone;

    @Schema(name = "email", description = "用户邮箱，可为空", type = "string")
    private String email;

    @NotNull(message = "用户性别不能为空", groups = {Add.class, Update.class})
    @SchemaEnum(implementation = UserEnum.UserSex.class)
    @Schema(name = "userSex", description = "用户性别", type = "integer")
    private Integer userSex;

    @Schema(name = "userAvatar", description = "用户头像base64编码", type = "string")
    private String userAvatar;

    @Schema(name = "remark", description = "备注", type = "string")
    private String remark;

    @NotNull(message = "用户状态不能为空", groups = {Add.class, Update.class})
    @SchemaEnum(implementation = UserEnum.UserStatus.class)
    @Schema(name = "status", description = "用户状态", type = "integer")
    private Integer status;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "tenantId", description = "租户编号", type = "string")
    private Long tenantId;

    @JsonSerialize(using = LongArrJsonSerializer.class)
    @NotEmpty(message = "角色不能为空", groups = {Add.class, Update.class})
    @Schema(name = "roles", description = "角色ID集合", type = "array")
    private Set<Long> roleIds;

    @JsonSerialize(using = LongArrJsonSerializer.class)
    @NotEmpty(message = "岗位不能为空", groups = {Add.class, Update.class})
    @Schema(name = "postIds", description = "岗位ID集合", type = "array")
    private Set<Long> postIds;

    @JsonSerialize(using = LongArrJsonSerializer.class)
    @NotEmpty(message = "部门不能为空", groups = {Add.class, Update.class})
    @Schema(name = "deptIds", description = "部门ID集合", type = "array")
    private Set<Long> deptIds;

    @NotEmpty(message = "部门不能为空", groups = {Add.class, Update.class})
    @Schema(name = "checkedPermIds", description = "用户部门编码集合，全勾选的节点", type = "array")
    private List<String> checkedDeptIds;

    @Schema(name = "allowDelete", description = "该数据是否可以被删除", type = "boolean")
    private Boolean allowDelete;

}
