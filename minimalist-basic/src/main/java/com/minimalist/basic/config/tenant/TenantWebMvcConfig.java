package com.minimalist.basic.config.tenant;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TenantWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantDatasourceInterceptor())
                .addPathPatterns("/**")
                //仅排除匿名接口（登录、验证码），其余所有已认证请求均走数据源路由
                .excludePathPatterns(
                        "/basic/user/login",
                        "/basic/user/getImageCaptcha"
                );
    }

}
