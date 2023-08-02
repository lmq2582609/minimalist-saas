package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户与角色关联表 1用户-N角色
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
@Getter
@Setter
@TableName("m_user_role")
public class MUserRole {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private Long roleId;
}
