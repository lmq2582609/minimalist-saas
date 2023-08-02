package com.minimalist.basic.entity.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "字典数据实体 - 一般用于下拉框的数据展示或编码转换使用")
public class DictCacheVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Data
    @Schema(name = "字典key和value实体")
    public static class DictKV {

        @Schema(name = "dictKey", description = "字典key，字典编码", type = "string")
        private String dictKey;

        @Schema(name = "dictValue", description = "字典value，字典编码对应的中文", type = "string")
        private String dictValue;

        @Schema(name = "dictType", description = "字典类型", type = "string")
        private String dictType;

        @Schema(name = "dictClass", description = "字典样式，对应前端Tag组件的type", type = "string")
        private String dictClass;

    }

    @Schema(name = "dictType", description = "字典类型", type = "string")
    private String dictType;

    @Schema(name = "dictList", description = "字典列表", type = "array")
    private List<DictKV> dictList;

}


