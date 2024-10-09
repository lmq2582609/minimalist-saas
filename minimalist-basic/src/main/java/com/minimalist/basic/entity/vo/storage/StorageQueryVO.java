package com.minimalist.basic.entity.vo.storage;

import com.minimalist.basic.entity.enums.StorageEnum;
import com.minimalist.common.mybatis.bo.Pager;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "存储信息查询实体")
public class StorageQueryVO extends Pager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "storageName", description = "存储名称", type = "string")
    private String storageName;

    @Schema(name = "storageCode", description = "存储编码", type = "string")
    private String storageCode;

    @Schema(name = "storageType", description = "存储类型", type = "string")
    private String storageType;

    @SchemaEnum(implementation = StorageEnum.Status.class)
    @Schema(name = "status", description = "存储状态", type = "integer")
    private Integer status;

}
