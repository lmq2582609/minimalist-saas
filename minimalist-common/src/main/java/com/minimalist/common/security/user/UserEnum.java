package com.minimalist.common.security.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserEnum {

    /** 用户处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        CAPTCHA_ID_EMPTY("验证码ID为空"),
        CAPTCHA_CONTENT_EMPTY("验证码为空"),
        CAPTCHA_INCORRECT("验证码输入错误"),
        U_OR_P_INCORRECT("用户名或密码错误"),
        USER_FROZEN("账户已被冻结"),
        AUTH_EXPIRED("登录信息过期，请重新登录"),
        ACCOUNT_EXPIRED("账户已过期"),
        BAD_INVALID("凭证无效，请重新登录"),
        EXISTS_ACCOUNT("用户名已存在"),
        PHONE_ACCOUNT("用户手机已存在"),
        EMAIL_ACCOUNT("用户邮箱已存在"),
        NONENTITY_ACCOUNT("用户不存在"),
        OLD_PASSWORD_INCORRECT("旧密码输入错误"),
        USER_AVATAR_SIZE("头像大小需小于100kb"),
        LOGIN_USER_INCONSISTENT("获取的用户信息与当前登陆用户不一致"),
        USER_UNBOUND_TENANT("该账户未绑定租户"),

        ;
        private final String desc;
    }

    /** 用户状态 */
    @Getter
    @AllArgsConstructor
    public enum UserStatus {
        USER_STATUS_0(0, "禁用"),
        USER_STATUS_1(1, "正常"),
        ;
        private final Integer code;
        private final String desc;
    }

    /** 用户性别 */
    @Getter
    @AllArgsConstructor
    public enum UserSex {
        USER_SEX_0(0, "未知"),
        USER_SEX_1(1, "男"),
        USER_SEX_2(2, "女"),
        ;
        private final Integer code;
        private final String desc;
    }

}
