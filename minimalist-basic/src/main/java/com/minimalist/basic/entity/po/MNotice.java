package com.minimalist.basic.entity.po;

import com.minimalist.basic.config.mybatis.InsertFullColumnHandler;
import com.minimalist.basic.config.mybatis.UpdateFullColumnHandler;
import com.minimalist.basic.config.mybatis.bo.BaseEntity;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serial;

/**
 * 通知公告表 实体类。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(value = "m_notice", onInsert = InsertFullColumnHandler.class, onUpdate = UpdateFullColumnHandler.class)
public class MNotice extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    private Long noticeId;

    /**
     * 公告标题
     */
    private String noticeTitle;

    /**
     * 公告类型（1公告）
     */
    private Integer noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 封面图文件ID，多个使用 , 分割
     */
    private String noticePicFileId;

    /**
     * 是否置顶 0否 1是
     */
    private Boolean noticeTop;

    /**
     * 延时发布的时间
     */
    private LocalDateTime noticeTimeInterval;

    /**
     * 排序
     */
    private Integer noticeSort;

    /**
     * 是否外链 0否 1是
     */
    private Boolean noticeOutChain;

    /**
     * 外部跳转链接
     */
    private String noticeLink;

    /**
     * 发布部门
     */
    private Long publishDeptId;

    /**
     * 发布人
     */
    private Long publishAuthorId;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 状态  0禁用 1正常
     */
    private Integer status;

}
