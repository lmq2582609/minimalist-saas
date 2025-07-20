package com.minimalist.basic.config.tenant;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.po.MConfig;
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.entity.po.MTenant;
import com.minimalist.basic.entity.po.MTenantDatasource;
import com.minimalist.basic.entity.vo.storage.StorageVO;
import com.minimalist.basic.entity.vo.tenant.TenantDatasourceVO;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.manager.TenantManager;
import com.minimalist.basic.mapper.MConfigMapper;
import com.minimalist.basic.mapper.MStorageMapper;
import com.minimalist.basic.mapper.MTenantDatasourceMapper;
import com.minimalist.basic.mapper.MTenantMapper;
import com.minimalist.basic.utils.CommonConstant;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TenantInit implements ApplicationRunner {

    @Autowired
    private MTenantMapper tenantMapper;

    @Autowired
    private MTenantDatasourceMapper tenantDatasourceMapper;

    @Autowired
    private TenantManager tenantManager;

    @Autowired
    private MStorageMapper storageMapper;

    @Autowired
    private MConfigMapper configMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //查询系统多租户开关
        MConfig mConfig = configMapper.selectConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_TENANT, StatusEnum.STATUS_1.getCode());
        if (ObjectUtil.isNotNull(mConfig)) {
            boolean tenantOnOff = Boolean.parseBoolean(mConfig.getConfigValue());
            if (!tenantOnOff) {
                //未打开多租户开关，忽略多租户
                return;
            }
        }

        List<MTenant> mTenants = tenantMapper.selectListByQuery(QueryWrapper.create()
                .eq(MTenant::getStatus, StatusEnum.STATUS_1.getCode()));
        //租户数据库隔离数据源
        List<MTenantDatasource> tenantDatasourceList = tenantDatasourceMapper.selectAll();
        Map<Long, MTenantDatasource> tenantDatasourceMap = tenantDatasourceList.stream().collect(Collectors.toMap(MTenantDatasource::getTenantId, Function.identity()));
        //租户文件存储
        List<MStorage> mStorages = storageMapper.selectListByQuery(QueryWrapper.create()
                .eq(MStorage::getStatus, StatusEnum.STATUS_1.getCode()));
        Map<Long, MStorage> storageMap = mStorages.stream().collect(Collectors.toMap(MStorage::getStorageId, Function.identity()));
        for (MTenant tenant : mTenants) {
            TenantVO tenantVO = BeanUtil.copyProperties(tenant, TenantVO.class);
            //查询租户数据源
            if (tenantDatasourceMap.containsKey(tenant.getTenantId())) {
                //动态添加数据源
                MTenantDatasource tenantDatasource = tenantDatasourceMap.get(tenant.getTenantId());
                TenantDatasourceVO tenantDatasourceVO = BeanUtil.copyProperties(tenantDatasource, TenantDatasourceVO.class);
                tenantManager.dynamicAddDatasource(tenant.getTenantId().toString(), tenantDatasourceVO);
                //租户数据源信息
                tenantVO.setTenantDatasource(tenantDatasourceVO);
            }
            //租户文件存储
            MStorage storage = storageMap.get(tenant.getStorageId());
            tenantVO.setStorage(BeanUtil.copyProperties(storage, StorageVO.class));
            //缓存租户信息
            CommonConstant.tenantMap.put(tenant.getTenantId(), tenantVO);
        }
    }

}
