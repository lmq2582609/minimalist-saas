package com.minimalist.common.module.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ConfigEnum {

    /** 字典处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_CONFIG("参数配置不存在"),
        CONTAIN_CONFIG_KEY("参数键名已存在"),
        CANNOT_DEL_SYSTEM_CONFIG("不能删除系统参数"),
        ;
        private final String desc;
    }

}
