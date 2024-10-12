package com.minimalist.basic.config.resubmit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReSubmit {

    /**
     * 持有锁的租期时间
     * 第一次请求和第二次请求，时间相隔 leaseTime 毫秒算重复请求，将被拒绝
     */
    long leaseTime() default 3000;

    /**
     * 获取锁时，等待 waitTime 毫秒，超过 waitTimeout 毫秒，就不再尝试获取锁
     * 默认 0 不等待，即获取锁时只要获取不到就返回
     */
    long waitTime() default 0;

}
