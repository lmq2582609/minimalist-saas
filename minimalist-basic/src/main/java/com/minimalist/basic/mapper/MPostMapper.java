package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.vo.post.PostQueryVO;
import com.minimalist.basic.entity.vo.post.PostVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MPost;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 岗位表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MPostMapper extends BaseMapper<MPost> {

    /**
     * 根据岗位编码查询岗位
     * @param postCode 岗位编码
     * @return 岗位实体
     */
    default MPost selectPostByPostCode(String postCode) {
        return selectOneByQuery(QueryWrapper.create().eq(MPost::getPostCode, postCode));
    }

    /**
     * 根据岗位ID删除岗位
     * @param postId 岗位ID
     */
    default void deletePostByPostId(Long postId) {
        deleteByQuery(QueryWrapper.create().eq(MPost::getPostId, postId));
    }

    /**
     * 根据岗位ID查询岗位(单条)
     * @param postId 岗位ID
     * @return 岗位实体
     */
    default MPost selectPostByPostId(Long postId) {
        return selectOneByQuery(QueryWrapper.create().eq(MPost::getPostId, postId));
    }

    /**
     * 根据岗位ID查询岗位(批量)
     * @param postIds 岗位ID列表
     * @return 岗位实体列表
     */
    default List<PostVO> selectPostByPostIds(List<Long> postIds) {
        return selectListByQueryAs(QueryWrapper.create().in(MPost::getPostId, postIds), PostVO.class);
    }

    /**
     * 根据岗位ID修改岗位
     * @param mPost 岗位数据
     */
    default void updatePostByPostId(MPost mPost) {
        updateByQuery(mPost, QueryWrapper.create().eq(MPost::getPostId, mPost.getPostId()));
    }

    /**
     * 查询岗位列表(分页)
     * @param queryVO 查询条件
     * @return 岗位分页数据
     */
    default Page<PostVO> selectPagePostList(PostQueryVO queryVO) {
        return paginateAs(queryVO.getPageNum(), queryVO.getPageSize(),
                QueryWrapper.create()
                        .eq(MPost::getStatus, queryVO.getStatus())
                        .like(MPost::getPostName, queryVO.getPostName())
                        .like(MPost::getPostCode, queryVO.getPostCode())
                        .orderBy(MPost::getPostSort, true),
                PostVO.class
        );
    }

    /**
     * 根据租户ID查询岗位列表 -> 字典查询
     * @return 部门列表
     */
    default List<MPost> selectPostDict() {
        return selectListByQuery(QueryWrapper.create().eq(MPost::getStatus, StatusEnum.STATUS_1.getCode()));
    }
}
