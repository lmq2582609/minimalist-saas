package com.minimalist.basic.entity.vo.role;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.mybatis.bo.PageReq;
import com.minimalist.basic.config.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "角色查询实体")
public class RoleQueryVO extends PageReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "roleName", description = "角色名称", type = "string")
    private String roleName;

    @Schema(name = "roleCode", description = "角色编码", type = "string")
    private String roleCode;

    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "角色状态", type = "integer")
    private Integer status;

}
