package com.minimalist.basic.controller;

import com.minimalist.basic.entity.vo.notice.NoticeQueryVO;
import com.minimalist.basic.entity.vo.notice.NoticeVO;
import com.minimalist.basic.service.NoticeService;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.mybatis.bo.Pager;
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
@Tag(name = "通知、公告管理")
@RequestMapping("/basic/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/addNotice")
    @PreAuthorize("hasAuthority('basic:notice:add')")
    @Operation(summary = "添加公告")
    public ResponseEntity<Void> addNotice(@RequestBody @Validated(Add.class) NoticeVO noticeVO) {
        noticeService.addNotice(noticeVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteNoticeByNoticeId")
    @PreAuthorize("hasAuthority('basic:notice:delete')")
    @Operation(summary = "删除公告 -> 根据公告ID删除")
    public ResponseEntity<Void> deleteNoticeByNoticeId(@RequestParam("noticeId")
                                                   @NotNull(message = "公告ID不能为空")
                                                   @Parameter(name = "noticeId", required = true, description = "公告ID") Long noticeId) {
        noticeService.deleteNoticeByNoticeId(noticeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateNoticeByNoticeId")
    @PreAuthorize("hasAuthority('basic:notice:update')")
    @Operation(summary = "修改公告 -> 根据公告ID修改")
    public ResponseEntity<Void> updateNoticeByNoticeId(@RequestBody @Validated(Update.class) NoticeVO noticeVO) {
        noticeService.updateNoticeByNoticeId(noticeVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageNoticeList")
    @PreAuthorize("hasAuthority('basic:notice:get')")
    @Operation(summary = "查询公告列表(分页) -> 公告管理使用")
    public ResponseEntity<PageResp<NoticeVO>> getPageNoticeList(NoticeQueryVO queryVO) {
        return ResponseEntity.ok(noticeService.getPageNoticeList(queryVO));
    }

    @GetMapping("/getNoticeByNoticeId/{noticeId}")
    @PreAuthorize("hasAuthority('basic:notice:get')")
    @Operation(summary = "根据公告ID查询公告")
    public ResponseEntity<NoticeVO> getNoticeByNoticeId(@PathVariable(value = "noticeId")
                                                  @NotNull(message = "公告ID不能为空")
                                                  @Parameter(name = "noticeId", description = "公告ID", required = true) Long noticeId) {
        return ResponseEntity.ok(noticeService.getNoticeByNoticeId(noticeId));
    }

    @GetMapping("/getPageHomeNoticeList")
    @PreAuthorize("hasAuthority('basic:notice:get')")
    @Operation(summary = "查询公告列表(分页) -> 首页使用")
    public ResponseEntity<PageResp<NoticeVO>> getPageHomeNoticeList(Pager pager) {
        return ResponseEntity.ok(noticeService.getPageHomeNoticeList(pager));
    }

}
