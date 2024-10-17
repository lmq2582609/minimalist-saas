package com.minimalist.basic.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.entity.vo.config.ConfigQueryVO;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.basic.service.ConfigService;
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
@Tag(name = "参数配置管理")
@RequestMapping("/basic/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @PostMapping("/addConfig")
    @SaCheckPermission("basic:config:add")
    @Operation(summary = "添加参数")
    public ResponseEntity<Void> addConfig(@RequestBody @Validated(Add.class) ConfigVO configVO) {
        configService.addConfig(configVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteConfigByConfigId")
    @SaCheckPermission("basic:config:delete")
    @Operation(summary = "删除参数 -> 根据参数ID删除")
    public ResponseEntity<Void> deleteConfigByConfigId(@RequestParam("configId")
                                                    @NotNull(message = "参数ID不能为空")
                                                    @Parameter(name = "configId", required = true, description = "参数ID") Long configId) {
        configService.deleteConfigByConfigId(configId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateConfigByConfigId")
    @SaCheckPermission("basic:config:update")
    @Operation(summary = "修改参数")
    public ResponseEntity<Void> updateConfigByConfigId(@RequestBody @Validated(Update.class) ConfigVO configVO) {
        configService.updateConfigByConfigId(configVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageConfigList")
    @SaCheckPermission("basic:config:get")
    @Operation(summary = "查询参数配置列表(分页)")
    public ResponseEntity<PageResp<ConfigVO>> getPageConfigList(ConfigQueryVO queryVO) {
        return ResponseEntity.ok(configService.getPageConfigList(queryVO));
    }

    @GetMapping("/getConfigByConfigId/{configId}")
    @SaCheckPermission("basic:config:get")
    @Operation(summary = "根据参数ID查询参数")
    public ResponseEntity<ConfigVO> getConfigByConfigId(@PathVariable(value = "configId")
                                                  @NotNull(message = "参数ID不能为空")
                                                  @Parameter(name = "configId", description = "参数ID", required = true) Long configId) {
        return ResponseEntity.ok(configService.getConfigByConfigId(configId));
    }

}
