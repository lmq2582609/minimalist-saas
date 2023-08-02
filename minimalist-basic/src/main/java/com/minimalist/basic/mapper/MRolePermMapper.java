package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minimalist.basic.entity.po.MRolePerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 角色与权限关联表 1角色-N权限 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
public interface MRolePermMapper extends BaseMapper<MRolePerm> {

    /**
     * 根据角色ID获取角色与权限关联关系
     * @param roleIds 角色ID集合(多条)
     * @return 角色与权限关联数据
     */
    default List<MRolePerm> selectRolePermByRoleIds(List<Long> roleIds) {
        return selectList(new LambdaQueryWrapper<MRolePerm>()
                .select(MRolePerm::getRoleId, MRolePerm::getPermId)
                .in(MRolePerm::getRoleId, roleIds));
    }

    /**
     * 根据角色ID获取角色与权限关联关系
     * @param roleId 角色ID(单条)
     * @return 角色与权限关联数据
     */
    default List<MRolePerm> selectRolePermByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapper<MRolePerm>()
                .select(MRolePerm::getRoleId, MRolePerm::getPermId)
                .eq(MRolePerm::getRoleId, roleId));
    }

}
