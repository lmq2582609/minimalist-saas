package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RespEnum {

    /** 操作成功 */
    SUCCESS(200, "操作成功"),

    /** 系统异常,请稍后再试 */
    FAILED(500, "系统异常,请稍后再试"),

    /** 参数错误 */
    PARAM_ERROR(400, "参数错误"),

    /** 用户认证失败，请重新登录 */
    REQUEST_UNAUTH(401, "用户认证失败，请重新登录"),

    /** 无操作权限 */
    NO_OPERATION_PERMISSION(403, "暂无操作权限"),

    /** 无操作权限 */
    TAMPER_WITH_DATA(400, "请勿篡改数据"),

    /** 重复提交 */
    RESUBMIT_ERROR(503, "请求已提交，请稍后重试"),

    /** 参数存在XSS敏感字符 */
    PARAM_XSS_ERROR(400, "存在敏感数据，不能提交"),

    ;

    private final Integer code;
    private final String desc;

}
