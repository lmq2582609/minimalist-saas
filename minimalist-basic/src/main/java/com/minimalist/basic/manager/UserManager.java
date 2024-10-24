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
import com.minimalist.basic.mapper.MUserDeptMapper;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.entity.enums.UserEnum;
import com.minimalist.basic.mapper.MUserPostMapper;
import com.minimalist.basic.mapper.MUserRoleMapper;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
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
    private MUserRoleMapper userRoleMapper;

    @Autowired
    private MUserPostMapper userPostMapper;

    @Autowired
    private MUserDeptMapper userDeptMapper;

    /**
     * 用户密码加密
     * @param password 密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    public String passwordEncrypt(String password, String salt) {
        String p1 = SecureUtil.md5(password + salt);
        return SecureUtil.md5(p1);
    }

    /**
     * 用户名唯一性校验
     * 整个系统中不允许重复的用户名
     * @param username 用户名
     * @param userId 用户ID，可以被忽略的用户ID
     */
    public void checkUsernameUniqueness(String username, Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(MUser::getUsername, username);
        if (ObjectUtil.isNotNull(userId)) {
            queryWrapper.ne(MUser::getUserId, userId);
        }
        MUser user = userMapper.selectOneByQuery(queryWrapper);
        Assert.isNull(user, () -> new BusinessException(UserEnum.ErrorMsg.EXISTS_ACCOUNT.getDesc()));
    }

    /**
     * 用户邮箱唯一性校验
     * 邮箱不允许重复
     * @param email 用户邮箱
     * @param userId 用户ID，可以被忽略的用户ID
     */
    public void checkUserEmailUniqueness(String email, Long userId) {
        if (StrUtil.isBlank(email)) { return; }
        QueryWrapper queryWrapper = QueryWrapper.create().eq(MUser::getEmail, email);
        if (ObjectUtil.isNotNull(userId)) {
            queryWrapper.ne(MUser::getUserId, userId);
        }
        MUser user = userMapper.selectOneByQuery(queryWrapper);
        Assert.isNull(user, () -> new BusinessException(UserEnum.ErrorMsg.EMAIL_ACCOUNT.getDesc()));
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
            userRoleMapper.insertBatch(userRoleList);
        }
        //用户与岗位关联关系
        if (CollectionUtil.isNotEmpty(postIds)) {
            List<MUserPost> userPostList = postIds.stream().map(p -> {
                MUserPost userPost = new MUserPost();
                userPost.setUserId(userId);
                userPost.setPostId(p);
                return userPost;
            }).toList();
            userPostMapper.insertBatch(userPostList);
        }
        //用户与部门关联关系
        if (CollectionUtil.isNotEmpty(deptIds)) {
            List<MUserDept> userDeptList = deptIds.stream().map(d -> {
                MUserDept userDept = new MUserDept();
                userDept.setUserId(userId);
                userDept.setDeptId(d);
                return userDept;
            }).toList();
            userDeptMapper.insertBatch(userDeptList);
        }
    }

    /**
     * 删除用户角色、岗位关联信息
     * @param userId 用户ID
     */
    public void deleteUserRelation(Long userId) {
        //删除用户与角色关联关系 -> 真实删除
        LogicDeleteManager.execWithoutLogicDelete(()->
                userRoleMapper.deleteByQuery(QueryWrapper.create().eq(MUserRole::getUserId, userId))
        );
        //删除用户与岗位关联关系 -> 真实删除
        LogicDeleteManager.execWithoutLogicDelete(()->
                userPostMapper.deleteByQuery(QueryWrapper.create().eq(MUserPost::getUserId, userId))
        );
        //删除用户与部门关联关系 -> 真实删除
        LogicDeleteManager.execWithoutLogicDelete(()->
                userDeptMapper.deleteByQuery(QueryWrapper.create().eq(MUserDept::getUserId, userId))
        );
    }

}
