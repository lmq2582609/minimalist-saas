package com.minimalist.common.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.common.constant.RedisKeyConstant;
import com.minimalist.common.enums.StatusEnum;
import com.minimalist.common.module.entity.enums.ConfigEnum;
import com.minimalist.common.module.entity.po.MConfig;
import com.minimalist.common.module.entity.vo.config.ConfigQueryVO;
import com.minimalist.common.module.entity.vo.config.ConfigVO;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.module.mapper.MConfigMapper;
import com.minimalist.common.module.service.ConfigService;
import com.minimalist.common.redis.RedisManager;
import com.minimalist.common.utils.UnqIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private MConfigMapper configMapper;

    @Autowired
    private RedisManager redisManager;

    /**
     * 添加参数
     * @param configVO 参数配置信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addConfig(ConfigVO configVO) {
        //检查键名唯一性
        MConfig config = configMapper.selectConfigByConfigKey(configVO.getConfigKey(), null);
        Assert.isNull(config, () -> new BusinessException(ConfigEnum.ErrorMsg.CONTAIN_CONFIG_KEY.getDesc()));
        MConfig insertConfig = BeanUtil.copyProperties(configVO, MConfig.class);
        insertConfig.setConfigId(UnqIdUtil.uniqueId());
        configMapper.insert(insertConfig);
        //添加后将配置放入缓存
        String redisKey = StrUtil.indexedFormat(RedisKeyConstant.SYSTEM_CONFIG_KEY, configVO.getConfigKey());
        redisManager.set(redisKey, configVO, RedisKeyConstant.SYSTEM_CONFIG_CACHE_EX);
    }

    /**
     * 删除参数 -> 根据参数ID删除
     * @param configId 参数ID
     */
    @Override
    public void deleteConfigByConfigId(Long configId) {
        MConfig config = configMapper.selectConfigByConfigId(configId);
        Assert.notNull(config, () -> new BusinessException(ConfigEnum.ErrorMsg.NONENTITY_CONFIG.getDesc()));
        Assert.isFalse(config.getConfigKey().startsWith("system."),
                () -> new BusinessException(ConfigEnum.ErrorMsg.CANNOT_DEL_SYSTEM_CONFIG.getDesc()));
        int deleteCount = configMapper.deleteConfigByConfigId(configId);
        Assert.isTrue(deleteCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //从缓存中删除参数
        String redisKey = StrUtil.indexedFormat(RedisKeyConstant.SYSTEM_CONFIG_KEY, config.getConfigKey());
        redisManager.delete(redisKey);
    }

    /**
     * 修改参数
     * @param configVO 参数配置信息
     */
    @Override
    public void updateConfigByConfigId(ConfigVO configVO) {
        //检查键名唯一性
        MConfig config = configMapper.selectConfigByConfigKey(configVO.getConfigKey(), null);
        if (ObjectUtil.isNotNull(config) && !configVO.getConfigId().equals(config.getConfigId())) {
            throw new BusinessException(ConfigEnum.ErrorMsg.CONTAIN_CONFIG_KEY.getDesc());
        }
        MConfig oldConfig = configMapper.selectConfigByConfigId(configVO.getConfigId());
        Assert.notNull(oldConfig, () -> new BusinessException(ConfigEnum.ErrorMsg.NONENTITY_CONFIG.getDesc()));
        MConfig updateConfig = BeanUtil.copyProperties(configVO, MConfig.class);
        //乐观锁字段赋值
        updateConfig.updateBeforeSetVersion(oldConfig.getVersion());
        configMapper.updateConfigByConfigId(updateConfig);
        //添加后将配置放入缓存
        String redisKey = StrUtil.indexedFormat(RedisKeyConstant.SYSTEM_CONFIG_KEY, configVO.getConfigKey());
        redisManager.set(redisKey, configVO, RedisKeyConstant.SYSTEM_CONFIG_CACHE_EX);
    }

    /**
     * 查询参数配置列表(分页)
     * @param queryVO 查询条件
     * @return 参数配置列表
     */
    @Override
    public PageResp<ConfigVO> getPageConfigList(ConfigQueryVO queryVO) {
        Page<MConfig> configPage = configMapper.selectPageRoleList(queryVO);
        List<ConfigVO> configVOList = BeanUtil.copyToList(configPage.getRecords(), ConfigVO.class);
        return new PageResp<>(configVOList, configPage.getTotal());
    }

    /**
     * 根据参数ID查询参数
     * @param configId 参数ID
     * @return 参数信息
     */
    @Override
    public ConfigVO getConfigByConfigId(Long configId) {
        return BeanUtil.copyProperties(configMapper.selectConfigByConfigId(configId), ConfigVO.class);
    }

    /**
     * 根据参数Key查询参数 - 状态为正常
     * @param configKey 参数Key
     * @return 参数信息
     */
    @Override
    public ConfigVO getConfigByConfigKey(String configKey) {
        String redisKey = StrUtil.indexedFormat(RedisKeyConstant.SYSTEM_CONFIG_KEY, configKey);
        ConfigVO configVO = redisManager.get(redisKey);
        if (ObjectUtil.isNull(configVO)) {
            MConfig mConfig = configMapper.selectConfigByConfigKey(configKey, StatusEnum.STATUS_1.getCode());
            configVO = BeanUtil.copyProperties(mConfig, ConfigVO.class);
            //重新放入缓存
            redisManager.set(redisKey, configVO, RedisKeyConstant.SYSTEM_CONFIG_CACHE_EX);
        }
        return configVO;
    }

    /**
     * 刷新配置缓存
     */
    @Override
    public void refreshConfigCache() {
        List<MConfig> configList = configMapper.selectList(new LambdaQueryWrapper<>());
        for (MConfig config : configList) {
            ConfigVO configVO = BeanUtil.copyProperties(config, ConfigVO.class);
            String redisKey = StrUtil.indexedFormat(RedisKeyConstant.SYSTEM_CONFIG_KEY, configVO.getConfigKey());
            redisManager.set(redisKey, configVO, RedisKeyConstant.SYSTEM_CONFIG_CACHE_EX);
        }
    }
}
