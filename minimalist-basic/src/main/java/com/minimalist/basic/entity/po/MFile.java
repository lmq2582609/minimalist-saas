package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.basic.config.mybatis.bo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author baomidou
 * @since 2023-07-18
 */
@Getter
@Setter
@TableName("m_file")
public class MFile extends BaseEntity {

    /**
     * 文件ID
     */
    @TableField("file_id")
    private Long fileId;

    /**
     * 原文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 现文件名
     */
    @TableField("new_file_Name")
    private String newFileName;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件基础路径
     */
    @TableField("file_base_path")
    private String fileBasePath;

    /**
     * 文件相对路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 文件来源
     */
    @TableField("file_source")
    private Integer fileSource;

    /**
     * 存储类型
     */
    @TableField("storage_type")
    private String storageType;

    /**
     * 文件缩略图url
     */
    @TableField("file_th_url")
    private String fileThUrl;

    /**
     * 文件缩略图文件名
     */
    @TableField("file_th_filename")
    private String fileThFilename;

    /**
     * 文件缩略图大小
     */
    @TableField("file_th_size")
    private Long fileThSize;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态  0未使用 1已使用
     */
    @TableField("status")
    private Integer status;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

}
