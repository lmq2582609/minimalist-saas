package com.minimalist.basic.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.minimalist.basic.entity.enums.*;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.entity.vo.tenant.TenantDatasourceVO;
import com.minimalist.basic.mapper.MRolePermMapper;
import com.minimalist.basic.mapper.MTenantMapper;
import com.minimalist.basic.mapper.MTenantPackagePermMapper;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.basic.config.exception.BusinessException;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户相关的辅助处理
 */
@Component
public class TenantManager {

    @Autowired
    private MTenantMapper tenantMapper;

    @Autowired
    private MUserMapper userMapper;

    @Autowired
    private MRolePermMapper rolePermMapper;

    @Autowired
    private MTenantPackagePermMapper tenantPackagePermMapper;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DefaultDataSourceCreator dataSourceCreator;

    /**
     * 检查租户套餐
     * @param tenantId 租户ID
     */
    public void checkTenantPackage(long tenantId) {
        //检查租户下用户数是否满足套餐
        MTenant mTenant = tenantMapper.selectTenantByTenantId(tenantId);
        Assert.notNull(mTenant, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT.getDesc()));
        long userCount = userMapper.selectUserCountByTenantId(tenantId);
        // +1是去除租户本身的用户
        Assert.isFalse((userCount + 1) >= mTenant.getAccountCount(),
                () -> new BusinessException(TenantEnum.ErrorMsg.TENANT_USER_COUNT_LIMIT.getDesc()));
        //检查租户状态
        Assert.isTrue(StatusEnum.STATUS_1.getCode().equals(mTenant.getStatus()),
                () -> new BusinessException(TenantEnum.ErrorMsg.DISABLED_TENANT.getDesc()));
        //检查租户是否过期
        checkTenantExpireTime(mTenant.getExpireTime());
    }

    /**
     * 检查租户是否过期
     * @param expireTime 租户到期时间
     */
    public void checkTenantExpireTime(LocalDateTime expireTime) {
        //获取当天最晚时间，23:59:59
        LocalDateTime localDateTime = LocalDateTimeUtil.endOfDay(LocalDateTime.now());
        //检查租户是否过期
        Duration duration = LocalDateTimeUtil.between(localDateTime, expireTime);
        //如果租户到期时间 < 当天，返回负，说明已到期
        long exHours = duration.toHours();
        Assert.isFalse(exHours <= 0, () -> new BusinessException(TenantEnum.ErrorMsg.EX_TENANT.getDesc()));
    }

    public void updateTenantPermission(List<MRole> roleList, Long packageId) {
        //修改后的套餐权限
        List<MTenantPackagePerm> newTpp = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(packageId);
        List<Long> permIds = newTpp.stream().map(MTenantPackagePerm::getPermId).toList();
        for (MRole role : roleList) {
            //如果是租户管理员，将套餐所有权限重新分配给租户管理员
            if (RoleEnum.Role.ADMIN.getCode().equals(role.getRoleCode())) {
                //删除旧关联数据
                LogicDeleteManager.execWithoutLogicDelete(()->
                        rolePermMapper.deleteByQuery(QueryWrapper.create().eq(MRolePerm::getRoleId, role.getRoleId()))
                );
                //插入新关联数据
                List<MRolePerm> rolePerms = newTpp.stream().map(tpp -> {
                    MRolePerm rolePerm = new MRolePerm();
                    rolePerm.setRoleId(role.getRoleId());
                    rolePerm.setPermId(tpp.getPermId());
                    return rolePerm;
                }).toList();
                rolePermMapper.insertBatch(rolePerms);
            } else {
                //如果是其他角色，删除超出的权限
                if (CollectionUtil.isNotEmpty(permIds)) {
                    rolePermMapper.deleteByQuery(QueryWrapper.create()
                            .eq(MRolePerm::getRoleId, role.getRoleId())
                            .notIn(MRolePerm::getPermId, permIds)
                    );
                }
            }
        }
    }

    /**
     * 动态添加数据源
     * @param tenantId 租户ID
     * @param tenantDatasourceVO 数据源信息
     */
    public void dynamicAddDatasource(String tenantId, TenantDatasourceVO tenantDatasourceVO) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setUrl(tenantDatasourceVO.getDatasourceUrl());
        dataSourceProperty.setUsername(tenantDatasourceVO.getUsername());
        dataSourceProperty.setPassword(tenantDatasourceVO.getPassword());
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(tenantId, dataSource);
    }

    /**
     * 动态删除数据源
     * @param tenantId 租户ID
     */
    public void dynamicDeleteDatasource(String tenantId) {
        //动态删除数据源
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(tenantId);
    }

}
