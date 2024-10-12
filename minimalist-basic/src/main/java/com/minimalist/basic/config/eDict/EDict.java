package com.minimalist.basic.config.eDict;

import java.lang.annotation.*;

/**
 * 额外字典数据处理
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EDict {

    /**
     * 字典类型
     */
    String dictType() default "";

}
