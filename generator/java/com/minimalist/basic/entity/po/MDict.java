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
 * 字典表 实体类。
 *
 * @author asus
 * @since 2024-10-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(value = "m_dict", onInsert = InsertFullColumnHandler.class, onUpdate = UpdateFullColumnHandler.class)
public class MDict extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典ID
     */
    private Long dictId;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典key
     */
    private String dictKey;

    /**
     * 字典value
     */
    private String dictValue;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典描述
     */
    private String dictDesc;

    /**
     * 字典排序值
     */
    private Integer dictOrder;

    /**
     * 字典样式，对应前端Tag组件的type
     */
    private String dictClass;

    /**
     * 状态  0禁用 1正常
     */
    private Integer status;

}
