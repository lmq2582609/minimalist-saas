package com.minimalist.common.mybatis.mapper;

import com.minimalist.common.mybatis.bo.DeleteParams;
import com.minimalist.common.mybatis.bo.InsertBatchParams;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface EntityMapper {

    /**
     * 通用批量插入
     * @param insertBatchParams 实体
     * @return 受影响行数
     */
    int insertBatch(InsertBatchParams insertBatchParams);

    /**
     * 通用删除
     * @param deleteParams 实体
     * @return 受影响行数
     */
    int delete(DeleteParams deleteParams);





    /**
     * 通用批量修改 - 根据id修改
     * @param paramMap
     * @return
     */
    Integer updateBatch(List<Map<String,Object>> paramMap);

    /**
     * 通用批量删除
     * @param tableName
     * @param key
     * @param values
     * @return
     */
    Integer deleteBatch(@Param("table_name") String tableName, @Param("key") String key, @Param("values") List<Long> values);

}
