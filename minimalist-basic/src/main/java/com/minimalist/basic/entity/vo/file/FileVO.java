package com.minimalist.basic.entity.vo.file;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.common.file.FileEnum;
import com.minimalist.common.convert.FileSizeSerializer;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "文件实体")
public class FileVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "fileId", description = "文件ID", type = "string")
    private Long fileId;

    @Schema(name = "fileName", description = "文件名", type = "string")
    private String fileName;

    @JsonSerialize(using = FileSizeSerializer.class)
    @Schema(name = "fileSize", description = "文件大小", type = "string")
    private Long fileSize;

    @Schema(name = "fileType", description = "文件类型", type = "string")
    private String fileType;

    @Schema(name = "fileUrl", description = "文件URL", type = "string")
    private String fileUrl;

    @SchemaEnum(implementation = FileEnum.FileSource.class)
    @Schema(name = "fileSource", description = "文件来源", type = "int")
    private Integer fileSource;

    @Schema(name = "filePlatform", description = "文件存储平台", type = "string")
    private String filePlatform;

    @Schema(name = "fileThUrl", description = "文件缩略图URL", type = "string")
    private String fileThUrl;

    @SchemaEnum(implementation = FileEnum.FileStatus.class)
    @Schema(name = "status", description = "文件状态", type = "int")
    private Byte status;

    @Schema(name = "remark", description = "备注", type = "string")
    private String remark;

}
