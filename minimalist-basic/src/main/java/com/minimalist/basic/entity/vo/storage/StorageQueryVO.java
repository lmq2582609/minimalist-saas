package com.minimalist.basic.entity.vo.storage;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.mybatis.bo.PageReq;
import com.minimalist.basic.config.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "存储信息查询实体")
public class StorageQueryVO extends PageReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "storageName", description = "存储名称", type = "string")
    private String storageName;

    @Schema(name = "storageType", description = "存储类型", type = "string")
    private String storageType;

    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "存储状态", type = "integer")
    private Integer status;

}
