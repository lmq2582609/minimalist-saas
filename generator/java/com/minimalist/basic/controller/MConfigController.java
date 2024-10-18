package com.minimalist.basic.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.minimalist.basic.entity.po.MConfig;
import com.minimalist.basic.service.MConfigService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 参数配置表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mConfig")
public class MConfigController {

    @Autowired
    private MConfigService mConfigService;

    /**
     * 添加参数配置表。
     *
     * @param mConfig 参数配置表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MConfig mConfig) {
        return mConfigService.save(mConfig);
    }

    /**
     * 根据主键删除参数配置表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mConfigService.removeById(id);
    }

    /**
     * 根据主键更新参数配置表。
     *
     * @param mConfig 参数配置表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MConfig mConfig) {
        return mConfigService.updateById(mConfig);
    }

    /**
     * 查询所有参数配置表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MConfig> list() {
        return mConfigService.list();
    }

    /**
     * 根据参数配置表主键获取详细信息。
     *
     * @param id 参数配置表主键
     * @return 参数配置表详情
     */
    @GetMapping("getInfo/{id}")
    public MConfig getInfo(@PathVariable Serializable id) {
        return mConfigService.getById(id);
    }

    /**
     * 分页查询参数配置表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MConfig> page(Page<MConfig> page) {
        return mConfigService.page(page);
    }

}
