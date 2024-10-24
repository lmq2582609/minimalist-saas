package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.minimalist.basic.entity.enums.PostEnum;
import com.minimalist.basic.entity.po.MPost;
import com.minimalist.basic.entity.po.MUserPost;
import com.minimalist.basic.entity.vo.post.PostQueryVO;
import com.minimalist.basic.entity.vo.post.PostVO;
import com.minimalist.basic.mapper.MPostMapper;
import com.minimalist.basic.mapper.MUserPostMapper;
import com.minimalist.basic.service.PostService;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.utils.UnqIdUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private MPostMapper postMapper;

    @Autowired
    private MUserPostMapper userPostMapper;

    /**
     * 添加岗位
     * @param postVO 岗位数据
     */
    @Override
    public void addPost(PostVO postVO) {
        MPost mPost = postMapper.selectPostByPostCode(postVO.getPostCode());
        Assert.isNull(mPost, () -> new BusinessException(PostEnum.ErrorMsg.EXISTS_POST.getDesc()));
        long postId = UnqIdUtil.uniqueId();
        mPost = BeanUtil.copyProperties(postVO, MPost.class);
        mPost.setPostId(postId);
        postMapper.insert(mPost);
    }

    /**
     * 删除岗位 -> 根据岗位ID删除
     * @param postId 岗位ID
     */
    @Override
    public void deletePostByPostId(Long postId) {
        //删除岗位
        postMapper.deletePostByPostId(postId);
        //删除岗位与用户关联关系
        LogicDeleteManager.execWithoutLogicDelete(()->
                userPostMapper.deleteByQuery(QueryWrapper.create().eq(MUserPost::getPostId, postId))
        );
    }

    /**
     * 修改岗位 -> 根据岗位ID修改
     * @param postVO 岗位数据
     */
    @Override
    public void updatePostByPostId(PostVO postVO) {
        MPost newPost = BeanUtil.copyProperties(postVO, MPost.class);
        postMapper.updatePostByPostId(newPost);
    }

    /**
     * 查询岗位列表(分页)
     * @param queryVO 查询参数
     * @return 岗位列表
     */
    @Override
    public PageResp<PostVO> getPagePostList(PostQueryVO queryVO) {
        Page<PostVO> postVOPage = postMapper.selectPagePostList(queryVO);
        return new PageResp<>(postVOPage.getRecords(), postVOPage.getTotalRow());
    }

    /**
     * 根据岗位ID查询岗位
     * @param postId 岗位ID
     * @return 岗位数据
     */
    @Override
    public PostVO getPostByPostId(Long postId) {
        return BeanUtil.copyProperties(postMapper.selectPostByPostId(postId), PostVO.class);
    }

    /**
     * 根据用户ID查询用户岗位
     * @param userId 用户ID
     * @return 岗位列表
     */
    @Override
    public List<PostVO> getPostByUserId(Long userId) {
        //查询用户与岗位关联数据
        List<MUserPost> userPostList = userPostMapper.selectUserPostRelation(userId);
        if (CollectionUtil.isEmpty(userPostList)) {
            return CollectionUtil.list(false);
        }
        List<Long> postIds = userPostList.stream().map(MUserPost::getPostId).toList();
        return postMapper.selectPostByPostIds(postIds);
    }

}
