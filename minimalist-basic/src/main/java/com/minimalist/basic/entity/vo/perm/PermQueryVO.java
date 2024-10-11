package com.minimalist.basic.entity.vo.perm;

import com.minimalist.common.enums.StatusEnum;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "权限查询实体")
public class PermQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "permName", description = "权限名称", type = "string")
    private String permName;

    @Schema(name = "permType", description = "权限类型", type = "string")
    private String permType;

    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "状态", type = "integer")
    private Integer status;

}
