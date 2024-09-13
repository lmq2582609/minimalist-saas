package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.common.mybatis.bo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 租户套餐表
 * </p>
 *
 * @author baomidou
 * @since 2023-07-04
 */
@Getter
@Setter
@TableName("m_tenant_package")
public class MTenantPackage extends BaseEntity {

    /**
     * 套餐ID
     */
    @TableField("package_id")
    private Long packageId;

    /**
     * 套餐名称
     */
    @TableField("package_name")
    private String packageName;

    /**
     * 存储套餐所关联的权限集合，使用英文逗号分割，此字段只用于套餐管理，获取租户套餐从套餐与权限关联表中获取
     */
    @TableField("perm_ids")
    private String permIds;

    /**
     * 状态 0禁用 1正常
     */
    @TableField("status")
    private Byte status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /** 该数据是否可以被删除 0否 1是 */
    @TableField(value = "allow_delete")
    private Boolean allowDelete;
}
