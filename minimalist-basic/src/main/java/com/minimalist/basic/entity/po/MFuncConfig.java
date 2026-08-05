package com.minimalist.basic.entity.po;

import com.minimalist.basic.config.mybatis.InsertFullColumnHandler;
import com.minimalist.basic.config.mybatis.UpdateFullColumnHandler;
import com.minimalist.basic.config.mybatis.bo.BaseEntity;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serial;

/**
 * 功能配置表 实体类。
 *
 * @author 小太阳
 * @since 2026-08-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(value = "m_func_config", onInsert = InsertFullColumnHandler.class, onUpdate = UpdateFullColumnHandler.class)
public class MFuncConfig extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long configId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置键名
     */
    private String configKey;

    /**
     * 配置键值
     */
    private String configValue;

    /**
     * 说明
     */
    private String description;

}
