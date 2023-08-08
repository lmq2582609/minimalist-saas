package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.common.mybatis.bo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
@Getter
@Setter
@TableName("m_perms")
public class MPerms extends BaseEntity {

    /**
     * 权限ID
     */
    @TableField("perm_id")
    private Long permId;

    /**
     * 权限标识
     */
    @TableField(value = "perm_code", updateStrategy = FieldStrategy.IGNORED)
    private String permCode;

    /**
     * 权限名称
     */
    @TableField("perm_name")
    private String permName;

    /**
     * 父权限ID
     */
    @TableField("parent_perm_id")
    private Long parentPermId;

    /**
     * 显示顺序
     */
    @TableField("perm_sort")
    private Integer permSort;

    /**
     * 路由地址
     */
    @TableField(value = "perm_path", updateStrategy = FieldStrategy.IGNORED)
    private String permPath;

    /**
     * 权限图标 菜单可传图标
     */
    @TableField(value = "perm_icon", updateStrategy = FieldStrategy.IGNORED)
    private String permIcon;

    /**
     * 权限类型  M菜单 B按钮
     */
    @TableField("perm_type")
    private String permType;

    /**
     * 组件路径
     */
    @TableField(value = "component", updateStrategy = FieldStrategy.IGNORED)
    private String component;

    /**
     * 是否可见 0隐藏 1显示）
     */
    @TableField("visible")
    private Boolean visible;

    /**
     * 备注
     */
    @TableField(value = "remark", updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    /**
     * 状态  0禁用 1正常
     */
    @TableField("status")
    private Integer status;

}
