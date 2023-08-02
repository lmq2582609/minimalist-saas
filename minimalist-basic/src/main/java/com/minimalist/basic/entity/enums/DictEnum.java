package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class DictEnum {

    /** 字典处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_DICT("字典不存在"),
        ;
        private final String desc;
    }

    /** 字典状态 */
    @Getter
    @AllArgsConstructor
    public enum UserStatus {
        USER_STATUS_0(0, "禁用"),
        USER_STATUS_1(1, "正常"),
        ;
        private final Integer code;
        private final String desc;
    }

}
