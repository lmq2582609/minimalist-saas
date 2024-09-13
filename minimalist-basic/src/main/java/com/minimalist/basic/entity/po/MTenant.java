package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.minimalist.common.mybatis.bo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author baomidou
 * @since 2023-07-04
 */
@Getter
@Setter
@TableName("m_tenant")
public class MTenant extends BaseEntity {

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 租户套餐ID
     */
    @TableField("package_id")
    private Long packageId;

    /**
     * 租户名
     */
    @TableField("tenant_name")
    private String tenantName;

    /**
     * 租户过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 可创建账号数量
     */
    @TableField("account_count")
    private Integer accountCount;

    /**
     * 状态
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

}
