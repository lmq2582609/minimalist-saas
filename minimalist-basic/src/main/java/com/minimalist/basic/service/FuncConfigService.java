package com.minimalist.basic.service;

import com.minimalist.basic.entity.vo.funcConfig.FuncConfigVO;

import java.util.List;
import java.util.Map;

/**
 * 功能配置 Service接口
 *
 * @author 小太阳
 * @since 2026-08-04
 */
public interface FuncConfigService {

    /**
     * 查询所有功能配置列表
     * @return 功能配置列表
     */
    List<FuncConfigVO> getFuncConfigList();

    /**
     * 修改功能配置
     * @param funcConfigVO 功能配置信息
     */
    void updateFuncConfig(FuncConfigVO funcConfigVO);

    /**
     * 获取功能配置键值对（供getUserInfo使用）
     * @return Map<configKey, configValue>
     */
    Map<String, String> getFuncConfigMap();

}
