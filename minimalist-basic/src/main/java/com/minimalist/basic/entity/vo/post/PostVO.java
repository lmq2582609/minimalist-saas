package com.minimalist.basic.entity.vo.post;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.swagger.SchemaEnum;
import com.minimalist.basic.utils.Add;
import com.minimalist.basic.utils.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "岗位实体")
public class PostVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "岗位ID不能为空", groups = {Update.class})
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "postId", description = "岗位ID", type = "string")
    private Long postId;

    @NotBlank(message = "岗位编码不能为空", groups = {Add.class, Update.class})
    @Schema(name = "postCode", description = "岗位编码", type = "string")
    private String postCode;

    @NotBlank(message = "岗位名称不能为空", groups = {Add.class, Update.class})
    @Schema(name = "postName", description = "岗位名称", type = "string")
    private String postName;

    @NotNull(message = "排序值不能为空", groups = {Add.class, Update.class})
    @Schema(name = "postSort", description = "排序", type = "integer")
    private Integer postSort;

    @Schema(name = "remark", description = "备注", type = "string")
    private String remark;

    @NotNull(message = "岗位状态不能为空", groups = {Update.class})
    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "岗位状态", type = "integer")
    private Integer status;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "tenantId", description = "租户ID", type = "string")
    private Long tenantId;

}
