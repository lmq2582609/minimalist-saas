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
import com.minimalist.basic.entity.po.MTenantPackage;
import com.minimalist.basic.service.MTenantPackageService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 租户套餐表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mTenantPackage")
public class MTenantPackageController {

    @Autowired
    private MTenantPackageService mTenantPackageService;

    /**
     * 添加租户套餐表。
     *
     * @param mTenantPackage 租户套餐表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MTenantPackage mTenantPackage) {
        return mTenantPackageService.save(mTenantPackage);
    }

    /**
     * 根据主键删除租户套餐表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mTenantPackageService.removeById(id);
    }

    /**
     * 根据主键更新租户套餐表。
     *
     * @param mTenantPackage 租户套餐表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MTenantPackage mTenantPackage) {
        return mTenantPackageService.updateById(mTenantPackage);
    }

    /**
     * 查询所有租户套餐表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MTenantPackage> list() {
        return mTenantPackageService.list();
    }

    /**
     * 根据租户套餐表主键获取详细信息。
     *
     * @param id 租户套餐表主键
     * @return 租户套餐表详情
     */
    @GetMapping("getInfo/{id}")
    public MTenantPackage getInfo(@PathVariable Serializable id) {
        return mTenantPackageService.getById(id);
    }

    /**
     * 分页查询租户套餐表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MTenantPackage> page(Page<MTenantPackage> page) {
        return mTenantPackageService.page(page);
    }

}
