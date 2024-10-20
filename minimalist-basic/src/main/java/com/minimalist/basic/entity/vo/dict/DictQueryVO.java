package com.minimalist.basic.entity.vo.dict;

import com.minimalist.basic.config.mybatis.bo.PageReq;
import com.minimalist.basic.config.swagger.SchemaEnum;
import com.minimalist.basic.entity.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "字典查询实体")
public class DictQueryVO extends PageReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "dictName", description = "字典名称", type = "string")
    private String dictName;

    @Schema(name = "dictType", description = "字典类型", type = "string")
    private String dictType;

    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "字典状态", type = "int")
    private Byte status;

}
