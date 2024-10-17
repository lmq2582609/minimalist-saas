package com.minimalist.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.minimalist.basic.entity.vo.file.*;
import com.minimalist.basic.service.FileService;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Tag(name = "文件管理")
@RequestMapping("/basic/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/uploadFile")
    @SaCheckPermission("basic:file:upload")
    @Operation(summary = "单文件上传")
    public ResponseEntity<FileUploadRespVO> uploadFile(@Validated FileUploadVO fileUploadVO) {
        return ResponseEntity.ok(fileService.uploadFile(fileUploadVO));
    }

    @PostMapping("/uploadFileBatch")
    @SaCheckPermission("basic:file:upload")
    @Operation(summary = "批量文件上传")
    public ResponseEntity<List<FileUploadRespVO>> uploadFileBatch(@Validated FileUploadBatchVO uploadBatchVO) {
        return ResponseEntity.ok(fileService.uploadFileBatch(uploadBatchVO));
    }

    @DeleteMapping("/deleteFile")
    @SaCheckPermission("basic:file:delete")
    @Operation(summary = "单文件删除")
    public ResponseEntity<Void> deleteFile(@RequestParam("fileId")
                                           @NotNull(message = "文件ID不能为空")
                                           @Parameter(name = "fileId", required = true, description = "fileId") Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageFileList")
    @SaCheckPermission("basic:file:get")
    @Operation(summary = "查询文件列表(分页)")
    public ResponseEntity<PageResp<FileVO>> getPageFileList(FileQueryVO queryVO) {
        return ResponseEntity.ok(fileService.getPageFileList(queryVO));
    }

}
