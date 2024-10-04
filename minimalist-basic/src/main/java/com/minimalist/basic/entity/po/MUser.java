package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.common.mybatis.bo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
@Getter
@Setter
@TableName("m_user")
public class MUser extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户账号
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 盐值
     */
    @TableField("salt")
    private String salt;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 用户真实姓名
     */
    @TableField("user_real_name")
    private String userRealName;

    /**
     * 用户邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号码
     */
    @TableField("phone")
    private String phone;

    /**
     * 用户性别  0未知 1男 2女
     */
    @TableField("user_sex")
    private Integer userSex;

    /**
     * 用户头像base64编码
     */
    @TableField("user_avatar")
    private String userAvatar;

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
