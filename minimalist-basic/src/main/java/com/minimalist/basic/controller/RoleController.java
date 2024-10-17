package com.minimalist.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.minimalist.basic.entity.vo.role.RoleQueryVO;
import com.minimalist.basic.entity.vo.role.RoleVO;
import com.minimalist.basic.service.RoleService;
import com.minimalist.basic.config.mybatis.bo.PageResp;
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
@RequestMapping("/basic/role")
@Tag(name = "角色管理")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/addRole")
    @SaCheckPermission("basic:role:add")
    @Operation(summary = "添加角色")
    public ResponseEntity<Void> addRole(@RequestBody @Validated(Add.class) RoleVO roleVO) {
        roleService.addRole(roleVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteRoleByRoleId")
    @SaCheckPermission("basic:role:delete")
    @Operation(summary = "删除角色 -> 根据角色ID删除角色")
    public ResponseEntity<Void> deleteRoleByRoleId(@RequestParam("roleId")
                                                   @NotNull(message = "角色ID不能为空")
                                                   @Parameter(name = "roleId", required = true, description = "角色ID") Long roleId) {
        roleService.deleteRoleByRoleId(roleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateRoleByRoleId")
    @SaCheckPermission("basic:role:update")
    @Operation(summary = "修改角色 -> 根据角色ID修改")
    public ResponseEntity<Void> updateRoleByRoleId(@RequestBody @Validated(Update.class) RoleVO roleVO) {
        roleService.updateRoleByRoleId(roleVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageRoleList")
    @SaCheckPermission("basic:role:get")
    @Operation(summary = "查询角色(分页) -> 角色管理使用")
    public ResponseEntity<PageResp<RoleVO>> getPageRoleList(RoleQueryVO queryVO) {
        return ResponseEntity.ok(roleService.getPageRoleList(queryVO));
    }

    @GetMapping("/getRoleByRoleId/{roleId}")
    @SaCheckPermission("basic:role:get")
    @Operation(summary = "根据角色ID查询角色")
    public ResponseEntity<RoleVO> getRoleByRoleId(@PathVariable(value = "roleId")
                                                  @NotNull(message = "角色ID不能为空")
                                                  @Parameter(name = "roleId", description = "角色ID", required = true) Long roleId) {
        return ResponseEntity.ok(roleService.getRoleByRoleId(roleId));
    }

}
