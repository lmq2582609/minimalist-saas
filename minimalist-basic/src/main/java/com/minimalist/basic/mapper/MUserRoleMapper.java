package com.minimalist.basic.mapper;

import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MUserRole;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 用户与角色关联表 1用户-N角色 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MUserRoleMapper extends BaseMapper<MUserRole> {

    /**
     * 根据userId查询用户与角色关联关系
     * @param userId 用户ID
     * @return 用户与角色关联关系
     */
    default List<MUserRole> selectUserRoleRelation(Long userId) {
        return selectListByQuery(QueryWrapper.create().eq(MUserRole::getUserId, userId));
    }

}
