package com.minimalist.basic.entity.mybatis;

import lombok.Data;
import java.util.List;

@Data
public class InsertBatchParams {

    /** 表名 */
    private String table_name;

    /** 列名 */
    private List<String> columnNames;

    /** 值  */
    private List<List<Object>> columnValues;

    /** 列名映射 */
    private List<FieldParams> fieldParams;

}
