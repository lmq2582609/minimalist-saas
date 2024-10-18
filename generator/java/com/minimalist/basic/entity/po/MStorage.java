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
 * 存储管理表 实体类。
 *
 * @author asus
 * @since 2024-10-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(value = "m_storage", onInsert = InsertFullColumnHandler.class, onUpdate = UpdateFullColumnHandler.class)
public class MStorage extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long storageId;

    /**
     * 名称
     */
    private String storageName;

    /**
     * 存储类型，用于标识存储平台，如本地、阿里云oss、七牛云oss等
     */
    private String storageType;

    /**
     * 是否默认使用这个存储，0否 1是
     */
    private Boolean storageDefault;

    /**
     * 说明
     */
    private String description;

    /**
     * 存储配置，JSON数据
     */
    private String storageConfig;

    private Integer status;

}
