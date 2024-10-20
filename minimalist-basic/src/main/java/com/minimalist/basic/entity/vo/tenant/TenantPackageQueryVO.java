package com.minimalist.basic.entity.vo.tenant;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.mybatis.bo.PageReq;
import com.minimalist.basic.config.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "租户套餐查询实体")
public class TenantPackageQueryVO extends PageReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "packageName", description = "套餐名称", type = "string")
    private String packageName;

    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "租户套餐状态", type = "integer")
    private Byte status;

}
