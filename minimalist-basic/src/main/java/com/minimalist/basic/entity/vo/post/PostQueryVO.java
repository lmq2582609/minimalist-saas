package com.minimalist.basic.entity.vo.post;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.mybatis.bo.PageReq;
import com.minimalist.basic.config.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "岗位查询实体")
public class PostQueryVO extends PageReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "postName", description = "岗位名称", type = "string")
    private String postName;

    @Schema(name = "postCode", description = "岗位编码", type = "string")
    private String postCode;

    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "岗位状态", type = "integer")
    private Integer status;

}
