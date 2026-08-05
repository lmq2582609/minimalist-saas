package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.entity.po.MFuncConfig;
import com.minimalist.basic.entity.po.MPerms;
import com.minimalist.basic.entity.vo.funcConfig.FuncConfigVO;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.basic.mapper.MFuncConfigMapper;
import com.minimalist.basic.mapper.MPermsMapper;
import com.minimalist.basic.service.ConfigService;
import com.minimalist.basic.service.FuncConfigService;
import com.minimalist.basic.utils.TenantUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能配置 Service实现
 *
 * @author 小太阳
 * @since 2026-08-04
 */
@Service
public class FuncConfigServiceImpl implements FuncConfigService {

    @Autowired
    private MFuncConfigMapper funcConfigMapper;

    @Autowired
    private MPermsMapper permsMapper;

    @Autowired
    private ConfigService configService;

    /**
     * 查询所有功能配置列表
     */
    @Override
    public List<FuncConfigVO> getFuncConfigList() {
        List<MFuncConfig> funcConfigList = funcConfigMapper.selectListByQuery(QueryWrapper.create());
        return BeanUtil.copyToList(funcConfigList, FuncConfigVO.class);
    }

    /**
     * 修改功能配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFuncConfig(FuncConfigVO funcConfigVO) {
        MFuncConfig existConfig = funcConfigMapper.selectByConfigKey(funcConfigVO.getConfigKey());
        Assert.notNull(existConfig, () -> new BusinessException("功能配置不存在"));

        MFuncConfig updateConfig = new MFuncConfig();
        updateConfig.setConfigId(existConfig.getConfigId());
        updateConfig.setConfigValue(funcConfigVO.getConfigValue());
        funcConfigMapper.updateByConfigId(updateConfig);

        //功能开关切换时，联动菜单显隐（系统租户不修改m_perms.visible，避免通过套餐同步影响所有租户）
        String configKey = funcConfigVO.getConfigKey();
        if ("feature.dept.enable".equals(configKey) || "feature.post.enable".equals(configKey)) {
            boolean isSystemTenant = TenantUtil.checkTenantOnOff() && TenantUtil.checkIsSystemTenant();
            if (!isSystemTenant) {
                toggleMenuVisibility(configKey, funcConfigVO.getConfigValue());
            }
        }
    }

    /**
     * 获取功能配置键值对
     */
    @Override
    public Map<String, String> getFuncConfigMap() {
        List<MFuncConfig> funcConfigList = funcConfigMapper.selectListByQuery(QueryWrapper.create());
        Map<String, String> map = new LinkedHashMap<>();
        for (MFuncConfig config : funcConfigList) {
            map.put(config.getConfigKey(), config.getConfigValue());
        }
        return map;
    }

    /**
     * 功能开关切换时，联动更新m_perms.visible
     * @param configKey 配置键
     * @param configValue 配置值 true/false
     */
    private void toggleMenuVisibility(String configKey, String configValue) {
        //确定对应的菜单匹配参数key（从主库m_config中读取）
        String paramsKey = "feature.dept.enable".equals(configKey) ? "dept_enable_params" : "post_enable_params";
        ConfigVO paramsConfig = configService.getConfigByConfigKey(paramsKey);
        Assert.notNull(paramsConfig, () -> new BusinessException("菜单匹配参数配置不存在: " + paramsKey));

        //解析JSON获取perm_name和perm_path
        JSONObject json = JSONUtil.parseObj(paramsConfig.getConfigValue());
        String permName = json.getStr("perm_name");
        String permPath = json.getStr("perm_path");

        //定位菜单记录
        MPerms targetPerm = permsMapper.selectOneByQuery(QueryWrapper.create()
                .eq(MPerms::getPermName, permName)
                .eq(MPerms::getPermPath, permPath));
        Assert.notNull(targetPerm, () -> new BusinessException("未找到对应菜单记录: " + permName));

        boolean enable = Boolean.parseBoolean(configValue);
        //更新该菜单的visible
        MPerms updatePerm = new MPerms();
        updatePerm.setPermId(targetPerm.getPermId());
        updatePerm.setVisible(enable);
        permsMapper.updatePermsByPermId(updatePerm);

        //同时处理该菜单下的所有子菜单/按钮
        updateChildrenVisibility(targetPerm.getPermId(), enable);
    }

    /**
     * 递归更新子菜单/按钮的visible
     * @param parentPermId 父权限ID
     * @param enable 是否可见
     */
    private void updateChildrenVisibility(Long parentPermId, boolean enable) {
        List<MPerms> children = permsMapper.selectListByQuery(
                QueryWrapper.create().eq(MPerms::getParentPermId, parentPermId));
        for (MPerms child : children) {
            MPerms updateChild = new MPerms();
            updateChild.setPermId(child.getPermId());
            updateChild.setVisible(enable);
            permsMapper.updatePermsByPermId(updateChild);
            //递归处理下级
            updateChildrenVisibility(child.getPermId(), enable);
        }
    }

}
