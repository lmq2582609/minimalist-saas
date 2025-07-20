package com.minimalist.basic.config.tenant;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TenantWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantDatasourceInterceptor())
                //排除/basic接口对数据源的限制，/basic只能使用master数据源
                .excludePathPatterns("/basic/**")
                .addPathPatterns("/**");
    }

}
