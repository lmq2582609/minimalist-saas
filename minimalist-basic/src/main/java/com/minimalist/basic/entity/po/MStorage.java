package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.basic.config.mybatis.bo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 存储管理表
 * </p>
 *
 * @author baomidou
 * @since 2024-10-08
 */
@Getter
@Setter
@TableName("m_storage")
public class MStorage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("storage_id")
    private Long storageId;

    /**
     * 名称
     */
    @TableField("storage_name")
    private String storageName;

    /**
     * 存储类型，用于标识存储平台，如本地、阿里云oss、七牛云oss等
     */
    @TableField("storage_type")
    private String storageType;

    /**
     * 是否默认使用这个存储，0否 1是
     */
    @TableField("storage_default")
    private Boolean storageDefault;

    /**
     * 说明
     */
    @TableField("description")
    private String description;

    /**
     * 存储配置，JSON数据
     */
    @TableField("storage_config")
    private String storageConfig;

    @TableField("status")
    private Integer status;
}
