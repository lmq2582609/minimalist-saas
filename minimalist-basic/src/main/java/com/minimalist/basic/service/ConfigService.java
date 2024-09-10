package com.minimalist.basic.service;

import com.minimalist.basic.entity.vo.config.ConfigQueryVO;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.common.mybatis.bo.PageResp;

public interface ConfigService {

    /**
     * 添加参数
     * @param configVO 参数配置信息
     */
    void addConfig(ConfigVO configVO);

    /**
     * 删除参数 -> 根据参数ID删除
     * @param configId 参数ID
     */
    void deleteConfigByConfigId(Long configId);

    /**
     * 修改参数
     * @param configVO 参数配置信息
     */
    void updateConfigByConfigId(ConfigVO configVO);

    /**
     * 查询参数配置列表(分页)
     * @param queryVO 查询条件
     * @return 参数配置列表
     */
    PageResp<ConfigVO> getPageConfigList(ConfigQueryVO queryVO);
}
