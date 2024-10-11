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

}
