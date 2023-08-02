package com.minimalist.basic.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.lang.reflect.Method;

/**
 * 存放class和method
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanMethod<T> {

    /**
     * class
     */
    private T bean;

    /**
     * method
     */
    private Method method;

}
