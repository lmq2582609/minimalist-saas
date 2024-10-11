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

}
