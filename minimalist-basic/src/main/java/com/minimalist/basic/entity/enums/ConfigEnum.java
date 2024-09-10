package com.minimalist.basic.entity.enums;

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

    /** 字典状态 */
    @Getter
    @AllArgsConstructor
    public enum Status {
        STATUS_0(0, "禁用"),
        STATUS_1(1, "正常"),
        ;
        private final Integer code;
        private final String desc;
    }

}
