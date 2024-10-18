package com.minimalist.basic.mapper;

import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.po.table.MDeptTableDef;
import com.minimalist.basic.entity.po.table.MUserDeptTableDef;
import com.minimalist.basic.entity.po.table.MUserTableDef;
import com.minimalist.basic.entity.vo.user.UserQueryVO;
import com.minimalist.basic.utils.CommonConstant;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MUser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;
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
     * 根据用户ID查询用户
     * @param userIdList 用户ID列表
     * @return 用户PO
     */
    default List<MUser> selectUserByUserIds(List<Long> userIdList) {
        return selectListByQuery(QueryWrapper.create().in(MUser::getUserId, userIdList));
    }

    /**
     * 根据租户ID查询用户 -> 字典查询（区分租户和管理员）
     * @return 用户列表
     */
    default List<MUser> selectUserDict() {
        return selectListByQuery(QueryWrapper.create().eq(MUser::getStatus, StatusEnum.STATUS_1.getCode()));
    }

    /**
     * 根据租户ID查询该租户下的用户数量
     * @param tenantId 租户ID
     * @return 该租户下的用户数量
     */
    default long selectUserCountByTenantId(Long tenantId) {
        return selectCountByQuery(QueryWrapper.create().eq(MUser::getTenantId, tenantId));
    }

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户实体
     */
    default MUser selectUserByPhone(String phone) {
        return selectOneByQuery(QueryWrapper.create().eq(MUser::getPhone, phone));
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
     * 根据部门ID列表，查询用户数
     * @param deptIds 部门ID
     * @return 用户数
     */
    default long selectUserCountByDeptIds(List<Long> deptIds) {
        long result = 0;
        for (Long deptId : deptIds) {
            result += selectCountByQuery(QueryWrapper.create().where("FIND_IN_SET("  + deptId +", ancestors)"));
        }
        return result;
    }

    /**
     * 查询用户列表(分页)
     * @param query 查询条件
     * @return 用户分页数据
     */
    default Page<MUser> selectPageUserList(@Param("query") UserQueryVO query) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(MUserTableDef.MUSER.ALL_COLUMNS)
                .from(MUserTableDef.MUSER)
                .eq(MUser::getStatus, query.getStatus())
                .like(MUser::getPhone, query.getPhone())
                .like(MUser::getUserRealName, query.getUserRealName());
        if (ObjectUtil.isNotNull(query.getDeptId()) && query.getDeptId().intValue() != CommonConstant.ZERO) {
            queryWrapper.and(
                    QueryMethods.exists(
                        QueryMethods.selectOne()
                                .from(MUserDeptTableDef.MUSER_DEPT)
                                .leftJoin(MDeptTableDef.MDEPT).on(MUserDeptTableDef.MUSER_DEPT.DEPT_ID.eq(MDeptTableDef.MDEPT.DEPT_ID))
                                .where(MUserTableDef.MUSER.USER_ID.eq(MUserDeptTableDef.MUSER_DEPT.USER_ID))
                                .and(
                                        MDeptTableDef.MDEPT.DEPT_ID.eq(query.getDeptId())
                                                .or(QueryMethods.findInSet(QueryMethods.column(query.getDeptId().toString()), MDeptTableDef.MDEPT.ANCESTORS).ge(0))
                                )
                    )
            );
        }
        return paginate(query.getPageNum(), query.getPageSize(), queryWrapper);
    }

}
