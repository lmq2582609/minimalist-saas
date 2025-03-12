package com.minimalist.basic.entity.vo.file;

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

    @Schema(name = "fileSource", description = "文件来源，字典：file-source-path", type = "int")
    private Integer fileSource = -1;

    @Schema(name = "storageId", description = "存储ID，可为空。为空则取默认使用的存储", type = "int")
    private Long storageId;

}
