package com.minimalist.application.config.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.minimalist.basic.config.exception.BusinessException;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        /** 演示环境，可以删除 ------- start */
//        SaInterceptor saInterceptor = new SaInterceptor(handle -> {
//            //校验登录
//            StpUtil.checkLogin();
//            // 根据请求类型匹配
//            SaRouter.match(SaHttpMethod.POST, SaHttpMethod.DELETE, SaHttpMethod.PUT)
//                    .check(() -> {
//                        throw new BusinessException("演示环境，只能查询，不能进行操作");
//                    });
//        });
//        registry.addInterceptor(saInterceptor).addPathPatterns("/**")
//                .excludePathPatterns("/basic/user/logout");
//        /** 演示环境，可以删除 ------- end */


        //上面演示环境删除后，下面的注视需要放开

        //全局拦截配置，必须登陆后才可访问，如果某个接口需要跳过拦截，需要在接口上增加@SaIgnore注解
        SaInterceptor saInterceptor = new SaInterceptor(handle -> StpUtil.checkLogin());
        registry.addInterceptor(saInterceptor).addPathPatterns("/**");
    }

}

