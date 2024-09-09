package com.minimalist.basic.manager;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.entity.vo.user.UserVO;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.security.user.UserEnum;
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

    /**
     * 用户密码加密
     * @param password 密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    public String passwordEncrypt(String password, String salt){
        return SecureUtil.md5(password + salt);
    }

    /**
     * 用户名唯一性校验
     * @param userVO 用户信息
     */
    public void checkUsernameUniqueness(UserVO userVO) {
        MUser mUser = userMapper.selectUserByUsername(userVO.getUsername());
        //校验用户名唯一
        if (ObjectUtil.isNotNull(mUser)) {
            Assert.isTrue(mUser.getUserId().equals(userVO.getUserId()), () -> new BusinessException(UserEnum.ErrorMsg.EXISTS_ACCOUNT.getDesc()));
        }
    }

    /**
     * 用户手机唯一性校验
     * @param userVO 用户信息
     */
    public void checkUserPhoneUniqueness(UserVO userVO) {
        MUser mUser = userMapper.selectUserByPhone(userVO.getPhone());
        //校验用户名唯一
        if (ObjectUtil.isNotNull(mUser)) {
            Assert.isTrue(mUser.getUserId().equals(userVO.getUserId()), () -> new BusinessException(UserEnum.ErrorMsg.PHONE_ACCOUNT.getDesc()));
        }
    }

    /**
     * 用户邮箱唯一性校验
     * @param userVO 用户信息
     */
    public void checkUserEmailUniqueness(UserVO userVO) {
        MUser mUser = userMapper.selectUserByPhone(userVO.getPhone());
        //校验用户名唯一
        if (ObjectUtil.isNotNull(mUser)) {
            Assert.isTrue(mUser.getUserId().equals(userVO.getUserId()), () -> new BusinessException(UserEnum.ErrorMsg.EMAIL_ACCOUNT.getDesc()));
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
