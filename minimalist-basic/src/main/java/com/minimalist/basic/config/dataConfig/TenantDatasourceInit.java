package com.minimalist.basic.config.dataConfig;

import cn.hutool.core.bean.BeanUtil;
import com.minimalist.basic.entity.po.MTenantDatasource;
import com.minimalist.basic.entity.vo.tenant.TenantDatasourceVO;
import com.minimalist.basic.mapper.MTenantDatasourceMapper;
import com.minimalist.basic.utils.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TenantDatasourceInit implements ApplicationRunner {

    @Autowired
    private MTenantDatasourceMapper tenantDatasourceMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //租户数据源
        List<MTenantDatasource> tenantDatasourceList = tenantDatasourceMapper.selectAll();
        //初始化将租户数据源缓存到Map中
        for (MTenantDatasource tenantDatasource : tenantDatasourceList) {
            TenantDatasourceVO tenantDatasourceVO = BeanUtil.copyProperties(tenantDatasource, TenantDatasourceVO.class);
            CommonConstant.tenantDatasourceMap.put(tenantDatasource.getTenantId(), tenantDatasourceVO);
        }
    }

}
