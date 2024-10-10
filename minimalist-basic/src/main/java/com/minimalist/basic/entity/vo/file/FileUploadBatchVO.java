package com.minimalist.basic.entity.vo.file;

import com.minimalist.common.file.FileEnum;
import com.minimalist.common.swagger.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "文件上传实体")
public class FileUploadBatchVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "上传的文件不能为空")
    @Schema(name = "files", description = "文件列表(支持多个文件)", type = "array")
    private List<MultipartFile> files;

    @NotNull(message = "文件来源不能为空")
    @SchemaEnum(implementation = FileEnum.FileSource.class)
    @Schema(name = "fileSource", description = "文件来源", type = "int")
    private Integer fileSource;

}
