package com.minimalist.basic.manager;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.basic.entity.enums.UserEnum;
import com.minimalist.common.module.entity.enums.ConfigEnum;
import com.minimalist.common.module.entity.po.MConfig;
import com.minimalist.common.module.mapper.MConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 用户相关的辅助处理
 */
@Component
public class UserManager {

    @Autowired
    private MUserMapper userMapper;

    @Autowired
    private MConfigMapper configMapper;

    /**
     * 用户密码加密
     * @param password 密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    public String passwordEncrypt(String password, String salt) {
        String p1 = SecureUtil.md5(password + salt);
        MConfig config = configMapper.selectConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_PASSWORD_SALT, ConfigEnum.Status.STATUS_1.getCode());
        return SecureUtil.md5(p1 + config.getConfigValue());
    }

    /**
     * 用户名唯一性校验
     * 不同租户下，允许重复的用户名
     * @param username 用户名
     * @param tenantId 租户ID
     */
    public void checkUsernameUniqueness(String username, long tenantId) {
        MUser mUser = userMapper.selectUserByUsername(username);
        if (ObjectUtil.isNotNull(mUser)) {
            //如果有重复的用户名，租户ID不一致允许重复的用户名
            Assert.isFalse(mUser.getTenantId().equals(tenantId), () -> new BusinessException(UserEnum.ErrorMsg.EXISTS_ACCOUNT.getDesc()));
        }
    }

    /**
     * 用户邮箱唯一性校验
     * 邮箱不允许重复
     * @param email 用户邮箱
     */
    public void checkUserEmailUniqueness(String email) {
        if (StrUtil.isNotBlank(email)) {
            MUser mUser = userMapper.selectUserByPhone(email);
            Assert.isNull(mUser, () -> new BusinessException(UserEnum.ErrorMsg.EMAIL_ACCOUNT.getDesc()));
        }
    }

    /**
     * 根据用户ID获取权限编码集合
     * @param userId 用户ID
     * @return 权限编码集合
     */
    public List<String> getUserPermissions(long userId) {
        return null;
    }

    /**
     * 根据用户ID获取角色编码集合
     * @param userId 用户ID
     * @return 角色编码集合
     */
    public List<String> getUserRoles(long userId) {
        return null;
    }

}
