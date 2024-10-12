package com.minimalist.basic.config.fileHandler;//package com.minimalist.application.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
///**
// * 配置swagger映射
// * 在springboot中，一般使用static目录下存放静态资源，当文件需要上传到本地时，自定义的路径就访问不到了，所以需要配置一个路径映射。
// * 如果本地不涉及其他路径的文件访问，比如使用oss存储等，则可以删除该类。
// */
//@Configuration
//public class WevMvcConfig extends WebMvcConfigurationSupport {
//
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/files/**").addResourceLocations("file:E:/");
//        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//
//}
