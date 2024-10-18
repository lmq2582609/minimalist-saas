package com.minimalist.basic.mapper;

import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MUserDept;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 用户与岗位关联表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MUserDeptMapper extends BaseMapper<MUserDept> {

    /**
     * 根据用户ID查询用户与部门关联数据
     * @param userId 用户ID
     * @return 用户与部门关联数据
     */
    default List<MUserDept> selectUserDeptRelation(Long userId) {
        return selectListByQuery(QueryWrapper.create().eq(MUserDept::getUserId, userId));
    }

}
