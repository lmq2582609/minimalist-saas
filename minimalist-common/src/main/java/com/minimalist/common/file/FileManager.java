package com.minimalist.common.file;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalist.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class FileManager {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 根据存储类型获取对应的处理类
     * @param storageType 存储类型
     * @return 处理类
     */
    public FileHandler getFileHandler(String storageType) {
        if (StrUtil.isBlank(storageType)) {
            throw new BusinessException("处理文件时，存储平台参数为空");
        }
        Map<String, FileHandler> beansMaps = applicationContext.getBeansOfType(FileHandler.class);
        if (MapUtil.isEmpty(beansMaps)) {
            throw new BusinessException("处理文件时，未找到指定的处理方法");
        }
        return beansMaps.values().stream()
                .filter(bean -> bean.isHandler(storageType))
                .findFirst()
                .orElseThrow(() -> new BusinessException("未找到 [" + storageType + "] 的处理方法"));
    }

}
