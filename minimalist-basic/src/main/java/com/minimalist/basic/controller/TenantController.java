package com.minimalist.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.minimalist.basic.entity.vo.tenant.TenantQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.service.TenantService;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.config.tenant.TenantIgnore;
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
@RequestMapping("/basic/tenant")
@Tag(name = "租户管理")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @TenantIgnore
    @PostMapping("/addTenant")
    @SaCheckPermission("basic:tenant:add")
    @Operation(summary = "添加租户")
    public ResponseEntity<Void> addTenant(@RequestBody @Validated(Add.class) TenantVO tenantVO) {
        tenantService.addTenant(tenantVO);
        return ResponseEntity.ok().build();
    }

    @TenantIgnore
    @DeleteMapping("/deleteTenantByTenantId")
    @SaCheckPermission("basic:tenant:delete")
    @Operation(summary = "删除租户 -> 根据租户ID删除租户")
    public ResponseEntity<Void> deleteTenantByTenantId(@RequestParam("tenantId")
                                                                     @NotNull(message = "租户ID不能为空")
                                                                     @Parameter(name = "tenantId", required = true, description = "租户ID") Long tenantId) {
        tenantService.deleteTenantByTenantId(tenantId);
        return ResponseEntity.ok().build();
    }

    @TenantIgnore
    @PutMapping("/updateTenantByTenantId")
    @SaCheckPermission("basic:tenant:update")
    @Operation(summary = "修改租户 -> 根据租户ID修改")
    public ResponseEntity<Void> updateTenantByTenantId(@RequestBody @Validated(Update.class) TenantVO tenantVO) {
        tenantService.updateTenantByTenantId(tenantVO);
        return ResponseEntity.ok().build();
    }

    @TenantIgnore
    @GetMapping("/getPageTenantList")
    @SaCheckPermission("basic:tenant:get")
    @Operation(summary = "查询租户(分页)")
    public ResponseEntity<PageResp<TenantVO>> getPageTenantList(TenantQueryVO queryVO) {
        return ResponseEntity.ok(tenantService.getPageTenantList(queryVO));
    }

    @TenantIgnore
    @GetMapping("/getTenantByTenantId/{tenantId}")
    @SaCheckPermission("basic:tenant:get")
    @Operation(summary = "根据租户ID查询租户")
    public ResponseEntity<TenantVO> getTenantByTenantId(@PathVariable(value = "tenantId")
                                                                             @NotNull(message = "租户ID不能为空")
                                                                             @Parameter(name = "tenantId", description = "租户ID", required = true) Long tenantId) {
        return ResponseEntity.ok(tenantService.getTenantByTenantId(tenantId));
    }

}
