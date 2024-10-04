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
     * 状态 0禁用 1正常
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

}
