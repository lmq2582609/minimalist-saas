package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色与权限关联表 1角色-N权限
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
@Getter
@Setter
@TableName("m_role_perm")
public class MRolePerm {

    /**
     * 角色ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 权限ID
     */
    @TableField("perm_id")
    private Long permId;
}
