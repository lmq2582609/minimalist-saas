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
import com.minimalist.basic.entity.po.MUserDept;
import com.minimalist.basic.service.MUserDeptService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 用户与岗位关联表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mUserDept")
public class MUserDeptController {

    @Autowired
    private MUserDeptService mUserDeptService;

    /**
     * 添加用户与岗位关联表。
     *
     * @param mUserDept 用户与岗位关联表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MUserDept mUserDept) {
        return mUserDeptService.save(mUserDept);
    }

    /**
     * 根据主键删除用户与岗位关联表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mUserDeptService.removeById(id);
    }

    /**
     * 根据主键更新用户与岗位关联表。
     *
     * @param mUserDept 用户与岗位关联表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MUserDept mUserDept) {
        return mUserDeptService.updateById(mUserDept);
    }

    /**
     * 查询所有用户与岗位关联表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MUserDept> list() {
        return mUserDeptService.list();
    }

    /**
     * 根据用户与岗位关联表主键获取详细信息。
     *
     * @param id 用户与岗位关联表主键
     * @return 用户与岗位关联表详情
     */
    @GetMapping("getInfo/{id}")
    public MUserDept getInfo(@PathVariable Serializable id) {
        return mUserDeptService.getById(id);
    }

    /**
     * 分页查询用户与岗位关联表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MUserDept> page(Page<MUserDept> page) {
        return mUserDeptService.page(page);
    }

}
