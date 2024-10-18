package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.vo.config.ConfigQueryVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MConfig;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * 参数配置表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MConfigMapper extends BaseMapper<MConfig> {

    /**
     * 根据参数ID查询参数
     * @param configId 参数ID
     * @return 参数信息
     */
    default MConfig selectConfigByConfigId(Long configId) {
        return selectOneByQuery(QueryWrapper.create().eq(MConfig::getConfigId, configId));
    }

    /**
     * 根据参数键名和状态查询参数
     * @param configKey 参数键名
     * @param status 参数状态
     * @return 参数信息
     */
    default MConfig selectConfigByConfigKey(String configKey, Integer status) {
        return selectOneByQuery(QueryWrapper.create()
                .eq(MConfig::getConfigKey, configKey)
                .eq(MConfig::getStatus, status));
    }

    /**
     * 根据参数ID删除参数
     * @param configId 参数ID
     * @return 受影响行数
     */
    default int deleteConfigByConfigId(Long configId) {
        return deleteByQuery(QueryWrapper.create().eq(MConfig::getConfigId, configId));
    }

    /**
     * 根据参数ID修改参数
     * @param updateConfig 参数信息
     */
    default void updateConfigByConfigId(MConfig updateConfig) {
        updateByQuery(updateConfig, QueryWrapper.create().eq(MConfig::getConfigId, updateConfig.getConfigId()));
    }

    /**
     * 查询参数配置列表(分页)
     * @param queryVO 查询条件
     * @return 参数配置列表
     */
    default Page<MConfig> selectPageConfigList(ConfigQueryVO queryVO) {
        return paginate(queryVO.getPageNum(), queryVO.getPageSize(),
                QueryWrapper.create()
                        .eq(MConfig::getStatus, queryVO.getStatus())
                        .like(MConfig::getConfigName, queryVO.getConfigName())
                        .like(MConfig::getConfigKey, queryVO.getConfigKey()));

    }

}
