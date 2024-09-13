package com.minimalist.basic.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class DeptEnum {

    /** 部门处理异常信息 */
    @Getter
    @AllArgsConstructor
    public enum ErrorMsg {
        NONENTITY_DEPT("部门不存在"),
        CONTAIN_CHILDREN("该权限下包含子项，请先删除子项"),
        USER_DEPT_CHILDREN("有用户在该部门，请先调整该用户部门"),
        ;
        private final String desc;
    }

    /** 部门状态 */
    @Getter
    @AllArgsConstructor
    public enum DeptStatus {
        DEPT_STATUS_0(0, "禁用"),
        DEPT_STATUS_1(1, "正常"),
        ;
        private final Integer code;
        private final String desc;
    }

    /** 获取部门位置 */
    @Getter
    @AllArgsConstructor
    public enum GetDeptPos {
        USER(1, "在用户管理页获取部门"),
        ;
        private final Integer code;
        private final String desc;
    }

}
