package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公共的状态枚举
 * 对应每张表中的status字段
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {
    STATUS_0(0, "禁用"),
    STATUS_1(1, "正常"),
    ;
    private final Integer code;
    private final String desc;
}
