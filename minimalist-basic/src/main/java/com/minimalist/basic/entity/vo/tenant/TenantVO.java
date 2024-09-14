package com.minimalist.basic.entity.vo.tenant;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.vo.user.UserVO;
import com.minimalist.common.swagger.SchemaEnum;
import com.minimalist.common.valid.Add;
import com.minimalist.common.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(name = "租户实体")
public class TenantVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "租户ID不能为空", groups = {Update.class})
    @Schema(name = "tenantId", description = "租户ID", type = "string")
    private Long tenantId;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "租户联系人不能为空", groups = {Update.class})
    @Schema(name = "userId", description = "用户ID，与租户绑定", type = "string")
    private Long userId;

    @Schema(name = "contactName", description = "联系人昵称，回显联系人使用", type = "string")
    private String contactName;

    @Schema(name = "email", description = "联系人邮箱，回显联系人使用", type = "string")
    private String email;

    @Schema(name = "phone", description = "联系人号码，回显联系人使用", type = "string")
    private String phone;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "租户套餐ID不能为空", groups = {Add.class, Update.class})
    @Schema(name = "packageId", description = "租户套餐ID", type = "string")
    private Long packageId;

    @NotBlank(message = "租户名不能为空", groups = {Add.class, Update.class})
    @Schema(name = "tenantName", description = "租户名", type = "string")
    private String tenantName;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @NotNull(message = "租户过期时间不能为空", groups = {Add.class, Update.class})
    @Schema(name = "expireTime", description = "租户过期时间", type = "string")
    private LocalDateTime expireTime;

    @NotNull(message = "可创建账号数量不能为空", groups = {Add.class, Update.class})
    @Schema(name = "accountCount", description = "可创建账号数量，表示这个租户下可以创建多少账号", type = "integer")
    private Integer accountCount;

    @NotNull(message = "租户状态不能为空", groups = {Update.class})
    @SchemaEnum(implementation = TenantEnum.TenantStatus.class)
    @Schema(name = "status", description = "租户状态", type = "integer")
    private Byte status;

    @Schema(name = "remark", description = "备注", type = "string")
    private String remark;

    @Schema(name = "user", description = "租户的用户信息，新增时填充", type = "string")
    private UserVO user;

    @Schema(name = "allowDelete", description = "该数据是否可以被删除", type = "boolean")
    private Boolean allowDelete;

}
