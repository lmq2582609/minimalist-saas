package com.minimalist.basic.controller;

import com.minimalist.basic.entity.vo.dept.DeptQueryVO;
import com.minimalist.basic.entity.vo.dept.DeptVO;
import com.minimalist.basic.service.DeptService;
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
import java.util.List;

@Validated
@RestController
@RequestMapping("/basic/dept")
@Tag(name = "部门管理")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @PostMapping("/addDept")
    @Operation(summary = "添加部门")
    public ResponseEntity<Void> addDept(@RequestBody @Validated(Add.class) DeptVO deptVO) {
        deptService.addDept(deptVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteDeptByDeptId")
    @Operation(summary = "删除部门 -> 根据部门ID删除")
    public ResponseEntity<Void> deleteDeptByDeptId(@RequestParam("deptId")
                                                   @NotNull(message = "部门ID不能为空")
                                                   @Parameter(name = "deptId", required = true, description = "部门ID") Long deptId) {
        deptService.deleteDeptByDeptId(deptId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateDeptByDeptId")
    @Operation(summary = "修改部门 -> 根据部门ID修改")
    public ResponseEntity<Void> updateDeptByDeptId(@RequestBody @Validated(Update.class) DeptVO deptVO) {
        deptService.updateDeptByDeptId(deptVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getDeptList")
    @Operation(summary = "查询部门列表(不分页，获取全部数据) -> 部门管理使用")
    public ResponseEntity<List<DeptVO>> getDeptList(DeptQueryVO queryVO) {
        return ResponseEntity.ok(deptService.getDeptList(queryVO));
    }

    @GetMapping("/getEnableDeptList")
    @Operation(summary = "查询部门树 -> 只获取正常状态的部门")
    public ResponseEntity<List<DeptVO>> getEnableDeptList() {
        return ResponseEntity.ok(deptService.getEnableDeptList());
    }

    @GetMapping("/getDeptByDeptId/{deptId}")
    @Operation(summary = "根据部门ID查询部门")
    public ResponseEntity<DeptVO> getDeptByDeptId(@PathVariable(value = "deptId")
                                                  @NotNull(message = "部门ID不能为空")
                                                  @Parameter(name = "deptId", description = "部门ID", required = true) Long deptId) {
        return ResponseEntity.ok(deptService.getDeptByDeptId(deptId));
    }

}
