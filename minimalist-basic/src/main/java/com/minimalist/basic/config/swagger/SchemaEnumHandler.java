package com.minimalist.basic.config.swagger;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.StringJoiner;

@Slf4j
public class SchemaEnumHandler {

    /**
     * 查找注解
     * @param annotations 注解数组
     * @return SchemaEnum注解
     */
    protected SchemaEnum getSchemaEnumAnnotation(Annotation[] annotations) {
        if (ArrayUtil.isEmpty(annotations)) {
            return null;
        }
        for (Annotation annotation : annotations) {
            SchemaEnum anno = AnnotationUtils.getAnnotation(annotation, SchemaEnum.class);
            if (ObjectUtil.isNotNull(anno)) {
                return anno;
            }
        }
        return null;
    }

    /**
     * 获取枚举里的方法，并执行取值
     * @param enumClass 枚举
     * @param method 方法名
     * @return 值
     */
    protected Object getEnumMethodValue(Enum enumClass, String method) {
        try {
            Method m = enumClass.getClass().getMethod(method);
            return m.invoke(enumClass);
        } catch (Exception e) {
            log.warn("swagger自定义处理，获取枚举中 {} 方法异常，", method, e);
        }
        return null;
    }

    /**
     * 根据注解获取描述
     * @param schemaEnumAnnotation swagger自定义枚举处理注解
     * @param sourceDescription 字段swagger原始描述
     * @return 文字描述
     */
    protected String getColumnDescription(SchemaEnum schemaEnumAnnotation, String sourceDescription) {
        if (StrUtil.isNotBlank(sourceDescription)) { sourceDescription = ""; }
        //枚举
        Class<? extends Enum> enumClass = schemaEnumAnnotation.implementation();
        //将枚举转为文字描述
        StringJoiner sj = new StringJoiner(", ");
        for (Enum enumConstant : enumClass.getEnumConstants()) {
            Object key = getEnumMethodValue(enumConstant, schemaEnumAnnotation.keyMethodName());
            Object value = getEnumMethodValue(enumConstant, schemaEnumAnnotation.valueMethodName());
            sj.add(key + ": " + value);
        }
        return sourceDescription + " -> " + sj;
    }

}
