package com.minimalist.basic.service;

import com.minimalist.basic.entity.vo.post.PostQueryVO;
import com.minimalist.basic.entity.vo.post.PostVO;
import com.minimalist.common.mybatis.bo.PageResp;

import java.util.List;

public interface PostService {

    /**
     * 添加岗位
     * @param postVO 岗位数据
     */
    void addPost(PostVO postVO);

    /**
     * 删除岗位 -> 根据岗位ID删除
     * @param postId 岗位ID
     */
    void deletePostByPostId(Long postId);

    /**
     * 修改岗位 -> 根据岗位ID修改
     * @param postVO 岗位数据
     */
    void updatePostByPostId(PostVO postVO);

    /**
     * 查询岗位列表(分页)
     * @param queryVO 查询参数
     * @return 岗位列表
     */
    PageResp<PostVO> getPagePostList(PostQueryVO queryVO);

    /**
     * 根据岗位ID查询岗位
     * @param postId 岗位ID
     * @return 岗位数据
     */
    PostVO getPostByPostId(Long postId);

    /**
     * 根据用户ID查询用户岗位
     * @param userId 用户ID
     * @return 岗位列表
     */
    List<PostVO> getPostByUserId(Long userId);

}
