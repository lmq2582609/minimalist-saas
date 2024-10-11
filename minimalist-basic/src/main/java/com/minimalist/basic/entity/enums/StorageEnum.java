package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class StorageEnum {

    /** 存储处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_STORAGE("该存储信息不存在"),
        ;
        private final String desc;
    }

}
