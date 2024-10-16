package com.minimalist.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.minimalist.**.mapper")
@SpringBootApplication(scanBasePackages = {"com.minimalist.*", "cn.hutool.extra.spring"})
public class MinimalistBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinimalistBasicApplication.class, args);
    }

}
