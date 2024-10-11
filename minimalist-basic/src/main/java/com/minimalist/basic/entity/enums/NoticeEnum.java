package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class NoticeEnum {

    /** 公告处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_NOTICE("公告不存在"),
        ;
        private final String desc;
    }

    /** 公告类型 */
    @Getter
    @AllArgsConstructor
    public enum NoticeType {
        NOTICE(1, "公告"),
        NEWS(2, "新闻"),
        ;
        private final Integer code;
        private final String desc;
    }

}
