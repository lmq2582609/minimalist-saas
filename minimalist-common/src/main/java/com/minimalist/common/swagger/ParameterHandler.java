package com.minimalist.common.swagger;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.core.MethodParameter;

/**
 * 对单字段的枚举进行处理
 * 单字段对应的swagger注解：@Parameter
 */
@Slf4j
public class ParameterHandler extends SchemaEnumHandler implements ParameterCustomizer {

    @Override
    public Parameter customize(Parameter property, MethodParameter parameter) {
        //检查是否包含自定义注解
        SchemaEnum schemaEnumAnnotation = getSchemaEnumAnnotation(parameter.getParameterAnnotations());
        if (ObjectUtil.isNull(schemaEnumAnnotation)) {
            return property;
        }
        String columnDescription = getColumnDescription(schemaEnumAnnotation, property.getDescription());
        property.setDescription(columnDescription);
        return property;
    }

}
