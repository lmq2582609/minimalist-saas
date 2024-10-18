package com.minimalist.basic.mapper;

import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MRolePerm;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 角色与权限关联表 1角色-N权限 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MRolePermMapper extends BaseMapper<MRolePerm> {

    /**
     * 根据角色ID获取角色与权限关联关系
     * @param roleIds 角色ID集合(多条)
     * @return 角色与权限关联数据
     */
    default List<MRolePerm> selectRolePermByRoleIds(List<Long> roleIds) {
        return selectListByQuery(QueryWrapper.create().in(MRolePerm::getRoleId, roleIds));
    }

    /**
     * 根据角色ID获取角色与权限关联关系
     * @param roleId 角色ID(单条)
     * @return 角色与权限关联数据
     */
    default List<MRolePerm> selectRolePermByRoleId(Long roleId) {
        return selectListByQuery(QueryWrapper.create().eq(MRolePerm::getRoleId, roleId));
    }

}
