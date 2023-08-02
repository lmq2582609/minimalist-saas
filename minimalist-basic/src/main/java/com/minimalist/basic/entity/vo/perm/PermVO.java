package com.minimalist.basic.entity.vo.perm;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.config.swagger.SchemaEnum;
import com.minimalist.basic.entity.valid.Add;
import com.minimalist.basic.entity.valid.Update;
import com.minimalist.basic.entity.enums.PermEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.util.List;
import java.io.Serializable;

@Data
@Schema(name = "权限实体")
public class PermVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "权限ID不能为空", groups = {Update.class})
    @Schema(name = "permId", description = "权限ID", type = "string")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long permId;

    @Schema(name = "permCode", description = "权限编码", type = "string")
    private String permCode;

    @NotBlank(message = "权限名称不能为空", groups = {Add.class, Update.class})
    @Schema(name = "permName", description = "权限名称", type = "string")
    private String permName;

    @NotNull(message = "上级权限ID不能为空", groups = {Add.class, Update.class})
    @Schema(name = "parentPermId", description = "父权限ID", type = "string")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentPermId;

    @NotNull(message = "排序值不能为空", groups = {Add.class, Update.class})
    @Schema(name = "permSort", description = "排序值", type = "integer")
    private Integer permSort;

    @Schema(name = "permPath", description = "路由地址", type = "string")
    private String permPath;

    @Schema(name = "permIcon", description = "权限图标，菜单可传图标", type = "string")
    private String permIcon;

    @SchemaEnum(implementation = PermEnum.PermType.class)
    @Schema(name = "permType", description = "权限类型", type = "string")
    private String permType;

    @Schema(name = "component", description = "组件路径", type = "string")
    private String component;

    @NotNull(message = "是否可见不能为空", groups = {Add.class, Update.class})
    @Schema(name = "visible", description = "是否可见", type = "boolean")
    private Boolean visible;

    @SchemaEnum(implementation = PermEnum.PermStatus.class)
    @NotNull(message = "权限状态不能为空", groups = {Add.class, Update.class})
    @Schema(name = "status", description = "权限状态", type = "integer")
    private Integer status;

    @Schema(name = "remark", description = "备注", type = "string")
    private String remark;

    @Schema(name = "children", description = "权限子集", type = "array")
    private List<PermVO> children;

}
