package com.minimalist.basic.config.mybatis.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldParams {

    /** 数据库字段名 */
    private String columnName;

    /** 实体类字段名 */
    private String FieldName;

}
