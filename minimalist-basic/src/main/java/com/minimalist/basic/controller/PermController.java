package com.minimalist.basic.controller;

import com.minimalist.basic.entity.vo.perm.PermQueryVO;
import com.minimalist.basic.entity.vo.perm.PermVO;
import com.minimalist.basic.service.PermService;
import com.minimalist.common.valid.Add;
import com.minimalist.common.valid.Update;
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
@RequestMapping("/basic/permission")
@Tag(name = "权限管理")
public class PermController {

    @Autowired
    private PermService permService;

    @PostMapping("/addPerm")
    @Operation(summary = "添加权限")
    public ResponseEntity<Void> addPerm(@RequestBody @Validated(Add.class) PermVO permVO) {
        permService.addPerm(permVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePermByPermId")
    @Operation(summary = "删除权限 -> 根据权限ID删除")
    public ResponseEntity<Void> deletePermByPermId(@RequestParam("permId")
                                                   @NotNull(message = "权限ID不能为空")
                                                   @Parameter(name = "permId", required = true, description = "权限ID") Long permId) {
        permService.deletePermByPermId(permId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updatePermByPermId")
    @Operation(summary = "修改权限 -> 根据权限ID修改")
    public ResponseEntity<Void> updatePermByPermId(@RequestBody @Validated(Update.class) PermVO permVO) {
        permService.updatePermByPermId(permVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPermList")
    @Operation(summary = "查询权限列表(不分页，获取全部数据) -> 权限管理使用")
    public ResponseEntity<List<PermVO>> getPermList(PermQueryVO queryVO) {
        return ResponseEntity.ok(permService.getPermList(queryVO));
    }

    @GetMapping("/getEnablePermList")
    @Operation(summary = "查询权限列表 -> 只获取正常状态的权限")
    public ResponseEntity<List<PermVO>> getEnablePermList() {
        return ResponseEntity.ok(permService.getEnablePermList());
    }

    @GetMapping("/getPermByPermId/{permId}")
    @Operation(summary = "根据权限ID查询权限")
    public ResponseEntity<PermVO> getPermByPermId(@PathVariable(value = "permId")
                                                        @NotNull(message = "权限ID不能为空")
                                                        @Parameter(name = "permId", description = "权限ID", required = true) Long permId) {
        return ResponseEntity.ok(permService.getPermByPermId(permId));
    }

}
