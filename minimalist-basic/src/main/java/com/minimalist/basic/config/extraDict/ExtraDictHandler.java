package com.minimalist.basic.config.extraDict;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalist.basic.service.ExtraDictService;
import com.minimalist.basic.entity.bo.BeanMethod;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Map;

@Component
public class ExtraDictHandler {

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

    /** 全部用户字典type */
    public static final String USER_ALL_LIST = "dict-user-all-list";

    /** 租户套餐字典type */
    public static final String TENANT_PACKAGE_LIST = "tenant-package-list";

    /**
     * 缓存额外字典数据处理方法
     */
    @PostConstruct
    public void initDictMethodHandler() {
        ExtraDictService extraDictService = SpringUtil.getBean(ExtraDictService.class);
        Method[] methods = extraDictService.getClass().getMethods();
        for (Method method : methods) {
            ExtraDict extraDict = method.getAnnotation(ExtraDict.class);
            if (ObjectUtil.isNotNull(extraDict) && StrUtil.isNotBlank(extraDict.dictType())) {
                dictMethodMap.put(extraDict.dictType(), new BeanMethod<>(extraDictService, method));
            }
        }
    }

}
