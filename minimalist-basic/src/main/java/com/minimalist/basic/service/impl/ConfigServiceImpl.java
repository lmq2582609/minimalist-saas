package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.entity.enums.ConfigEnum;
import com.minimalist.basic.entity.enums.RespEnum;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.config.redis.RedisManager;
import com.minimalist.basic.entity.po.MConfig;
import com.minimalist.basic.entity.vo.config.ConfigQueryVO;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.basic.mapper.MConfigMapper;
import com.minimalist.basic.service.ConfigService;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.RedisKeyConstant;
import com.minimalist.basic.utils.UnqIdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
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
        configMapper.insert(insertConfig, true);
        //发布消息 - 新增
        redisManager.publishMessage(RedisKeyConstant.SYSTEM_CONFIG_TOPIC_KEY + "." + CommonConstant.ADD, JSONUtil.toJsonStr(configVO));
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
        //发布消息 - 删除
        redisManager.publishMessage(RedisKeyConstant.SYSTEM_CONFIG_TOPIC_KEY + "." + CommonConstant.DELETE, config.getConfigKey());
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
        MConfig updateConfig = BeanUtil.copyProperties(configVO, MConfig.class);
        configMapper.updateConfigByConfigId(updateConfig);
        //发布消息 - 修改
        redisManager.publishMessage(RedisKeyConstant.SYSTEM_CONFIG_TOPIC_KEY + "." + CommonConstant.UPDATE, JSONUtil.toJsonStr(configVO));
    }

    /**
     * 查询参数配置列表(分页)
     * @param queryVO 查询条件
     * @return 参数配置列表
     */
    @Override
    public PageResp<ConfigVO> getPageConfigList(ConfigQueryVO queryVO) {
        Page<MConfig> configPage = configMapper.selectPageConfigList(queryVO);
        List<ConfigVO> configVOList = BeanUtil.copyToList(configPage.getRecords(), ConfigVO.class);
        return new PageResp<>(configVOList, configPage.getTotalRow());
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
        ConfigVO configVO = CommonConstant.systemConfigMap.get(configKey);
        if (ObjectUtil.isNull(configVO)) {
            MConfig mConfig = configMapper.selectConfigByConfigKey(configKey, StatusEnum.STATUS_1.getCode());
            configVO = BeanUtil.copyProperties(mConfig, ConfigVO.class);
            //重新放入缓存
            CommonConstant.systemConfigMap.put(configKey, configVO);
        }
        return configVO;
    }

    /**
     * 刷新配置缓存
     */
    @Override
    public void refreshConfigCache() {
        List<MConfig> configList = configMapper.selectListByQuery(
                QueryWrapper.create().eq(MConfig::getStatus, StatusEnum.STATUS_1.getCode()));
        for (MConfig config : configList) {
            ConfigVO configVO = BeanUtil.copyProperties(config, ConfigVO.class);
            //缓存系统配置到Map
            CommonConstant.systemConfigMap.put(configVO.getConfigKey(), configVO);
        }
    }
}

