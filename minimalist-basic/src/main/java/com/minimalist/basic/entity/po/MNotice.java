package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.basic.entity.mybatis.BaseEntity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 通知公告表
 * </p>
 *
 * @author baomidou
 * @since 2023-07-18
 */
@Getter
@Setter
@TableName("m_notice")
public class MNotice extends BaseEntity {

    /**
     * 公告ID
     */
    @TableField("notice_id")
    private Long noticeId;

    /**
     * 公告标题
     */
    @TableField("notice_title")
    private String noticeTitle;

    /**
     * 公告类型（1公告）
     */
    @TableField("notice_type")
    private Integer noticeType;

    /**
     * 公告内容
     */
    @TableField("notice_content")
    private String noticeContent;

    /**
     * 公告封面图地址
     */
    @TableField("notice_pic")
    private String noticePic;

    /**
     * 是否置顶
     */
    @TableField("notice_top")
    private Boolean noticeTop;

    /**
     * 延期发布时间 更新时，允许由原值更新为空值
     */
    @TableField(value = "notice_time_interval", updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime noticeTimeInterval;

    /**
     * 排序
     */
    @TableField("notice_sort")
    private Integer noticeSort;

    /**
     * 是否外链
     */
    @TableField("notice_out_chain")
    private Boolean noticeOutChain;

    /**
     * 外部链接
     */
    @TableField("notice_link")
    private String noticeLink;

    /**
     * 发布部门ID
     */
    @TableField("publish_dept_id")
    private Long publishDeptId;

    /**
     * 发布人
     */
    @TableField("publish_author_id")
    private Long publishAuthorId;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    /**
     * 状态  0禁用 1正常
     */
    @TableField("status")
    private Byte status;

}
