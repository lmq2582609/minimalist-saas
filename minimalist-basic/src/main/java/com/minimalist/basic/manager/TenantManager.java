package com.minimalist.basic.manager;

import cn.hutool.core.lang.Assert;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.po.MTenant;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.mapper.MTenantMapper;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.basic.entity.enums.UserEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 租户相关的辅助处理
 */
@Component
public class TenantManager {

    @Autowired
    private MTenantMapper tenantMapper;

    @Autowired
    private MUserMapper userMapper;

    public void checkTenantPackage(long tenantId) {
        //检查租户下用户数是否满足套餐
        MTenant mTenant = tenantMapper.selectTenantByTenantId(tenantId);
        Assert.notNull(mTenant, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT.getDesc()));
        long userCount = userMapper.selectUserCountByTenantId(tenantId);
        Assert.isFalse(userCount >= mTenant.getAccountCount(),
                () -> new BusinessException(TenantEnum.ErrorMsg.TENANT_USER_COUNT_LIMIT.getDesc()));
        //检查租户状态
        Assert.isTrue(TenantEnum.TenantStatus.TENANT_STATUS_1.getCode().equals(mTenant.getStatus()),
                () -> new BusinessException(TenantEnum.ErrorMsg.DISABLED_TENANT.getDesc()));
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

}
