package com.minimalist.common.module.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class StorageEnum {

    /** 存储处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_STORAGE("存储信息不存在"),
        ;
        private final String desc;
    }

}
