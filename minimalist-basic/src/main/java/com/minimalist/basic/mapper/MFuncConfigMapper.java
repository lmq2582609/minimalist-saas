package com.minimalist.basic.mapper;

import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MFuncConfig;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * 功能配置表 映射层。
 *
 * @author 小太阳
 * @since 2026-08-04
 */
public interface MFuncConfigMapper extends BaseMapper<MFuncConfig> {

    /**
     * 根据配置键查询配置
     * @param configKey 配置键
     * @return 配置信息
     */
    default MFuncConfig selectByConfigKey(String configKey) {
        return selectOneByQuery(QueryWrapper.create().eq(MFuncConfig::getConfigKey, configKey));
    }

    /**
     * 根据配置ID更新配置
     * @param funcConfig 配置信息
     */
    default void updateByConfigId(MFuncConfig funcConfig) {
        updateByQuery(funcConfig, QueryWrapper.create().eq(MFuncConfig::getConfigId, funcConfig.getConfigId()));
    }

}
