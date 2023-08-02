package com.minimalist.basic.entity.constant;

public class RedisKeyConstant {

    /**
     * 用户登录缓存
     */
    public static final String USERLOGIN_CACHE_KEY = "user_login:{0}";

    /**
     * 图形验证码缓存
     */
    public static final String CAPTCHA_CACHE_KEY = "captcha:{0}";

    /**
     * 图形验证码，有效期 1分钟
     */
    public static final int CAPTCHA_CACHE_EX = 60;

    /**
     * 用户登录缓存，有效期 1 天
     */
    public static final int USERLOGIN_CACHE_EX = 24 * 60 * 60;

    /**
     * 字典缓存前缀 dict:dictType:dictData
     */
    public static final String DICT_CACHE_KEY = "dict:{0}";

    /**
     * 字典缓存，有效期 7 天
     */
    public static final int DICT_CACHE_EX = 7 * 24 * 60 * 60;

    /**
     * 防重复提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:{0}";

}
