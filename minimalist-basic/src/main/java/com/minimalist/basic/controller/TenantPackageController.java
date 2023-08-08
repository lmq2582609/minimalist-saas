package com.minimalist.basic.controller;

import com.minimalist.basic.entity.vo.tenant.TenantPackageQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantPackageVO;
import com.minimalist.basic.service.TenantPackageService;
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
@RequestMapping("/basic/tenantPackage")
@Tag(name = "租户管理")
public class TenantPackageController {

    @Autowired
    private TenantPackageService tenantPackageService;

    @PostMapping("/addTenantPackage")
    @PreAuthorize("hasAuthority('basic:tenantPackage:add')")
    @Operation(summary = "添加租户套餐")
    public ResponseEntity<Void> addTenantPackage(@RequestBody @Validated(Add.class) TenantPackageVO tenantPackageVO) {
        tenantPackageService.addTenantPackage(tenantPackageVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteTenantPackageByTenantPackageId")
    @PreAuthorize("hasAuthority('basic:tenantPackage:delete')")
    @Operation(summary = "删除租户套餐 -> 根据租户套餐ID删除租户套餐")
    public ResponseEntity<Void> deleteTenantPackageByTenantPackageId(@RequestParam("tenantPackageId")
                                                   @NotNull(message = "租户套餐ID不能为空")
                                                   @Parameter(name = "tenantPackageId", required = true, description = "租户套餐ID") Long tenantPackageId) {
        tenantPackageService.deleteTenantPackageByTenantPackageId(tenantPackageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateTenantPackageByTenantPackageId")
    @PreAuthorize("hasAuthority('basic:tenantPackage:update')")
    @Operation(summary = "修改租户套餐 -> 根据租户套餐ID修改")
    public ResponseEntity<Void> updateTenantPackageByTenantPackageId(@RequestBody @Validated(Update.class) TenantPackageVO tenantPackageVO) {
        tenantPackageService.updateTenantPackageByTenantPackageId(tenantPackageVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageTenantPackageList")
    @PreAuthorize("hasAuthority('basic:tenantPackage:get')")
    @Operation(summary = "查询租户套餐(分页)")
    public ResponseEntity<PageResp<TenantPackageVO>> getPageTenantPackageList(TenantPackageQueryVO queryVO) {
        return ResponseEntity.ok(tenantPackageService.getPageTenantPackageList(queryVO));
    }

    @GetMapping("/getTenantPackageByTenantPackageId/{tenantPackageId}")
    @PreAuthorize("hasAuthority('basic:tenantPackage:get')")
    @Operation(summary = "根据租户套餐ID查询租户套餐")
    public ResponseEntity<TenantPackageVO> getTenantPackageByTenantPackageId(@PathVariable(value = "tenantPackageId")
                                                  @NotNull(message = "租户套餐ID不能为空")
                                                  @Parameter(name = "tenantPackageId", description = "租户套餐ID", required = true) Long tenantPackageId) {
        return ResponseEntity.ok(tenantPackageService.getTenantPackageByTenantPackageId(tenantPackageId));
    }

}
