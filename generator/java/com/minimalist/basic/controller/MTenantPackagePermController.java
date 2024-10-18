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
import com.minimalist.basic.entity.po.MTenantPackagePerm;
import com.minimalist.basic.service.MTenantPackagePermService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 租户套餐与权限关联表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mTenantPackagePerm")
public class MTenantPackagePermController {

    @Autowired
    private MTenantPackagePermService mTenantPackagePermService;

    /**
     * 添加租户套餐与权限关联表。
     *
     * @param mTenantPackagePerm 租户套餐与权限关联表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MTenantPackagePerm mTenantPackagePerm) {
        return mTenantPackagePermService.save(mTenantPackagePerm);
    }

    /**
     * 根据主键删除租户套餐与权限关联表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mTenantPackagePermService.removeById(id);
    }

    /**
     * 根据主键更新租户套餐与权限关联表。
     *
     * @param mTenantPackagePerm 租户套餐与权限关联表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MTenantPackagePerm mTenantPackagePerm) {
        return mTenantPackagePermService.updateById(mTenantPackagePerm);
    }

    /**
     * 查询所有租户套餐与权限关联表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MTenantPackagePerm> list() {
        return mTenantPackagePermService.list();
    }

    /**
     * 根据租户套餐与权限关联表主键获取详细信息。
     *
     * @param id 租户套餐与权限关联表主键
     * @return 租户套餐与权限关联表详情
     */
    @GetMapping("getInfo/{id}")
    public MTenantPackagePerm getInfo(@PathVariable Serializable id) {
        return mTenantPackagePermService.getById(id);
    }

    /**
     * 分页查询租户套餐与权限关联表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MTenantPackagePerm> page(Page<MTenantPackagePerm> page) {
        return mTenantPackagePermService.page(page);
    }

}
