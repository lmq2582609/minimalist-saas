package com.minimalist.basic.entity.vo.tenant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.config.swagger.SchemaEnum;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.utils.Add;
import com.minimalist.basic.utils.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "租户数据源实体")
public class TenantDatasourceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "数据源ID不能为空", groups = {Update.class})
    @Schema(name = "datasourceId", description = "数据源ID", type = "string")
    private Long datasourceId;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "租户ID不能为空", groups = {Add.class, Update.class})
    @Schema(name = "tenantId", description = "租户ID", type = "string")
    private Long tenantId;

    @NotBlank(message = "数据源名称不能为空", groups = {Add.class, Update.class})
    @Schema(name = "datasourceName", description = "数据源连接", type = "string")
    private String datasourceName;

    @NotBlank(message = "数据源连接不能为空", groups = {Add.class, Update.class})
    @Schema(name = "datasourceUrl", description = "数据源连接", type = "string")
    private String datasourceUrl;

    @NotBlank(message = "数据源用户名不能为空", groups = {Add.class, Update.class})
    @Schema(name = "username", description = "数据源用户名", type = "string")
    private String username;

    @NotBlank(message = "数据源密码不能为空", groups = {Add.class, Update.class})
    @Schema(name = "password", description = "数据源密码", type = "string")
    private String password;

    @NotNull(message = "租户数据源状态不能为空", groups = {Update.class})
    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "租户状态", type = "integer")
    private Integer status;

    @Schema(name = "remark", description = "备注", type = "string")
    private String remark;

}
