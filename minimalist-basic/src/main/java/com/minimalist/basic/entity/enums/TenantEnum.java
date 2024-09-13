package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TenantEnum {

    /** 租户处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_TENANT("租户不存在"),
        EXISTS_TENANT("租户已存在，请检查租户名"),
        NONENTITY_TENANT_PACKAGE("租户套餐不存在"),
        USE_TENANT_PACKAGE("有租户正在使用该套餐，不能删除"),
        STATUS_TENANT_PACKAGE("选择的租户套餐已被禁用"),
        TENANT_USER_COUNT_LIMIT("租户下可创建的用户数已达上限"),
        EX_TENANT("租户已过期，请联系管理员"),
        DISABLED_TENANT("租户已被禁用，请联系管理员"),
        SYSTEM_TENANT("系统租户，不可删除"),
        SYSTEM_TENANT_PACKAGE("系统租户套餐，不可删除"),
        ADD_TENANT_USER_NULL("添加租户时用户信息不能为空"),
        ADD_TENANT_USERNAME_NULL("添加租户时用户账号不能为空"),
        ADD_TENANT_PASSWORD_NULL("添加租户时用户密码不能为空"),
        ADD_TENANT_NICKNAME_NULL("添加租户时用户昵称不能为空"),
        ADD_TENANT_REALNAME_NULL("添加租户时用户真实姓名不能为空"),
        ADD_TENANT_PHONE_NULL("添加租户时用户手机号不能为空"),
        ADD_TENANT_USERSEX_NULL("添加租户时用户性别不能为空"),
        ;
        private final String desc;
    }

    /** 租户状态 */
    @Getter
    @AllArgsConstructor
    public enum TenantStatus {
        TENANT_STATUS_0(0, "禁用"),
        TENANT_STATUS_1(1, "正常"),
        ;
        private final Integer code;
        private final String desc;
    }

    /** 租户套餐状态 */
    @Getter
    @AllArgsConstructor
    public enum TenantPackageStatus {
        TENANT_PACKAGE_STATUS_0(0, "禁用"),
        TENANT_PACKAGE_STATUS_1(1, "正常"),
        ;
        private final Integer code;
        private final String desc;
    }

}
