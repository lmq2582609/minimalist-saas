package com.minimalist.basic.config.eDict;

import cn.hutool.core.map.MapUtil;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class EDictConstant {

    /** 存储额外字典数据处理的方法 */
    public static final Map<String, BeanMethod<?>> dictMethodMap = MapUtil.newConcurrentHashMap();

    /** 部门字典type */
    public static final String DEPT_LIST = "dict-dept-list";

    /** 岗位字典type */
    public static final String POST_LIST = "dict-post-list";

    /** 角色字典type */
    public static final String ROLE_LIST = "dict-role-list";

    /** 用户字典type */
    public static final String USER_LIST = "dict-user-list";

    /** 租户字典type */
    public static final String TENANT_LIST = "dict-tenant-list";

    /** 租户套餐字典type */
    public static final String TENANT_PACKAGE_LIST = "tenant-package-list";

}
