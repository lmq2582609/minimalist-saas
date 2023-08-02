package com.minimalist.basic.entity.mybatis;

import lombok.Data;

@Data
public class DeleteParams {

    /** 表名 */
    private String table_name;

    /** 列名 */
    private String key;

    /** 值  */
    private Object value;

}
