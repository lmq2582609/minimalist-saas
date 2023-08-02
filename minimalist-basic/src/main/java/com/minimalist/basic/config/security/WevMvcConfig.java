package com.minimalist.basic.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 在springboot中，一般使用static目录下存放静态资源，当文件需要上传到本地时，自定义的路径就访问不到了，所以需要配置一个路径映射。
 * 如果本地不涉及其他路径的文件访问，比如使用oss存储等，则可以删除该类。
 */
@Configuration
public class WevMvcConfig extends WebMvcConfigurationSupport {

    @Value("${spring.file-storage.local[0].base-path}")
    private String basePath;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //访问 /files/** 的路径时，映射到本地 basePath 路径，注意/files/**路径需要在spring security中放行
        registry.addResourceHandler("/files/**").addResourceLocations("file:" + basePath);
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
