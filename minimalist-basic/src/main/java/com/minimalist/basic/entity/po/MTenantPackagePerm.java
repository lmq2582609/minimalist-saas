package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 租户套餐与权限关联表
 * </p>
 *
 * @author baomidou
 * @since 2023-07-04
 */
@Getter
@Setter
@TableName("m_tenant_package_perm")
public class MTenantPackagePerm {

    /**
     * 租户套餐ID
     */
    @TableField("package_id")
    private Long packageId;

    /**
     * 权限ID
     */
    @TableField("perm_id")
    private Long permId;

}
