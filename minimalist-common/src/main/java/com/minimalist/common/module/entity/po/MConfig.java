package com.minimalist.common.module.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.common.mybatis.bo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 参数配置表
 * </p>
 *
 * @author baomidou
 * @since 2024-09-10
 */
@Getter
@Setter
@TableName("m_config")
public class MConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 参数ID
     */
    @TableField("config_id")
    private Long configId;

    /**
     * 参数名称
     */
    @TableField("config_name")
    private String configName;

    /**
     * 参数键名
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 参数键值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 状态  0禁用 1正常
     */
    @TableField("status")
    private Byte status;
}
