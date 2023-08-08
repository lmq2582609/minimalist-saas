package com.minimalist.basic.entity.vo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.entity.vo.dept.DeptVO;
import com.minimalist.basic.entity.vo.perm.PermVO;
import com.minimalist.basic.entity.vo.post.PostVO;
import com.minimalist.common.security.user.UserEnum;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Schema(name = "用户实体")
public class UserInfoVO implements Serializable {

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

    @Schema(name = "userRealName", description = "用户真实姓名", type = "string")
    private String userRealName;

    @Schema(name = "email", description = "用户邮箱", type = "string")
    private String email;

    @Schema(name = "phone", description = "手机号码", type = "string")
    private String phone;

    @SchemaEnum(implementation = UserEnum.UserSex.class)
    @Schema(name = "userSex", description = "用户性别", type = "integer")
    private Integer userSex;

    @Schema(name = "userAvatar", description = "用户头像base64编码", type = "string")
    private String userAvatar;

    @Schema(name = "remark", description = "备注", type = "string")
    private String remark;

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

    @Schema(name = "deptList", description = "用户所属部门信息", type = "array")
    private List<DeptVO> deptList;

    @Schema(name = "postList", description = "用户岗位信息", type = "array")
    private List<PostVO> postList;

    @Schema(name = "menus", description = "用户菜单")
    private List<PermVO> menus;

}
