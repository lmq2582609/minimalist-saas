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
import com.minimalist.basic.entity.po.MRoleDept;
import com.minimalist.basic.service.MRoleDeptService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 角色与部门关联表 1角色-N部门 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mRoleDept")
public class MRoleDeptController {

    @Autowired
    private MRoleDeptService mRoleDeptService;

    /**
     * 添加角色与部门关联表 1角色-N部门。
     *
     * @param mRoleDept 角色与部门关联表 1角色-N部门
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MRoleDept mRoleDept) {
        return mRoleDeptService.save(mRoleDept);
    }

    /**
     * 根据主键删除角色与部门关联表 1角色-N部门。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mRoleDeptService.removeById(id);
    }

    /**
     * 根据主键更新角色与部门关联表 1角色-N部门。
     *
     * @param mRoleDept 角色与部门关联表 1角色-N部门
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MRoleDept mRoleDept) {
        return mRoleDeptService.updateById(mRoleDept);
    }

    /**
     * 查询所有角色与部门关联表 1角色-N部门。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MRoleDept> list() {
        return mRoleDeptService.list();
    }

    /**
     * 根据角色与部门关联表 1角色-N部门主键获取详细信息。
     *
     * @param id 角色与部门关联表 1角色-N部门主键
     * @return 角色与部门关联表 1角色-N部门详情
     */
    @GetMapping("getInfo/{id}")
    public MRoleDept getInfo(@PathVariable Serializable id) {
        return mRoleDeptService.getById(id);
    }

    /**
     * 分页查询角色与部门关联表 1角色-N部门。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MRoleDept> page(Page<MRoleDept> page) {
        return mRoleDeptService.page(page);
    }

}
