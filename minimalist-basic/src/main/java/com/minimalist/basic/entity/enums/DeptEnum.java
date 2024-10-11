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

}
