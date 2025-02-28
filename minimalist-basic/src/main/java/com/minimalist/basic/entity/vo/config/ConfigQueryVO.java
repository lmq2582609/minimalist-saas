package com.minimalist.basic.entity.vo.config;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.mybatis.bo.PageReq;
import com.minimalist.basic.config.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "参数配置查询实体")
public class ConfigQueryVO extends PageReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "configName", description = "参数名称", type = "string")
    private String configName;

    @Schema(name = "configKey", description = "参数键名", type = "string")
    private String configKey;

    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "参数配置状态", type = "integer")
    private Integer status;

}
