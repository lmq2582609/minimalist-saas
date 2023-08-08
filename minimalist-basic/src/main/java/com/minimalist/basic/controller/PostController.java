package com.minimalist.basic.controller;

import com.minimalist.basic.entity.vo.post.PostQueryVO;
import com.minimalist.basic.entity.vo.post.PostVO;
import com.minimalist.basic.service.PostService;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.valid.Add;
import com.minimalist.common.valid.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/basic/post")
@Tag(name = "岗位管理")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/addPost")
    @PreAuthorize("hasAuthority('basic:post:add')")
    @Operation(summary = "添加岗位")
    public ResponseEntity<Void> addPost(@RequestBody @Validated(Add.class) PostVO postVO) {
        postService.addPost(postVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePostByPostId")
    @PreAuthorize("hasAuthority('basic:post:delete')")
    @Operation(summary = "删除岗位 -> 根据岗位ID删除")
    public ResponseEntity<Void> deletePostByPostId(@RequestParam("postId")
                                                   @NotNull(message = "岗位ID不能为空")
                                                   @Parameter(name = "postId", required = true, description = "岗位ID") Long postId) {
        postService.deletePostByPostId(postId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updatePostByPostId")
    @PreAuthorize("hasAuthority('basic:post:update')")
    @Operation(summary = "修改岗位 -> 根据岗位ID修改")
    public ResponseEntity<Void> updatePostByPostId(@RequestBody @Validated(Update.class) PostVO postVO) {
        postService.updatePostByPostId(postVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPagePostList")
    @PreAuthorize("hasAuthority('basic:post:get')")
    @Operation(summary = "查询岗位列表(分页)")
    public ResponseEntity<PageResp<PostVO>> getPagePostList(PostQueryVO queryVO) {
        return ResponseEntity.ok(postService.getPagePostList(queryVO));
    }

    @GetMapping("/getPostByPostId/{postId}")
    @PreAuthorize("hasAuthority('basic:post:get')")
    @Operation(summary = "根据岗位ID查询岗位")
    public ResponseEntity<PostVO> getPostByPostId(@PathVariable(value = "postId")
                                                  @NotNull(message = "岗位ID不能为空")
                                                  @Parameter(name = "postId", description = "岗位ID", required = true) Long postId) {
        return ResponseEntity.ok(postService.getPostByPostId(postId));
    }

}
