package com.minimalist.basic.controller;

import com.minimalist.basic.entity.vo.dict.*;
import com.minimalist.basic.service.DictService;
import com.minimalist.basic.entity.mybatis.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RestController
@Tag(name = "字典管理")
@RequestMapping("/basic/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @PostMapping("/addDict")
    @PreAuthorize("hasAuthority('basic:dict:add')")
    @Operation(summary = "添加字典")
    public ResponseEntity<Void> addDict(@RequestBody @Validated DictInfoVO dictInfoVO) {
        dictService.addDict(dictInfoVO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteDictByDictId")
    @PreAuthorize("hasAuthority('basic:dict:delete')")
    @Operation(summary = "删除字典 -> 根据字典ID删除")
    public ResponseEntity<Void> deleteDictByDictId(@RequestParam("dictId")
                                            @NotNull(message = "字典ID不能为空")
                                            @Parameter(name = "dictId", required = true, description = "字典ID") Long dictId) {
        dictService.deleteDictByDictId(dictId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteDictByDictType")
    @PreAuthorize("hasAuthority('basic:dict:delete')")
    @Operation(summary = "删除字典 -> 根据字典类型删除")
    public ResponseEntity<Void> deleteDictByDictType(@RequestParam("dictType")
                                            @NotNull(message = "字典类型不能为空")
                                            @Parameter(name = "dictType", required = true, description = "字典类型") String dictType) {
        dictService.deleteDictByDictType(dictType);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateDictByDictId")
    @PreAuthorize("hasAuthority('basic:dict:update')")
    @Operation(summary = "修改字典")
    public ResponseEntity<Void> updateDictByDictId(@RequestBody @Validated DictInfoVO dictInfoVO) {
        dictService.updateDictByDictId(dictInfoVO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPageDictList")
    @PreAuthorize("hasAuthority('basic:dict:get')")
    @Operation(summary = "查询字典列表(分页)")
    public ResponseEntity<PageResp<DictVO>> getPageDictList(DictQueryVO queryVO) {
        return ResponseEntity.ok(dictService.getPageDictList(queryVO));
    }

    @GetMapping("/getDictByDictType/{dictType}")
    @PreAuthorize("hasAuthority('basic:dict:get')")
    @Operation(summary = "根据字典类型查询字典 -> 用于字典管理页面")
    public ResponseEntity<DictInfoVO> getDictByDictType(@PathVariable(value = "dictType")
                                            @Parameter(name = "dictType", description = "字典类型", required = true) String dictType) {
        return ResponseEntity.ok(dictService.getDictByDictType(dictType));
    }

    @GetMapping("/getDictList/{dictTypes}")
    @PreAuthorize("hasAuthority('basic:dict:get')")
    @Operation(summary = "根据字典类型查询字典 -> 用于下拉框数据展示或编码转换")
    public ResponseEntity<List<DictCacheVO>> getDictList(@PathVariable(value = "dictTypes", required = false)
                                         @Parameter(name = "dictTypes", description = "字典类型列表，为空则查询所有字典数据") List<String> dictType) {
        return ResponseEntity.ok(dictService.getDictList(dictType));
    }

}
