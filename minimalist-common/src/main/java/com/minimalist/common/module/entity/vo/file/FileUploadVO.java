package com.minimalist.common.module.entity.vo.file;

import com.minimalist.common.module.entity.enums.FileEnum;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "文件上传实体")
public class FileUploadVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "上传的文件不能为空")
    @Schema(name = "file", description = "文件(单个文件)", type = "file")
    private MultipartFile file;

    @NotNull(message = "文件来源不能为空")
    @SchemaEnum(implementation = FileEnum.FileSource.class)
    @Schema(name = "fileSource", description = "文件来源", type = "int")
    private Integer fileSource;

    @Schema(name = "storageId", description = "存储ID，可为空。为空则取默认使用的存储", type = "int")
    private Long storageId;

}
