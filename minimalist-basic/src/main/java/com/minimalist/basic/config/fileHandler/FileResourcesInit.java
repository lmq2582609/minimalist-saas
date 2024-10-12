package com.minimalist.basic.config.fileHandler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.fileHandler.entity.LocalFileEntity;
import com.minimalist.basic.entity.enums.StorageEnum;
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.mapper.MStorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 静态资源目录映射，只有本地存储需要映射
 * 如果在存储管理中，修改了本地存储的文件存储路径，那么程序需要重启才可以生效
 * 启动后执行一次
 */
@Order(-1)
@Configuration
public class FileResourcesInit implements WebMvcConfigurer {

    @Autowired
    private MStorageMapper storageMapper;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //查询本地存储
        LambdaQueryWrapper<MStorage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MStorage::getStorageType, StorageEnum.StorageType.LOCAL.getCode());
        queryWrapper.eq(MStorage::getStatus, StatusEnum.STATUS_1.getCode());
        List<MStorage> storageList = storageMapper.selectList(queryWrapper);
        for (MStorage storage : storageList) {
            LocalFileEntity localFileEntity = JSONUtil.toBean(storage.getStorageConfig(), LocalFileEntity.class);
            registry.addResourceHandler("/files/**").addResourceLocations("file:" + localFileEntity.getStoragePath());
        }
        System.out.println("-------------------- 初始化静态资源映射完毕 --------------------");
    }

}
