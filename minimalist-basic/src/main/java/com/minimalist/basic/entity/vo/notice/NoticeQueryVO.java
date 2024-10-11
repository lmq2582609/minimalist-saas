package com.minimalist.basic.entity.vo.notice;

import com.minimalist.basic.entity.enums.NoticeEnum;
import com.minimalist.common.enums.StatusEnum;
import com.minimalist.common.mybatis.bo.Pager;
import com.minimalist.common.swagger.SchemaEnum;
import com.minimalist.common.valid.Add;
import com.minimalist.common.valid.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "公告查询实体")
public class NoticeQueryVO extends Pager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "noticeTitle", description = "公告标题", type = "string")
    private String noticeTitle;

    @SchemaEnum(implementation = NoticeEnum.NoticeType.class)
    @Schema(name = "noticeType", description = "公告类型", type = "string")
    private Integer noticeType;

    @NotNull(message = "公告状态不能为空", groups = {Add.class, Update.class})
    @SchemaEnum(implementation = StatusEnum.class)
    @Schema(name = "status", description = "公告状态", type = "string")
    private Byte status;

}
