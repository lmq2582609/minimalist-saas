package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PostEnum {

    /** 岗位处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_POST("岗位不存在"),
        EXISTS_POST("岗位已存在，请检查岗位编码"),
        ;
        private final String desc;
    }

    /** 岗位状态 */
    @Getter
    @AllArgsConstructor
    public enum PostStatus {
        POST_STATUS_0(0, "禁用"),
        POST_STATUS_1(1, "正常"),
        ;
        private final Integer code;
        private final String desc;
    }

}
