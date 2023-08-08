package com.minimalist.basic.entity.vo.tenant;

import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.common.mybatis.bo.Pager;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "租户查询实体")
public class TenantQueryVO extends Pager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "tenantName", description = "租户名", type = "string")
    private String tenantName;

    @SchemaEnum(implementation = TenantEnum.TenantStatus.class)
    @Schema(name = "status", description = "租户状态", type = "integer")
    private Byte status;

}
