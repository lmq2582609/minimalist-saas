package com.minimalist.basic.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minimalist.common.mybatis.bo.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author baomidou
 * @since 2023-05-27
 */
@Getter
@Setter
@TableName("m_dict")
public class MDict extends BaseEntity {

    /**
     * 字典ID
     */
    @TableField("dict_id")
    private Long dictId;

    /**
     * 字典类型
     */
    @TableField("dict_type")
    private String dictType;

    /**
     * 字典key
     */
    @TableField("dict_key")
    private String dictKey;

    /**
     * 字典value
     */
    @TableField("dict_value")
    private String dictValue;

    /**
     * 字典名称
     */
    @TableField("dict_name")
    private String dictName;

    /**
     * 字典描述
     */
    @TableField("dict_desc")
    private String dictDesc;

    /**
     * 字典排序值
     */
    @TableField("dict_order")
    private Integer dictOrder;

    /**
     * 字典样式，对应前端Tag组件的type
     */
    @TableField("dict_class")
    private String dictClass;

    /**
     * 状态  0禁用 1正常
     */
    @TableField("status")
    private Integer status;

}
