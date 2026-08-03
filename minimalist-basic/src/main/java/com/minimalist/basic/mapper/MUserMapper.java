package com.minimalist.basic.mapper;

import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.po.table.MDeptTableDef;
import com.minimalist.basic.entity.po.table.MUserDeptTableDef;
import com.minimalist.basic.entity.po.table.MUserTableDef;
import com.minimalist.basic.entity.vo.user.UserQueryVO;
import com.minimalist.basic.entity.vo.user.UserVO;
import com.minimalist.basic.utils.CommonConstant;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MUser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 用户表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MUserMapper extends BaseMapper<MUser> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户PO
     */
    default MUser selectUserByUsername(String username) {
        return selectOneByQuery(QueryWrapper.create().eq(MUser::getUsername, username));
    }

    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户PO
     */
    default MUser selectUserByUserId(Long userId) {
        return selectOneByQuery(QueryWrapper.create().eq(MUser::getUserId, userId));
    }

    /**
     * 根据租户ID查询用户 -> 字典查询（区分租户和管理员）
     * @return 用户列表
     */
    default List<MUser> selectUserDict() {
        return selectListByQuery(QueryWrapper.create().eq(MUser::getStatus, StatusEnum.STATUS_1.getCode()));
    }

    /**
     * 查询当前数据源下的用户数量
     * @return 用户数量
     */
    default long selectUserCount() {
        return selectCountByQuery(QueryWrapper.create());
    }

    /**
     * 根据用户ID删除用户
     * @param userId 用户ID
     */
    default void deleteUserByUserId(Long userId) {
        deleteByQuery(QueryWrapper.create().eq(MUser::getUserId, userId));
    }

    /**
     * 根据用户ID修改用户
     * @param user 用户数据
     */
    default void updateUserByUserId(MUser user) {
        updateByQuery(user, QueryWrapper.create().eq(MUser::getUserId, user.getUserId()));
    }

    /**
     * 查询用户列表(分页)
     * @param query 查询条件
     * @return 用户分页数据
     */
    default Page<UserVO> selectPageUserList(UserQueryVO query) {
        /* select u.* FROM m_user u
         * inner join m_user_dept ud on u.user_id = ud.user_id
         * inner join m_dept d on d.dept_id = ud.dept_id
         * WHERE (
         *  d.dept_id = 1677964029214371840 or d.dept_id in (select t.dept_id from m_dept t where find_in_set(1677964029214371840, ancestors))
         * )
         * group by u.user_id;
         */
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(MUserTableDef.MUSER.ALL_COLUMNS)
                .from(MUserTableDef.MUSER)
                .leftJoin(MUserDeptTableDef.MUSER_DEPT).on(MUserDeptTableDef.MUSER_DEPT.USER_ID.eq(MUserTableDef.MUSER.USER_ID))
                .leftJoin(MDeptTableDef.MDEPT).on(MDeptTableDef.MDEPT.DEPT_ID.eq(MUserDeptTableDef.MUSER_DEPT.DEPT_ID))
                .where(MUserTableDef.MUSER.STATUS.eq(query.getStatus()))
                .and(MUserTableDef.MUSER.PHONE.like(query.getPhone()))
                .and(MUserTableDef.MUSER.USER_REAL_NAME.like(query.getUserRealName()));
        //deptId=0表示全部，需要忽略
        if (ObjectUtil.isNotNull(query.getDeptId()) && CommonConstant.ZERO != query.getDeptId()) {
            queryWrapper.and(
                    MDeptTableDef.MDEPT.DEPT_ID.eq(query.getDeptId())
                            .or(MDeptTableDef.MDEPT.DEPT_ID.in(
                                    QueryWrapper.create()
                                            .select(MDeptTableDef.MDEPT.DEPT_ID)
                                            .from(MDeptTableDef.MDEPT)
                                            .where("FIND_IN_SET(" + query.getDeptId() + ", ancestors)")
                            ))
            );
        }
        queryWrapper.groupBy(MUserTableDef.MUSER.USER_ID);
        return paginateAs(query.getPageNum(), query.getPageSize(), queryWrapper, UserVO.class);
    }

}
