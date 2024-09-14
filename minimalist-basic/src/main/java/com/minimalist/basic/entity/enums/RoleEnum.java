package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class RoleEnum {

    /** 角色 */
    @Getter
    @AllArgsConstructor
    public enum Role {
        /** 系统管理员，权限最高 */
        SYSTEM_ADMIN("system_admin", "系统管理员"),
        /** 租户管理员，租户权限范围内权限最高 */
        ADMIN("admin", "管理员"),
        ;
        private final String code;
        private final String name;
    }

    /** 角色处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        EXISTS_ROLE("角色已存在，请检查角色编码"),
        NONENTITY_ROLE("角色不存在"),
        ;
        private final String desc;
    }

    /** 角色状态 */
    @Getter
    @AllArgsConstructor
    public enum RoleStatus {
        ROLE_STATUS_0(0, "禁用"),
        ROLE_STATUS_1(1, "正常"),
        ;
        private final Integer code;
        private final String desc;
    }

}
