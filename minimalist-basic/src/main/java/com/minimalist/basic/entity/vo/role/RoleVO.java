package com.minimalist.basic.entity.vo.role;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.entity.enums.RoleEnum;
import com.minimalist.common.convert.LongArrJsonSerializer;
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

@Data
@Schema(name = "角色实体")
public class RoleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "角色ID不能为空", groups = {Update.class})
    @Schema(name = "roleId", description = "角色ID", type = "string")
    private Long roleId;

    @NotBlank(message = "角色名称不能为空", groups = {Add.class, Update.class})
    @Schema(name = "roleName", description = "角色名称", type = "string")
    private String roleName;

    @NotBlank(message = "角色编码不能为空", groups = {Add.class, Update.class})
    @Schema(name = "roleCode", description = "角色编码", type = "string")
    private String roleCode;

    @NotNull(message = "排序值不能为空", groups = {Add.class, Update.class})
    @Schema(name = "roleSort", description = "排序", type = "integer")
    private Integer roleSort;

    @NotNull(message = "角色状态不能为空", groups = {Add.class, Update.class})
    @SchemaEnum(implementation = RoleEnum.RoleStatus.class)
    @Schema(name = "status", description = "角色状态", type = "integer")
    private Integer status;

    @Schema(name = "remark", description = "备注", type = "string")
    private String remark;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "tenantId", description = "租户ID", type = "string")
    private Long tenantId;

    @NotEmpty(message = "角色权限不能为空", groups = {Add.class, Update.class})
    @Schema(name = "checkedPermIds", description = "角色权限编码集合，全勾选的节点，用于回显", type = "array")
    private List<String> checkedPermIds;

    @JsonSerialize(using = LongArrJsonSerializer.class)
    @NotEmpty(message = "角色权限不能为空", groups = {Add.class, Update.class})
    @Schema(name = "permissionsIds", description = "角色权限编码集合，全勾选+半勾选的节点", type = "array")
    private List<Long> permissionsIds;

}
