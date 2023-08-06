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
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        //Spring代理类中的方法
        Method[] proxyMethods = extraDictService.getClass().getDeclaredMethods();
        //Spring代理类中的方法转Map，key：方法名，value：代理方法
        Map<String, Method> proxyMethodMap = Arrays.stream(proxyMethods).collect(Collectors.toMap(Method::getName, Function.identity(), (v1, v2) -> v1));
        //获取Spring代理类，再获取代理目标类
        Class<?> superclass = extraDictService.getClass().getSuperclass();
        //获取代理目标类中的方法，目的是为了获取到 ExtraDict 自定义注解
        Method[] methods = superclass.getDeclaredMethods();
        for (Method method : methods) {
            //获取自定义注解
            ExtraDict extraDict = method.getDeclaredAnnotation(ExtraDict.class);
            if (ObjectUtil.isNotNull(extraDict) && StrUtil.isNotBlank(extraDict.dictType())) {
                //从代理方法中查找并缓存代理方法，如果代理方法不存在，则缓存代理目标类方法
                Method proxyMethod = proxyMethodMap.get(method.getName());
                if (ObjectUtil.isNotNull(proxyMethod)) {
                    dictMethodMap.put(extraDict.dictType(), new BeanMethod<>(extraDictService, proxyMethod));
                } else {
                    dictMethodMap.put(extraDict.dictType(), new BeanMethod<>(extraDictService, method));
                }
            }
        }
    }

}
