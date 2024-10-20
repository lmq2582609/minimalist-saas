package com.minimalist.basic.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import com.minimalist.basic.entity.enums.*;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.mapper.MRolePermMapper;
import com.minimalist.basic.mapper.MTenantMapper;
import com.minimalist.basic.mapper.MTenantPackagePermMapper;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.basic.config.exception.BusinessException;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

    /**
     * 检查两个用户的租户ID是否匹配
     * @param optUserId 操作的用户ID
     * @param loginUserId 当前登陆人用户ID
     */
    public MUser checkTenantEqual(long optUserId, long loginUserId) {
        //检查租户ID，要删除的用户的租户必须与本次操作人的租户一致
        MUser optUser = userMapper.selectUserByUserId(optUserId);
        Assert.notNull(optUser, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_OPT_ACCOUNT.getDesc()));
        MUser loginUser = userMapper.selectUserByUserId(loginUserId);
        Assert.notNull(loginUser, () -> new BusinessException(UserEnum.ErrorMsg.AUTH_EXPIRED.getDesc()));
        Assert.isTrue(optUser.getTenantId().equals(loginUser.getTenantId()),
                () -> new BusinessException(RespEnum.NO_OPERATION_PERMISSION.getDesc()));
        return optUser;
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

}
