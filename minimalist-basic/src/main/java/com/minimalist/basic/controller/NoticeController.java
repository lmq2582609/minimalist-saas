package com.minimalist.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.minimalist.basic.entity.vo.notice.NoticeQueryVO;
import com.minimalist.basic.entity.vo.notice.NoticeVO;
import com.minimalist.basic.service.NoticeService;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.config.mybatis.bo.PageReq;
import com.minimalist.basic.utils.Add;
import com.minimalist.basic.utils.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import net.dreamlu.mica.xss.core.XssCleanIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@Tag(name = "通知、公告管理")
@RequestMapping("/basic/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @XssCleanIgnore
    @PostMapping("/addNotice")
    @SaCheckPermission("basic:notice:add")
    @Operation(summary = "添加公告")
    public ResponseEntity<Void> addNotice(@RequestBody @Validated(Add.class) NoticeVO noticeVO) {
        noticeService.addNotice(noticeVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteNoticeByNoticeId")
    @SaCheckPermission("basic:notice:delete")
    @Operation(summary = "删除公告 -> 根据公告ID删除")
    public ResponseEntity<Void> deleteNoticeByNoticeId(@RequestParam("noticeId")
                                                   @NotNull(message = "公告ID不能为空")
                                                   @Parameter(name = "noticeId", required = true, description = "公告ID") Long noticeId) {
        noticeService.deleteNoticeByNoticeId(noticeId);
        return ResponseEntity.ok().build();
    }

    @XssCleanIgnore
    @PutMapping("/updateNoticeByNoticeId")
    @SaCheckPermission("basic:notice:update")
    @Operation(summary = "修改公告 -> 根据公告ID修改")
    public ResponseEntity<Void> updateNoticeByNoticeId(@RequestBody @Validated(Update.class) NoticeVO noticeVO) {
        noticeService.updateNoticeByNoticeId(noticeVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageNoticeList")
    @SaCheckPermission("basic:notice:get")
    @Operation(summary = "查询公告列表(分页) -> 公告管理使用")
    public ResponseEntity<PageResp<NoticeVO>> getPageNoticeList(NoticeQueryVO queryVO) {
        return ResponseEntity.ok(noticeService.getPageNoticeList(queryVO));
    }

    @GetMapping("/getNoticeByNoticeId/{noticeId}")
    @Operation(summary = "根据公告ID查询公告")
    public ResponseEntity<NoticeVO> getNoticeByNoticeId(@PathVariable(value = "noticeId")
                                                  @NotNull(message = "公告ID不能为空")
                                                  @Parameter(name = "noticeId", description = "公告ID", required = true) Long noticeId) {
        return ResponseEntity.ok(noticeService.getNoticeByNoticeId(noticeId));
    }

    @GetMapping("/getPageHomeNoticeList")
    @Operation(summary = "查询公告列表(分页) -> 首页使用")
    public ResponseEntity<PageResp<NoticeVO>> getPageHomeNoticeList(PageReq pageReq) {
        return ResponseEntity.ok(noticeService.getPageHomeNoticeList(pageReq));
    }

}
