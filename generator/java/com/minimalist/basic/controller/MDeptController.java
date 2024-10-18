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
import com.minimalist.basic.entity.po.MDept;
import com.minimalist.basic.service.MDeptService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 部门表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mDept")
public class MDeptController {

    @Autowired
    private MDeptService mDeptService;

    /**
     * 添加部门表。
     *
     * @param mDept 部门表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MDept mDept) {
        return mDeptService.save(mDept);
    }

    /**
     * 根据主键删除部门表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mDeptService.removeById(id);
    }

    /**
     * 根据主键更新部门表。
     *
     * @param mDept 部门表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MDept mDept) {
        return mDeptService.updateById(mDept);
    }

    /**
     * 查询所有部门表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MDept> list() {
        return mDeptService.list();
    }

    /**
     * 根据部门表主键获取详细信息。
     *
     * @param id 部门表主键
     * @return 部门表详情
     */
    @GetMapping("getInfo/{id}")
    public MDept getInfo(@PathVariable Serializable id) {
        return mDeptService.getById(id);
    }

    /**
     * 分页查询部门表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MDept> page(Page<MDept> page) {
        return mDeptService.page(page);
    }

}
