package com.minimalist.basic.entity.po;

import com.minimalist.basic.config.mybatis.InsertFullColumnHandler;
import com.minimalist.basic.config.mybatis.UpdateFullColumnHandler;
import com.minimalist.basic.config.mybatis.bo.BaseEntity;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serial;

/**
 * 租户表 实体类。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(value = "m_tenant", onInsert = InsertFullColumnHandler.class, onUpdate = UpdateFullColumnHandler.class)
public class MTenant extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 租户套餐ID
     */
    private Long packageId;

    /**
     * 租户名
     */
    private String tenantName;

    /**
     * 租户过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 可创建账号数量
     */
    private Integer accountCount;

    /**
     * 数据隔离方式  column字段隔离(默认)  db数据库隔离
     */
    private String dataIsolation;

    /**
     * 数据源名称  master默认使用主库
     */
    private String datasource;

    /**
     * 状态 0禁用 1正常
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}
