package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户与岗位关联表
 * </p>
 *
 * @author baomidou
 * @since 2023-07-04
 */
@Getter
@Setter
@TableName("m_user_dept")
public class MUserDept {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private Long deptId;

}
