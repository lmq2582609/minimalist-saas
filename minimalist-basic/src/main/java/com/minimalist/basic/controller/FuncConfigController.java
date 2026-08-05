package com.minimalist.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.minimalist.basic.entity.vo.funcConfig.FuncConfigVO;
import com.minimalist.basic.service.FuncConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Tag(name = "功能配置管理")
@RequestMapping("/basic/funcConfig")
public class FuncConfigController {

    @Autowired
    private FuncConfigService funcConfigService;

    @GetMapping("/getFuncConfigList")
    @SaCheckPermission("basic:funcConfig:get")
    @Operation(summary = "查询所有功能配置")
    public ResponseEntity<List<FuncConfigVO>> getFuncConfigList() {
        return ResponseEntity.ok(funcConfigService.getFuncConfigList());
    }

    @PutMapping("/updateFuncConfig")
    @SaCheckPermission("basic:funcConfig:update")
    @Operation(summary = "修改功能配置")
    public ResponseEntity<Void> updateFuncConfig(@RequestBody @Validated FuncConfigVO funcConfigVO) {
        funcConfigService.updateFuncConfig(funcConfigVO);
        return ResponseEntity.ok().build();
    }

}
