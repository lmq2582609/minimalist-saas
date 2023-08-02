package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户与岗位关联表 1用户-N岗位
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
@Getter
@Setter
@TableName("m_user_post")
public class MUserPost {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 岗位ID
     */
    @TableField("post_id")
    private Long postId;

}
