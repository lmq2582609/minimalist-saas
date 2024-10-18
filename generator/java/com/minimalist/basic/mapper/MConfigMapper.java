package com.minimalist.basic.mapper;

import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MConfig;

/**
 * 参数配置表 映射层。
 *
 * @author asus
 * @since 2024-10-18
 */
public interface MConfigMapper extends BaseMapper<MConfig> {

    /**
     * 根据参数ID查询参数
     * @param configId 参数ID
     * @return 参数信息
     */
    default MConfig selectConfigByConfigId(Long configId) {
        LambdaQueryWrapper<MConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MConfig::getConfigId, configId);
        return selectOne(queryWrapper);
    }

    /**
     * 根据参数键名和状态查询参数
     * @param configKey 参数键名
     * @param status 参数状态
     * @return 参数信息
     */
    default MConfig selectConfigByConfigKey(String configKey, Integer status) {
        LambdaQueryWrapper<MConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MConfig::getConfigKey, configKey);
        if (ObjectUtil.isNotNull(status)) {
            queryWrapper.eq(MConfig::getStatus, status);
        }
        return selectOne(queryWrapper);
    }

    /**
     * 根据参数ID删除参数
     * @param configId 参数ID
     * @return 受影响行数
     */
    default int deleteConfigByConfigId(Long configId) {
        LambdaQueryWrapper<MConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MConfig::getConfigId, configId);
        return delete(queryWrapper);
    }

    /**
     * 根据参数ID修改参数
     * @param updateConfig 参数信息
     */
    default void updateConfigByConfigId(MConfig updateConfig) {
        LambdaUpdateWrapper<MConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MConfig::getConfigId, updateConfig.getConfigId());
        update(updateConfig, updateWrapper);
    }

    /**
     * 查询参数配置列表(分页)
     * @param queryVO 查询条件
     * @return 参数配置列表
     */
    default Page<MConfig> selectPageRoleList(ConfigQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MConfig>()
                        .likeNotNull(MConfig::getConfigName, queryVO.getConfigName())
                        .likeNotNull(MConfig::getConfigKey, queryVO.getConfigKey())
                        .eqNotNull(MConfig::getStatus, queryVO.getStatus())
        );
    }

}
