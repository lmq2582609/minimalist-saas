package com.minimalist.basic.entity.vo.dept;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.config.swagger.SchemaEnum;
import com.minimalist.basic.entity.valid.Add;
import com.minimalist.basic.entity.valid.Update;
import com.minimalist.basic.entity.enums.DeptEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "部门实体")
public class DeptVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "部门ID不能为空", groups = {Update.class})
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "deptId", description = "部门id", type = "string")
    private Long deptId;

    @NotNull(message = "上级部门ID不能为空", groups = {Add.class, Update.class})
    @Schema(name = "parentDeptId", description = "父部门ID", type = "string")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentDeptId;

    @NotBlank(message = "部门名称不能为空", groups = {Add.class, Update.class})
    @Schema(name = "deptName", description = "部门名称", type = "string")
    private String deptName;

    @NotNull(message = "排序值不能为空", groups = {Add.class, Update.class})
    @Schema(name = "deptSort", description = "排序值", type = "integer")
    private Integer deptSort;

    @NotNull(message = "部门负责人不能为空", groups = {Add.class, Update.class})
    @Schema(name = "deptLeader", description = "部门负责人", type = "string")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptLeader;

    @NotBlank(message = "部门电话不能为空", groups = {Add.class, Update.class})
    @Schema(name = "phone", description = "部门电话", type = "string")
    private String phone;

    @NotBlank(message = "部门邮箱不能为空", groups = {Add.class, Update.class})
    @Schema(name = "email", description = "部门邮箱", type = "string")
    private String email;

    @SchemaEnum(implementation = DeptEnum.DeptStatus.class)
    @NotNull(message = "部门状态不能为空", groups = {Add.class, Update.class})
    @Schema(name = "status", description = "部门状态", type = "integer")
    private Integer status;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "tenantId", description = "租户ID", type = "string")
    private Long tenantId;

    @Schema(name = "children", description = "部门子集", type = "array")
    private List<DeptVO> children;

}
