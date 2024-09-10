package com.minimalist.basic.controller;

import com.minimalist.basic.entity.vo.config.ConfigQueryVO;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.basic.service.ConfigService;
import com.minimalist.common.mybatis.bo.PageResp;
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
    @Operation(summary = "添加参数")
    public ResponseEntity<Void> addConfig(@RequestBody @Validated ConfigVO configVO) {
        configService.addConfig(configVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteConfigByConfigId")
    @Operation(summary = "删除参数 -> 根据参数ID删除")
    public ResponseEntity<Void> deleteConfigByConfigId(@RequestParam("configId")
                                                    @NotNull(message = "字典ID不能为空")
                                                    @Parameter(name = "configId", required = true, description = "参数ID") Long configId) {
        configService.deleteConfigByConfigId(configId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateConfigByConfigId")
    @Operation(summary = "修改参数")
    public ResponseEntity<Void> updateConfigByConfigId(@RequestBody @Validated ConfigVO configVO) {
        configService.updateConfigByConfigId(configVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageConfigList")
    @Operation(summary = "查询参数配置列表(分页)")
    public ResponseEntity<PageResp<ConfigVO>> getPageConfigList(ConfigQueryVO queryVO) {
        return ResponseEntity.ok(configService.getPageConfigList(queryVO));
    }

//    @GetMapping("/getDictByDictType/{dictType}")
//    @Operation(summary = "根据字典类型查询字典 -> 用于字典管理页面")
//    public ResponseEntity<DictInfoVO> getDictByDictType(@PathVariable(value = "dictType")
//                                            @Parameter(name = "dictType", description = "字典类型", required = true) String dictType) {
//        return ResponseEntity.ok(dictService.getDictByDictType(dictType));
//    }
//
//    @GetMapping("/getDictList/{dictTypes}")
//    @Operation(summary = "根据字典类型查询字典 -> 用于下拉框数据展示或编码转换")
//    public ResponseEntity<List<DictCacheVO>> getDictList(@PathVariable(value = "dictTypes", required = false)
//                                         @Parameter(name = "dictTypes", description = "字典类型列表，为空则查询所有字典数据") List<String> dictType) {
//        return ResponseEntity.ok(dictService.getDictList(dictType));
//    }

}
