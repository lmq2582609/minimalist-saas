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
 * 文件表 实体类。
 *
 * @author asus
 * @since 2024-10-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(value = "m_file", onInsert = InsertFullColumnHandler.class, onUpdate = UpdateFullColumnHandler.class)
public class MFile extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 原文件名
     */
    private String fileName;

    /**
     * 现文件名
     */
    private String newFileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件基础路径
     */
    private String fileBasePath;

    /**
     * 文件相对路径
     */
    private String filePath;

    /**
     * 文件url
     */
    private String fileUrl;

    /**
     * 文件来源
     */
    private Integer fileSource;

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 文件缩略图url
     */
    private String fileThUrl;

    /**
     * 文件缩略图文件名
     */
    private String fileThFilename;

    /**
     * 缩略图文件大小
     */
    private Long fileThSize;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 状态  0未使用 1已使用，默认未使用，代码中控制修改为已使用，可以定期清理未使用的文件
     */
    private Integer status;

}
