package com.minimalist.basic.config.extraDict;

import java.lang.annotation.*;

/**
 * 额外字典数据处理
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtraDict {

    /**
     * 字典类型
     */
    String dictType() default "";

}
