package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class StorageEnum {

    /** 存储处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        EXISTS_STORAGE_CODE("该存储编码已存在"),
        NONENTITY_STORAGE("该存储信息不存在"),
        ;
        private final String desc;
    }

    /** 存储状态 */
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
