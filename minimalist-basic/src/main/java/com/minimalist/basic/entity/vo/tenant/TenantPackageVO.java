package com.minimalist.basic.entity.vo.tenant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.common.convert.LongArrJsonSerializer;
import com.minimalist.common.swagger.SchemaEnum;
import com.minimalist.common.valid.Add;
import com.minimalist.common.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "租户套餐实体")
public class TenantPackageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "租户套餐ID不能为空", groups = {Update.class})
    @Schema(name = "packageId", description = "租户套餐ID", type = "string")
    private Long packageId;

    @NotNull(message = "套餐名称能为空", groups = {Add.class, Update.class})
    @Schema(name = "packageName", description = "套餐名称", type = "string")
    private String packageName;

    @NotNull(message = "租户套餐状态不能为空", groups = {Add.class, Update.class})
    @SchemaEnum(implementation = TenantEnum.TenantPackageStatus.class)
    @Schema(name = "status", description = "租户套餐状态", type = "integer")
    private Byte status;

    @Schema(name = "remark", description = "备注", type = "string")
    private String remark;

    @NotEmpty(message = "租户套餐权限不能为空", groups = {Add.class, Update.class})
    @Schema(name = "checkedPermIds", description = "租户套餐权限编码集合，全勾选的节点", type = "array")
    private List<String> checkedPermIds;

    @JsonSerialize(using = LongArrJsonSerializer.class)
    @NotEmpty(message = "租户套餐权限不能为空", groups = {Add.class, Update.class})
    @Schema(name = "permissionsIds", description = "租户套餐权限编码集合", type = "array")
    private List<Long> permissionsIds;

}
