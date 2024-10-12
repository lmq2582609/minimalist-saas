package com.minimalist.basic.config.swagger;

import java.lang.annotation.*;

/**
 * swagger 实体中枚举值处理注解
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SchemaEnum {

    /** 枚举中取key值的方法名 */
    String keyMethodName() default "getCode";

    /** 枚举中取value值的方法名 */
    String valueMethodName() default "getDesc";

    /** 枚举 */
    Class<? extends Enum> implementation();

}
