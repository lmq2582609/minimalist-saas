package com.minimalist.basic.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /** 标题 */
    @Value("${springdoc.title}")
    private String title;

    /** 作者 */
    @Value("${springdoc.authorName}")
    private String authorName;

    /** 作者主页 */
    @Value("${springdoc.authorUrl}")
    private String authorUrl;

    /** 作者Email */
    @Value("${springdoc.authorEmail}")
    private String authorEmail;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                //文档页面标题
                .title(title)
                //描述
                .description(title + "接口文档")
                //版本号
                .version("1.0.0-SNAPSHOT")
                //作者信息
                .contact(new Contact().name(authorName).email(authorEmail).url(authorUrl)));
    }

    @Bean
    @ConditionalOnMissingBean
    public PropertyHandler propertyHandler() {
        return new PropertyHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ParameterHandler parameterHandler() {
        return new ParameterHandler();
    }

}
