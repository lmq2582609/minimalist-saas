package com.minimalist.basic.entity.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "字典基础信息实体")
public class DictInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "字典类型不能为空")
    @Schema(name = "dictType", description = "字典类型", type = "string")
    private String dictType;

    @NotBlank(message = "字典名称不能为空")
    @Schema(name = "dictName", description = "字典名称", type = "string")
    private String dictName;

    @NotBlank(message = "字典描述不能为空")
    @Schema(name = "dictDesc", description = "字典描述", type = "string")
    private String dictDesc;

    @NotEmpty(message = "字典数据不能为空")
    @Schema(name = "dictDataList", description = "字典数据列表", type = "array")
    private List<DictDataVO> dictDataList;

}
