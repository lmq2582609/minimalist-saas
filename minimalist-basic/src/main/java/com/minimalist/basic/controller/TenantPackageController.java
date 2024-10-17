package com.minimalist.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.minimalist.basic.entity.vo.tenant.TenantPackageQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantPackageVO;
import com.minimalist.basic.service.TenantPackageService;
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
@RequestMapping("/basic/tenantPackage")
@Tag(name = "租户管理")
public class TenantPackageController {

    @Autowired
    private TenantPackageService tenantPackageService;

    @TenantIgnore
    @PostMapping("/addTenantPackage")
    @SaCheckPermission("basic:tenantPackage:add")
    @Operation(summary = "添加租户套餐")
    public ResponseEntity<Void> addTenantPackage(@RequestBody @Validated(Add.class) TenantPackageVO tenantPackageVO) {
        tenantPackageService.addTenantPackage(tenantPackageVO);
        return ResponseEntity.ok().build();
    }

    @TenantIgnore
    @DeleteMapping("/deleteTenantPackageByTenantPackageId")
    @SaCheckPermission("basic:tenantPackage:delete")
    @Operation(summary = "删除租户套餐 -> 根据租户套餐ID删除租户套餐")
    public ResponseEntity<Void> deleteTenantPackageByTenantPackageId(@RequestParam("tenantPackageId")
                                                   @NotNull(message = "租户套餐ID不能为空")
                                                   @Parameter(name = "tenantPackageId", required = true, description = "租户套餐ID") Long tenantPackageId) {
        tenantPackageService.deleteTenantPackageByTenantPackageId(tenantPackageId);
        return ResponseEntity.ok().build();
    }

    @TenantIgnore
    @PutMapping("/updateTenantPackageByTenantPackageId")
    @SaCheckPermission("basic:tenantPackage:update")
    @Operation(summary = "修改租户套餐 -> 根据租户套餐ID修改")
    public ResponseEntity<Void> updateTenantPackageByTenantPackageId(@RequestBody @Validated(Update.class) TenantPackageVO tenantPackageVO) {
        tenantPackageService.updateTenantPackageByTenantPackageId(tenantPackageVO);
        return ResponseEntity.ok().build();
    }

    @TenantIgnore
    @GetMapping("/getPageTenantPackageList")
    @SaCheckPermission("basic:tenantPackage:get")
    @Operation(summary = "查询租户套餐(分页)")
    public ResponseEntity<PageResp<TenantPackageVO>> getPageTenantPackageList(TenantPackageQueryVO queryVO) {
        return ResponseEntity.ok(tenantPackageService.getPageTenantPackageList(queryVO));
    }

    @TenantIgnore
    @GetMapping("/getTenantPackageByTenantPackageId/{tenantPackageId}")
    @SaCheckPermission("basic:tenantPackage:get")
    @Operation(summary = "根据租户套餐ID查询租户套餐")
    public ResponseEntity<TenantPackageVO> getTenantPackageByTenantPackageId(@PathVariable(value = "tenantPackageId")
                                                  @NotNull(message = "租户套餐ID不能为空")
                                                  @Parameter(name = "tenantPackageId", description = "租户套餐ID", required = true) Long tenantPackageId) {
        return ResponseEntity.ok(tenantPackageService.getTenantPackageByTenantPackageId(tenantPackageId));
    }

}
