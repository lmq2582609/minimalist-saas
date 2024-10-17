package com.minimalist.basic.utils;

public class RedisKeyConstant {

    /**
     * 图形验证码缓存
     */
    public static final String CAPTCHA_CACHE_KEY = "captcha:{0}";

    /**
     * 图形验证码，有效期 1分钟
     */
    public static final int CAPTCHA_CACHE_EX = 60;

    /**
     * 字典缓存前缀 dict:dictType:dictData
     */
    public static final String DICT_CACHE_KEY = "dict:{0}";

    /**
     * 字典缓存，有效期 30 天
     */
    public static final int DICT_CACHE_EX = 30 * 24 * 60 * 60;

    /**
     * 防重复提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:{0}";

    /**
     * 参数管理 redis key
     */
    public static final String SYSTEM_CONFIG_KEY = "system_config:{0}";

    /**
     * 参数缓存，有效期 30 天
     */
    public static final int SYSTEM_CONFIG_CACHE_EX = 30 * 24 * 60 * 60;

    /**
     * 用户角色 redis key
     */
    public static final String USER_ROLE_CACHE_KEY = "user_role:{0}";

    /**
     * 用户权限 perm key
     */
    public static final String USER_PERM_CACHE_KEY = "user_perm:{0}";

    /**
     * 用户权限超时时间
     */
    public static final int USER_PERM_CACHE_EX = 7 * 24 * 60 * 60;

}
