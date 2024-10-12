package com.minimalist.basic.entity.enums;

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

    /** 存储类型 */
    @Getter
    @AllArgsConstructor
    public enum StorageType {
        LOCAL("local", "本地"),
        MINIO("minio", "MinIO"),
        ;
        private final String code;
        private final String desc;
    }

}
