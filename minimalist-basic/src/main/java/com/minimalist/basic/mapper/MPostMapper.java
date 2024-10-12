package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.po.MPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalist.basic.entity.vo.post.PostQueryVO;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.mybatis.QueryCondition;
import java.util.List;

/**
 * <p>
 * 岗位表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
public interface MPostMapper extends BaseMapper<MPost> {

    /**
     * 根据岗位编码查询岗位
     * @param postCode 岗位编码
     * @return 岗位实体
     */
    default MPost selectPostByPostCode(String postCode) {
        return selectOne(new LambdaQueryWrapper<MPost>().eq(MPost::getPostCode, postCode));
    }

    /**
     * 根据岗位ID删除岗位
     * @param postId 岗位ID
     * @return 受影响行数
     */
    default int deletePostByPostId(Long postId) {
        return delete(new LambdaQueryWrapper<MPost>().eq(MPost::getPostId, postId));
    }

    /**
     * 根据岗位ID查询岗位(单条)
     * @param postId 岗位ID
     * @return 岗位实体
     */
    default MPost selectPostByPostId(Long postId) {
        return selectOne(new LambdaQueryWrapper<MPost>().eq(MPost::getPostId, postId));
    }

    /**
     * 根据岗位ID查询岗位(批量)
     * @param postIds 岗位ID列表
     * @return 岗位实体列表
     */
    default List<MPost> selectPostByPostIds(List<Long> postIds) {
        return selectList(new LambdaQueryWrapper<MPost>().in(MPost::getPostId, postIds));
    }

    /**
     * 根据岗位ID修改岗位
     * @param mPost 岗位数据
     * @return 受影响行数
     */
    default int updatePostByPostId(MPost mPost) {
        return update(mPost, new LambdaUpdateWrapper<MPost>().eq(MPost::getPostId, mPost.getPostId()));
    }

    /**
     * 查询岗位列表(分页)
     * @param queryVO 查询条件
     * @return 岗位分页数据
     */
    default Page<MPost> selectPagePostList(PostQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MPost>()
                        .likeNotNull(MPost::getPostName, queryVO.getPostName())
                        .likeNotNull(MPost::getPostCode, queryVO.getPostCode())
                        .eqNotNull(MPost::getStatus, queryVO.getStatus())
                        .orderByAscc(MPost::getPostSort));
    }

    /**
     * 根据租户ID查询岗位列表 -> 字典查询
     * @return 部门列表
     */
    default List<MPost> selectPostDict() {
        LambdaQueryWrapper<MPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(MPost::getPostId, MPost::getPostName);
        queryWrapper.eq(MPost::getStatus, StatusEnum.STATUS_1.getCode());
        return selectList(queryWrapper);
    }

}
