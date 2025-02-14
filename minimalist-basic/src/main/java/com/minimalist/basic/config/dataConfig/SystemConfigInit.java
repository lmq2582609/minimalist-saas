package com.minimalist.basic.config.dataConfig;

import com.minimalist.basic.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SystemConfigInit implements ApplicationRunner {

    @Autowired
    private ConfigService configService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //初始化将系统参数缓存到Map中
        configService.refreshConfigCache();
    }

}
