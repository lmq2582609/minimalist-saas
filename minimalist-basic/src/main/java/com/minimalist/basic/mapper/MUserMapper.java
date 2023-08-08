package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.po.MUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalist.basic.entity.vo.user.UserQueryVO;
import com.minimalist.common.mybatis.QueryCondition;
import com.minimalist.common.security.user.UserEnum;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
public interface MUserMapper extends BaseMapper<MUser> {

    /**
     * 根据用户名查询用户数量
     * @param username 用户名
     * @return 用户PO
     */
    default long selectUserCountByUsername(String username) {
        return selectCount(new LambdaQueryWrapper<MUser>().eq(MUser::getUsername, username));
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户PO
     */
    default MUser selectUserByUsername(String username) {
        return selectOne(new LambdaQueryWrapper<MUser>().eq(MUser::getUsername, username));
    }

    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户PO
     */
    default MUser selectUserByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapper<MUser>().eq(MUser::getUserId, userId));
    }

    /**
     * 根据用户ID查询用户
     * @param userIdList 用户ID列表
     * @return 用户PO
     */
    default List<MUser> selectUserByUserIds(List<Long> userIdList) {
        return selectList(new LambdaQueryWrapper<MUser>().in(MUser::getUserId, userIdList));
    }

    /**
     * 根据租户ID查询用户 -> 字典查询（区分租户和管理员）
     * @return 用户列表
     */
    default List<MUser> selectUserDict() {
        LambdaQueryWrapper<MUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(MUser::getUserId, MUser::getNickname);
        queryWrapper.eq(MUser::getStatus, UserEnum.UserStatus.USER_STATUS_1.getCode());
        return selectList(queryWrapper);
    }

    /**
     * 根据租户ID查询该租户下的用户数量
     * @param tenantId 租户ID
     * @return 该租户下的用户数量
     */
    default long selectUserCountByTenantId(Long tenantId) {
        return selectCount(new LambdaQueryWrapper<MUser>().eq(MUser::getTenantId, tenantId));
    }

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户实体
     */
    default MUser selectUserByPhone(String phone) {
        return selectOne(new LambdaQueryWrapper<MUser>().eq(MUser::getPhone, phone));
    }

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户实体
     */
    default MUser selectUserByEmail(String email) {
        return selectOne(new LambdaQueryWrapper<MUser>().eq(MUser::getEmail, email));
    }

    /**
     * 根据用户ID删除用户
     * @param userId 用户ID
     * @return 受影响行数
     */
    default int deleteUserByUserId(Long userId) {
        return delete(new LambdaQueryWrapper<MUser>().eq(MUser::getUserId, userId));
    }

    /**
     * 根据用户ID修改用户
     * @param user 用户数据
     * @return 受影响行数
     */
    default int updateUserByUserId(MUser user) {
        return update(user, new LambdaUpdateWrapper<MUser>().eq(MUser::getUserId, user.getUserId()));
    }

    /**
     * 查询用户列表(分页)
     * @param queryVO 查询条件
     * @return 用户分页数据
     */
    default Page<MUser> selectPageUserList(UserQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MUser>()
                        .likeNotNull(MUser::getUserRealName, queryVO.getUserRealName())
                        .likeNotNull(MUser::getPhone, queryVO.getPhone())
                        .eqNotNull(MUser::getStatus, queryVO.getStatus()));
    }

}
