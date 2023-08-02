package com.minimalist.basic.entity.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "日志文件实体")
public class LogFileVO {

    @Schema(name = "name", description = "日志文件名", type = "string")
    private String name;

    @Schema(name = "size", description = "日志文件大小", type = "string")
    private String size;

}
