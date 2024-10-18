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
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.service.MStorageService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 存储管理表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mStorage")
public class MStorageController {

    @Autowired
    private MStorageService mStorageService;

    /**
     * 添加存储管理表。
     *
     * @param mStorage 存储管理表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MStorage mStorage) {
        return mStorageService.save(mStorage);
    }

    /**
     * 根据主键删除存储管理表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mStorageService.removeById(id);
    }

    /**
     * 根据主键更新存储管理表。
     *
     * @param mStorage 存储管理表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MStorage mStorage) {
        return mStorageService.updateById(mStorage);
    }

    /**
     * 查询所有存储管理表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MStorage> list() {
        return mStorageService.list();
    }

    /**
     * 根据存储管理表主键获取详细信息。
     *
     * @param id 存储管理表主键
     * @return 存储管理表详情
     */
    @GetMapping("getInfo/{id}")
    public MStorage getInfo(@PathVariable Serializable id) {
        return mStorageService.getById(id);
    }

    /**
     * 分页查询存储管理表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MStorage> page(Page<MStorage> page) {
        return mStorageService.page(page);
    }

}
