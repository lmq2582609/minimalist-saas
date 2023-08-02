package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PermEnum {

    /** 权限处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_PERM("权限不存在"),
        CONTAIN_CHILDREN("该权限下包含子项，请先删除子项"),
        ;
        private final String desc;
    }

    /** 权限状态 */
    @Getter
    @AllArgsConstructor
    public enum PermStatus {
        PERM_STATUS_0(0, "禁用"),
        PERM_STATUS_1(1, "正常"),
        ;
        private final Integer code;
        private final String desc;
    }

    /** 权限类型 */
    @Getter
    @AllArgsConstructor
    public enum PermType {
        MENU("M", "菜单"),
        BUTTON("B", "按钮"),
        ;
        private final String code;
        private final String desc;
    }

    /** 获取权限位置 */
    @Getter
    @AllArgsConstructor
    public enum GetPermPos {
        ROLE(1, "在角色管理页获取权限"),
        TENANT_PACKAGE(2, "在租户套餐管理页获取权限"),
        ;
        private final Integer code;
        private final String desc;
    }

}
