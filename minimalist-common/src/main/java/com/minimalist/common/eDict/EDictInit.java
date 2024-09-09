package com.minimalist.common.eDict;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalist.common.entity.BeanMethod;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 额外字典处理，初始化加载字典处理方法
 */
@Component
public class EDictInit implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        //getBean，找到含有额外字典处理的注解方法
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            postProcessAfterInitialization(bean);
        }
    }

    public void postProcessAfterInitialization(Object bean) {
        //Spring代理类中的方法
        Method[] proxyMethods = bean.getClass().getDeclaredMethods();
        //Spring代理类中的方法转Map，key：方法名，value：代理方法
        Map<String, Method> proxyMethodMap = Arrays.stream(proxyMethods).collect(Collectors.toMap(Method::getName, Function.identity(), (v1, v2) -> v1));
        //判断是否是cglib动态代理
        boolean cglibProxy = AopUtils.isCglibProxy(bean);
        if (cglibProxy) {
            //获取Spring代理类，再获取代理目标类
            Class<?> superclass = bean.getClass().getSuperclass();
            //获取代理目标类中的方法，目的是为了获取到 ExtraDict 自定义注解
            Method[] methods = superclass.getDeclaredMethods();
            for (Method method : methods) {
                cacheBeanMethod(method, proxyMethodMap, bean);
            }
        } else {
            //不是cglib动态代理，可能是其他代理，暂时还没遇到其他代理扫描不到的情况
            for (Method method : proxyMethods) {
                cacheBeanMethod(method, proxyMethodMap, bean);
            }
        }
    }

    private void cacheBeanMethod(Method method, Map<String, Method> proxyMethodMap, Object bean) {
        //获取自定义注解
        EDict eDict = method.getDeclaredAnnotation(EDict.class);
        if (ObjectUtil.isNotNull(eDict) && StrUtil.isNotBlank(eDict.dictType())) {
            //从代理方法中查找并缓存代理方法，如果代理方法不存在，则缓存代理目标类方法
            Method proxyMethod = proxyMethodMap.get(method.getName());
            if (ObjectUtil.isNotNull(proxyMethod)) {
                EDictConstant.dictMethodMap.put(eDict.dictType(), new BeanMethod<>(bean, proxyMethod));
            } else {
                EDictConstant.dictMethodMap.put(eDict.dictType(), new BeanMethod<>(bean, method));
            }
        }
    }

}
