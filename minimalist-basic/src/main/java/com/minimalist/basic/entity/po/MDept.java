package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.common.mybatis.bo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
@Getter
@Setter
@TableName("m_dept")
public class MDept extends BaseEntity {

    /**
     * 部门id
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 父部门id
     */
    @TableField("parent_dept_id")
    private Long parentDeptId;

    /**
     * 祖级列表
     */
    @TableField("ancestors")
    private String ancestors;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 部门负责人
     */
    @TableField("dept_leader")
    private String deptLeader;

    /**
     * 显示顺序
     */
    @TableField("dept_sort")
    private Integer deptSort;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

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
