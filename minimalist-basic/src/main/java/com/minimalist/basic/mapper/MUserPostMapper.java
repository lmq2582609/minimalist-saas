package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minimalist.basic.entity.po.MUserPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

/**
 * <p>
 * 用户与岗位关联表 1用户-N岗位 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
public interface MUserPostMapper extends BaseMapper<MUserPost> {

    /**
     * 根据用户ID查询用户与岗位关联数据
     * @param userId 用户ID
     * @return 用户与岗位关联数据
     */
    default List<MUserPost> selectUserPostRelation(Long userId) {
        return selectList(new LambdaQueryWrapper<MUserPost>().eq(MUserPost::getUserId, userId));
    }

}
