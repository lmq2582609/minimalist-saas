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
import com.minimalist.basic.entity.po.MTenant;
import com.minimalist.basic.service.MTenantService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 租户表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mTenant")
public class MTenantController {

    @Autowired
    private MTenantService mTenantService;

    /**
     * 添加租户表。
     *
     * @param mTenant 租户表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MTenant mTenant) {
        return mTenantService.save(mTenant);
    }

    /**
     * 根据主键删除租户表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mTenantService.removeById(id);
    }

    /**
     * 根据主键更新租户表。
     *
     * @param mTenant 租户表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MTenant mTenant) {
        return mTenantService.updateById(mTenant);
    }

    /**
     * 查询所有租户表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MTenant> list() {
        return mTenantService.list();
    }

    /**
     * 根据租户表主键获取详细信息。
     *
     * @param id 租户表主键
     * @return 租户表详情
     */
    @GetMapping("getInfo/{id}")
    public MTenant getInfo(@PathVariable Serializable id) {
        return mTenantService.getById(id);
    }

    /**
     * 分页查询租户表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MTenant> page(Page<MTenant> page) {
        return mTenantService.page(page);
    }

}
