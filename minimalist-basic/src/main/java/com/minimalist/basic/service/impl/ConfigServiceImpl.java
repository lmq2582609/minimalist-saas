package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.ConfigEnum;
import com.minimalist.basic.entity.po.MConfig;
import com.minimalist.basic.entity.vo.config.ConfigQueryVO;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.basic.mapper.MConfigMapper;
import com.minimalist.basic.service.ConfigService;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.utils.UnqIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private MConfigMapper configMapper;

    /**
     * 添加参数
     * @param configVO 参数配置信息
     */
    @Override
    public void addConfig(ConfigVO configVO) {
        //检查键名唯一性
        MConfig config = configMapper.selectConfigByConfigKey(configVO.getConfigKey(), null);
        Assert.isNull(config, () -> new BusinessException(ConfigEnum.ErrorMsg.CONTAIN_CONFIG_KEY.getDesc()));
        MConfig insertConfig = BeanUtil.copyProperties(configVO, MConfig.class);
        insertConfig.setConfigId(UnqIdUtil.uniqueId());
        int insertCount = configMapper.insert(insertConfig);
        Assert.isTrue(insertCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 删除参数 -> 根据参数ID删除
     * @param configId 参数ID
     */
    @Override
    public void deleteConfigByConfigId(Long configId) {
        MConfig config = configMapper.selectConfigByConfigId(configId);
        Assert.notNull(config, () -> new BusinessException(ConfigEnum.ErrorMsg.NONENTITY_CONFIG.getDesc()));
        Assert.isFalse(config.getConfigKey().startsWith("system"),
                () -> new BusinessException(ConfigEnum.ErrorMsg.CANNOT_DEL_SYSTEM_CONFIG.getDesc()));
        int deleteCount = configMapper.deleteConfigByConfigId(configId);
        Assert.isTrue(deleteCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
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

}
