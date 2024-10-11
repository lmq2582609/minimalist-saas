package com.minimalist.basic.entity.vo.dict;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.common.enums.StatusEnum;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "字典数据实体")
public class DictDataVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "dictId", description = "字典ID", type = "string")
    private Long dictId;

    @NotBlank(message = "字典key不能为空")
    @Schema(name = "dictKey", description = "字典key", type = "string")
    private String dictKey;

    @NotBlank(message = "字典value不能为空")
    @Schema(name = "dictValue", description = "字典value", type = "string")
    private String dictValue;

    @NotNull(message = "字典排序值不能为空")
    @Schema(name = "dictOrder", description = "字典排序值", type = "integer")
    private Integer dictOrder;

    @Schema(name = "dictClass", description = "字典样式，对应前端Tag组件的type", type = "string")
    private String dictClass;

    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "字典状态", type = "integer")
    private Integer status;

}
