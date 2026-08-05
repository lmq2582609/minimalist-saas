package com.minimalist.basic.entity.vo.funcConfig;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "功能配置实体")
public class FuncConfigVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "配置ID不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "configId", description = "配置ID", type = "string")
    private Long configId;

    @Schema(name = "configName", description = "配置名称", type = "string")
    private String configName;

    @Schema(name = "configKey", description = "配置键名", type = "string")
    private String configKey;

    @NotBlank(message = "配置键值不能为空")
    @Schema(name = "configValue", description = "配置键值", type = "string")
    private String configValue;

    @Schema(name = "description", description = "说明", type = "string")
    private String description;

}
