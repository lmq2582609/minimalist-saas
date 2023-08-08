package com.minimalist.basic.entity.vo.file;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.common.convert.FileSizeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "文件上传响应实体")
public class FileUploadRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "fileId", description = "文件ID", type = "string")
    private Long fileId;

    @Schema(name = "fileName", description = "文件名称", type = "string")
    private String fileName;

    @Schema(name = "filePath", description = "文件路径", type = "string")
    private String filePath;

    @Schema(name = "fileUrl", description = "文件URL", type = "string")
    private String fileUrl;

    @Schema(name = "fileType", description = "文件类型", type = "string")
    private String fileType;

    @Schema(name = "fileSuffix", description = "文件后缀", type = "string")
    private String fileSuffix;

    @JsonSerialize(using = FileSizeSerializer.class)
    @Schema(name = "fileSize", description = "文件大小", type = "string")
    private Long fileSize;

    @Schema(name = "fileSource", description = "文件来源", type = "string")
    private Integer fileSource;

}
