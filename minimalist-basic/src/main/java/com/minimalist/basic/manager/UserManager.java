package com.minimalist.basic.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.entity.po.MUserDept;
import com.minimalist.basic.entity.po.MUserPost;
import com.minimalist.basic.entity.po.MUserRole;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.basic.entity.enums.UserEnum;
import com.minimalist.common.module.entity.enums.ConfigEnum;
import com.minimalist.common.module.entity.po.MConfig;
import com.minimalist.common.module.mapper.MConfigMapper;
import com.minimalist.common.mybatis.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;

/**
 * 用户相关的辅助处理
 */
@Component
public class UserManager {

    @Autowired
    private MUserMapper userMapper;

    @Autowired
    private MConfigMapper configMapper;

    @Autowired
    private EntityService entityService;

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
     * 整个系统中不允许重复的用户名
     * @param username 用户名
     * @param userId 用户ID，可以被忽略的用户ID
     */
    public void checkUsernameUniqueness(String username, Long userId) {
        MUser mUser = userMapper.selectUserByUsername(username);
        if (ObjectUtil.isNull(mUser)) {
            return;
        }
        Assert.isTrue(ObjectUtil.isNull(mUser) || mUser.getUserId().equals(userId),
                () -> new BusinessException(UserEnum.ErrorMsg.EXISTS_ACCOUNT.getDesc()));
    }

    /**
     * 用户邮箱唯一性校验
     * 邮箱不允许重复
     * @param email 用户邮箱
     * @param userId 用户ID，可以被忽略的用户ID
     */
    public void checkUserEmailUniqueness(String email, Long userId) {
        if (StrUtil.isNotBlank(email)) {
            MUser mUser = userMapper.selectUserByPhone(email);
            Assert.isTrue(ObjectUtil.isNull(mUser) || mUser.getUserId().equals(userId),
                    () -> new BusinessException(UserEnum.ErrorMsg.EXISTS_ACCOUNT.getDesc()));
        }
    }

    /**
     * 新增用户角色、岗位、部门关联信息
     * @param roleIds 角色ID列表
     * @param postIds 岗位ID列表
     * @param deptIds 部门ID列表
     */
    public void insertUserRelation(Set<Long> roleIds, Set<Long> postIds, Set<Long> deptIds, Long userId) {
        //用户与角色关联关系
        if (CollectionUtil.isNotEmpty(roleIds)) {
            List<MUserRole> userRoleList = roleIds.stream().map(r -> {
                MUserRole userRole = new MUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(r);
                return userRole;
            }).toList();
            entityService.insertBatch(userRoleList);
        }
        //用户与岗位关联关系
        if (CollectionUtil.isNotEmpty(postIds)) {
            List<MUserPost> userPostList = postIds.stream().map(p -> {
                MUserPost userPost = new MUserPost();
                userPost.setUserId(userId);
                userPost.setPostId(p);
                return userPost;
            }).toList();
            entityService.insertBatch(userPostList);
        }
        //用户与部门关联关系
        if (CollectionUtil.isNotEmpty(deptIds)) {
            List<MUserDept> userDeptList = deptIds.stream().map(d -> {
                MUserDept userDept = new MUserDept();
                userDept.setUserId(userId);
                userDept.setDeptId(d);
                return userDept;
            }).toList();
            entityService.insertBatch(userDeptList);
        }
    }

    /**
     * 删除用户角色、岗位关联信息
     * @param userId 用户ID
     */
    public void deleteUserRelation(Long userId) {
        //删除用户与角色关联关系 -> 真实删除
        entityService.delete(MUserRole::getUserId, userId);
        //删除用户与岗位关联关系 -> 真实删除
        entityService.delete(MUserPost::getUserId, userId);
        //删除用户与部门关联关系 -> 真实删除
        entityService.delete(MUserDept::getUserId, userId);
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