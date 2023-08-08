package com.minimalist.common.utils;

import cn.hutool.core.util.IdUtil;

public class UnqIdUtil {

    /**
     * 获取唯一ID
     * @return ID
     */
    public static long uniqueId() {
        return IdUtil.getSnowflakeNextId();
    }

}
