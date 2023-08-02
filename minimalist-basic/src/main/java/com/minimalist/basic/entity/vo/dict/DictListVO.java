package com.minimalist.basic.entity.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "字典列表实体")
public class DictListVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "dictType", description = "字典类型", type = "string")
    private String dictType;

    @Schema(name = "dictList", description = "字典列表", type = "array")
    private List<DictVO> dictList;

}
