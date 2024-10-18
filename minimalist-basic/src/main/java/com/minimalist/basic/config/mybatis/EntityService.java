package com.minimalist.basic.config.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalist.basic.config.mybatis.bo.DeleteParams;
import com.minimalist.basic.config.mybatis.bo.FieldParams;
import com.minimalist.basic.config.mybatis.bo.InsertBatchParams;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.entity.enums.RespEnum;
import com.minimalist.basic.mapper.EntityMapper;
import com.mybatisflex.annotation.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import java.beans.PropertyDescriptor;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EntityService {

    @Autowired
    private EntityMapper entityMapper;

    private static final Map<String, InsertBatchParams> paramsMap = new ConcurrentHashMap<>();

    /**
     * 通用删除数据 -> 真实delete
     * 因配置了mybatisPlus的逻辑删除，所以提供通用delete
     * @return 受影响行数
     */
    public <T, R> int delete(Func1<T, R> sqlColumnKey, Object value) {
        DeleteParams deleteParams = getColumnToParams(sqlColumnKey, value);
        return entityMapper.delete(deleteParams);
    }

    /**
     * 批量插入
     * @param insertList 待插入数据
     * @return 受影响行数
     */
    public <T> int insertBatch(List<T> insertList) {
        if (CollectionUtil.isEmpty(insertList)) {return 0;}
        return entityMapper.insertBatch(getInsertParams(insertList));
    }

    /**
     * 获取批量插入数据
     * @param insertList 待插入数据
     * @return 构造的数据，用于渲染mapper.xml
     */
    private <T> InsertBatchParams getInsertParams(List<T> insertList) {
        Class<?> aClass = insertList.get(0).getClass();
        String clazzName = aClass.getName();
        InsertBatchParams paramsCache = paramsMap.get(clazzName);
        if (ObjectUtil.isNotNull(paramsCache)) {
            InsertBatchParams insertBatchParams = new InsertBatchParams();
            insertBatchParams.setTable_name(paramsCache.getTable_name());
            insertBatchParams.setColumnNames(paramsCache.getColumnNames());
            //获取列值
            List<List<Object>> columnValues = getColumnValues(insertList, paramsCache.getFieldParams());
            insertBatchParams.setColumnValues(columnValues);
            return insertBatchParams;
        }
        //获取表名
        Table tableName = aClass.getAnnotation(Table.class);
        if (ObjectUtil.isNull(tableName) || StrUtil.isBlank(tableName.value())) {
            log.warn("获取注解失败，未获取到TableName注解值");
            throw new BusinessException(RespEnum.FAILED.getDesc());
        }
        InsertBatchParams insertBatchParams = new InsertBatchParams();
        insertBatchParams.setTable_name(tableName.value());
        //获取列名
        List<FieldParams> fieldParams = getColumnNames(aClass);
        //列名映射
        insertBatchParams.setFieldParams(fieldParams);
        //列名
        List<String> columnNames = fieldParams.stream().map(FieldParams::getColumnName).collect(Collectors.toList());
        insertBatchParams.setColumnNames(columnNames);
        //获取列值
        List<List<Object>> columnValues = getColumnValues(insertList, fieldParams);
        insertBatchParams.setColumnValues(columnValues);
        //存入缓存
        paramsCache = new InsertBatchParams();
        paramsCache.setTable_name(tableName.value());
        paramsCache.setFieldParams(fieldParams);
        paramsCache.setColumnNames(columnNames);
        paramsMap.put(clazzName, paramsCache);
        return insertBatchParams;
    }


    /**
     * 获取列名
     * @param function 字段
     * @return 字段信息
     */
    private static <T, R> DeleteParams getColumnToParams(Func1<T, R> function, Object value) {
        try {
            Method method = function.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            Object obj = method.invoke(function);
            if (!(obj instanceof SerializedLambda serializedLambda)) {
                log.warn("获取字段异常，类型不是SerializedLambda!");
                throw new BusinessException(RespEnum.FAILED.getDesc());
            }
            String fieldName = serializedLambda.getImplMethodName();
            if (fieldName.startsWith("is")) {
                fieldName = fieldName.substring(2);
            } else if (fieldName.startsWith("get") || fieldName.startsWith("set")) {
                fieldName = fieldName.substring(3);
            } else {
                log.warn("获取字段异常，字段名【" + fieldName + "】，不是方法引用");
                throw new BusinessException(RespEnum.FAILED.getDesc());
            }
            if (fieldName.length() == 1 || (fieldName.length() > 1 && !Character.isUpperCase(fieldName.charAt(1)))) {
                fieldName = fieldName.substring(0, 1).toLowerCase(Locale.ENGLISH) + fieldName.substring(1);
            }
            String declaredClass = serializedLambda.getImplClass().replace("/", ".");
            Class<?> aClass = Class.forName(declaredClass, false, ClassUtils.getDefaultClassLoader());
            Table tableName = aClass.getAnnotation(Table.class);
            if (ObjectUtil.isNull(tableName) || StrUtil.isBlank(tableName.value())) {
                log.warn("获取注解失败，未获取到TableName注解值");
                throw new BusinessException(RespEnum.FAILED.getDesc());
            }
            Field field = ReflectionUtils.findField(aClass, fieldName);
            if (ObjectUtil.isNull(field)) {
                log.warn("获取字段异常，字段名【" + fieldName + "】");
                throw new BusinessException(RespEnum.FAILED.getDesc());
            }
//            TableField tableField = field.getAnnotation(TableField.class);
//            if (ObjectUtil.isNull(tableField) || StrUtil.isBlank(tableField.value())) {
//                log.warn("获取字段异常，字段名【" + fieldName + "】，获取注解失败，未获取到TableField注解值");
//                throw new BusinessException(RespEnum.FAILED.getDesc());
//            }
            DeleteParams deleteParams = new DeleteParams();
            deleteParams.setTable_name(tableName.value());
//            deleteParams.setKey(tableField.value());
            deleteParams.setValue(value);
            return deleteParams;
        } catch (Exception e) {
            log.warn("获取字段异常：", e);
            throw new BusinessException(RespEnum.FAILED.getDesc());
        }
    }

    /**
     * 获取列名
     * @param aClass 实体
     * @return 字段列表
     */
    private static List<FieldParams> getColumnNames(Class<?> aClass) {
        List<FieldParams> columns = CollectionUtil.list(false);
        //获取字段，包含父类字段
        Field[] fields = ReflectUtil.getFields(aClass);
        for (int i = 0; i < fields.length; i++) {
            //需要忽略的字段 -> id，deleted，Version
            if (fields[i].getName().equals("id") || fields[i].getName().equals("deleted") || fields[i].getName().equals("version")) {
                continue;
            }
//            TableField tableField = fields[i].getAnnotation(TableField.class);
//            if (ObjectUtil.isNotNull(tableField) && StrUtil.isNotBlank(tableField.value())) {
//                columns.add(new FieldParams(tableField.value(), fields[i].getName()));
//            }
        }
        if (CollectionUtil.isEmpty(columns)) {
            log.warn("获取字段异常，未获取到字段，请检查实体类");
            throw new BusinessException(RespEnum.FAILED.getDesc());
        }
        return columns;
    }

    /**
     * 获取列值
     * @param insertList 待插入的数据
     * @param fieldParams 列名集合
     * @return 列值
     */
    private static <T> List<List<Object>> getColumnValues(List<T> insertList, List<FieldParams> fieldParams) {
        List<List<Object>> columnValues = CollectionUtil.list(false);
        for (int i = 0; i < insertList.size(); i++) {
            T entity = insertList.get(i);
            List<Object> values = CollectionUtil.list(false);
            for (int j = 0; j < fieldParams.size(); j++) {
                Object columnValue = getColumnValue(fieldParams.get(j).getFieldName(), entity.getClass(), entity);
                values.add(columnValue);
            }
            columnValues.add(values);
        }
        return columnValues;
    }

    /**
     * 调用get方法获取实体类的值
     * @param fieldName 字段名称
     * @param clazz 实体类class
     * @param entity 实体类
     * @return 字段值
     */
    private static <T> Object getColumnValue(String fieldName, Class<?> clazz, T entity) {
        try {
            return new PropertyDescriptor(fieldName, clazz).getReadMethod().invoke(entity, null);
        } catch (Exception e) {
            log.warn("获取字段值异常，", e);
            throw new BusinessException(RespEnum.FAILED.getDesc());
        }
    }

}
