package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.basic.entity.mybatis.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 岗位表
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
@Getter
@Setter
@TableName("m_post")
public class MPost extends BaseEntity {

    /**
     * 岗位ID
     */
    @TableField("post_id")
    private Long postId;

    /**
     * 岗位编码
     */
    @TableField("post_code")
    private String postCode;

    /**
     * 岗位名称
     */
    @TableField("post_name")
    private String postName;

    /**
     * 显示顺序
     */
    @TableField("post_sort")
    private Integer postSort;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态  0禁用 1正常
     */
    @TableField("status")
    private Integer status;

    /**
     * 租户编号
     */
    @TableField("tenant_id")
    private Long tenantId;
}
