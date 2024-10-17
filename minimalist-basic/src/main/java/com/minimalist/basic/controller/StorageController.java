package com.minimalist.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.entity.vo.storage.StorageQueryVO;
import com.minimalist.basic.entity.vo.storage.StorageVO;
import com.minimalist.basic.service.StorageService;
import com.minimalist.basic.utils.Add;
import com.minimalist.basic.utils.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/basic/storage")
@Tag(name = "存储管理")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/addStorage")
    @SaCheckPermission("basic:storage:add")
    @Operation(summary = "添加存储")
    public ResponseEntity<Void> addStorage(@RequestBody @Validated(Add.class) StorageVO storageVO) {
        storageService.addStorage(storageVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteStorageByStorageId")
    @SaCheckPermission("basic:storage:delete")
    @Operation(summary = "删除存储 -> 根据存储ID删除")
    public ResponseEntity<Void> deleteStorageByStorageId(@RequestParam("storageId")
                                                   @NotNull(message = "存储ID不能为空")
                                                   @Parameter(name = "storageId", required = true, description = "存储ID") Long storageId) {
        storageService.deleteStorageByStorageId(storageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateStorageByStorageId")
    @SaCheckPermission("basic:storage:update")
    @Operation(summary = "修改存储 -> 根据存储ID修改")
    public ResponseEntity<Void> updateStorageByStorageId(@RequestBody @Validated(Update.class) StorageVO storageVO) {
        storageService.updateStorageByStorageId(storageVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageStorageList")
    @SaCheckPermission("basic:storage:get")
    @Operation(summary = "查询存储列表(分页)")
    public ResponseEntity<PageResp<StorageVO>> getPageStorageList(StorageQueryVO queryVO) {
        return ResponseEntity.ok(storageService.getPageStorageList(queryVO));
    }

    @GetMapping("/getStorageByStorageId/{storageId}")
    @SaCheckPermission("basic:storage:get")
    @Operation(summary = "根据存储ID查询存储信息")
    public ResponseEntity<StorageVO> getStorageByStorageId(@PathVariable(value = "storageId")
                                                       @NotNull(message = "存储ID不能为空")
                                                       @Parameter(name = "storageId", description = "存储ID", required = true) Long storageId) {
        return ResponseEntity.ok(storageService.getStorageByStorageId(storageId));
    }

}
