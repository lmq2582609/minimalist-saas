package com.minimalist.basic.config.swagger;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.PropertyCustomizer;

/**
 * 对实体类中的枚举进行处理
 * 实体类中对应的swagger注解：@Schema
 */
@Slf4j
public class PropertyHandler extends SchemaEnumHandler implements PropertyCustomizer {

    @Override
    public Schema customize(Schema property, AnnotatedType type) {
        //检查是否包含自定义注解
        SchemaEnum schemaEnumAnnotation = getSchemaEnumAnnotation(type.getCtxAnnotations());
        if (ObjectUtil.isNull(schemaEnumAnnotation)) {
            return property;
        }
        String columnDescription = getColumnDescription(schemaEnumAnnotation, property.getDescription());
        property.setDescription(columnDescription);
        return property;
    }

}
