package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minimalist.basic.entity.po.MUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 用户与角色关联表 1用户-N角色 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
public interface MUserRoleMapper extends BaseMapper<MUserRole> {

    /**
     * 根据userId查询用户与角色关联关系
     * @param userId 用户ID
     * @return 用户与角色关联关系
     */
    default List<MUserRole> selectUserRoleRelation(Long userId) {
        LambdaQueryWrapper<MUserRole> urQuery = new LambdaQueryWrapper<>();
        urQuery.select(MUserRole::getUserId, MUserRole::getRoleId)
                .eq(MUserRole::getUserId, userId);
        return selectList(urQuery);
    }

}
