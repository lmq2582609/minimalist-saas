package com.minimalist.basic.entity.vo.storage;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.common.enums.StatusEnum;
import com.minimalist.common.swagger.SchemaEnum;
import com.minimalist.common.valid.Add;
import com.minimalist.common.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "存储信息实体")
public class StorageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "存储ID不能为空", groups = {Update.class})
    @Schema(name = "storageId", description = "存储ID", type = "string")
    private Long storageId;

    @NotBlank(message = "存储名称不能为空", groups = {Add.class, Update.class})
    @Schema(name = "storageName", description = "存储名称", type = "string")
    private String storageName;

    @NotBlank(message = "存储类型不能为空", groups = {Add.class, Update.class})
    @Schema(name = "storageType", description = "存储类型", type = "string")
    private String storageType;

    @NotNull(message = "是否默认使用不能为空", groups = {Add.class, Update.class})
    @Schema(name = "storageDefault", description = "排序", type = "integer")
    private Boolean storageDefault;

    @Schema(name = "description", description = "说明", type = "integer")
    private String description;

    @NotBlank(message = "存储配置不能为空", groups = {Add.class, Update.class})
    @Schema(name = "storageConfig", description = "存储配置", type = "string")
    private String storageConfig;

    @NotNull(message = "存储状态不能为空", groups = {Update.class})
    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "存储状态", type = "integer")
    private Integer status;

}
