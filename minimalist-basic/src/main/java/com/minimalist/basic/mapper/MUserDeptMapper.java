package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalist.basic.entity.po.MUserDept;

import java.util.List;

/**
 * <p>
 * 用户与岗位关联表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-07-04
 */
public interface MUserDeptMapper extends BaseMapper<MUserDept> {

    /**
     * 根据用户ID查询用户与部门关联数据
     * @param userId 用户ID
     * @return 用户与部门关联数据
     */
    default List<MUserDept> selectUserDeptRelation(Long userId) {
        return selectList(new LambdaQueryWrapper<MUserDept>().eq(MUserDept::getUserId, userId));
    }

}
