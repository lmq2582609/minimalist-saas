package com.minimalist.basic.entity.vo.file;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.mybatis.bo.Pager;
import com.minimalist.basic.config.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "文件查询实体")
public class FileQueryVO extends Pager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "fileName", description = "文件名", type = "string")
    private String fileName;

    @Schema(name = "fileSource", description = "文件来源", type = "string")
    private Integer fileSource;

    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "文件状态", type = "int")
    private Integer status;

}
