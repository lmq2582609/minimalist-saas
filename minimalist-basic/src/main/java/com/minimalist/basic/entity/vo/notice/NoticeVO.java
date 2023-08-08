package com.minimalist.basic.entity.vo.notice;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.minimalist.basic.entity.enums.NoticeEnum;
import com.minimalist.basic.entity.vo.file.FileVO;
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
import java.util.List;

@Data
@Schema(name = "公告管理实体")
public class NoticeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "公告ID不能为空", groups = {Update.class})
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "noticeId", description = "公告ID", type = "string")
    private Long noticeId;

    @NotBlank(message = "公告标题不能为空", groups = {Add.class, Update.class})
    @Schema(name = "noticeTitle", description = "公告标题", type = "string")
    private String noticeTitle;

    @NotNull(message = "公告类型不能为空", groups = {Add.class, Update.class})
    @SchemaEnum(implementation = NoticeEnum.NoticeType.class)
    @Schema(name = "noticeType", description = "公告类型", type = "string")
    private Integer noticeType;

    @NotBlank(message = "公告内容不能为空", groups = {Add.class, Update.class})
    @Schema(name = "noticeContent", description = "公告内容", type = "string")
    private String noticeContent;

    @Schema(name = "noticePic", description = "公告封面图地址，使用 | 分割", type = "string")
    private String noticePic;

    @Schema(name = "noticePicFile", description = "公告封面图文件信息", type = "array")
    private List<FileVO> noticePicFile;

    @Schema(name = "noticeTop", description = "是否置顶", type = "string")
    private Boolean noticeTop;

    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @Schema(name = "noticeTimeInterval", description = "延期发布时间", type = "string")
    private LocalDateTime noticeTimeInterval;

    @Schema(name = "noticeSort", description = "排序值", type = "string")
    private Integer noticeSort;

    @Schema(name = "noticeOutChain", description = "是否外链", type = "string")
    private Boolean noticeOutChain;

    @Schema(name = "noticeLink", description = "外部链接", type = "string")
    private String noticeLink;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "publishDeptId", description = "发布部门ID", type = "string")
    private Long publishDeptId;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "发布人不能为空", groups = {Add.class, Update.class})
    @Schema(name = "publishAuthorId", description = "发布人ID", type = "string")
    private Long publishAuthorId;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @Schema(name = "publishTime", description = "发布时间", type = "string")
    private LocalDateTime publishTime;

    @NotNull(message = "公告状态不能为空", groups = {Add.class, Update.class})
    @SchemaEnum(implementation = NoticeEnum.NoticeStatus.class)
    @Schema(name = "status", description = "公告状态", type = "string")
    private Byte status;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "createId", description = "创建人ID", type = "string")
    private Long createId;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @Schema(name = "createTime", description = "创建时间", type = "string")
    private LocalDateTime createTime;

}
