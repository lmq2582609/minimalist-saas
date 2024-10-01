package com.minimalist.basic.entity.vo.dict;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.entity.enums.DictEnum;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "字典管理实体")
public class DictVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "dictId", description = "字典ID", type = "string")
    private Long dictId;

    @Schema(name = "dictType", description = "字典类型", type = "string")
    private String dictType;

    @Schema(name = "dictKey", description = "字典key", type = "string")
    private String dictKey;

    @Schema(name = "dictValue", description = "字典value", type = "string")
    private String dictValue;

    @Schema(name = "dictName", description = "字典名称", type = "string")
    private String dictName;

    @Schema(name = "dictDesc", description = "字典描述", type = "string")
    private String dictDesc;

    @Schema(name = "dictOrder", description = "字典排序值", type = "integer")
    private Integer dictOrder;

    @Schema(name = "dictClass", description = "字典样式，对应前端Tag组件的type", type = "string")
    private String dictClass;

    @SchemaEnum(implementation = DictEnum.Status.class)
    @Schema(name = "status", description = "字典状态", type = "integer")
    private Integer status;

}
