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
import com.minimalist.basic.entity.po.MRolePerm;
import com.minimalist.basic.service.MRolePermService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 角色与权限关联表 1角色-N权限 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mRolePerm")
public class MRolePermController {

    @Autowired
    private MRolePermService mRolePermService;

    /**
     * 添加角色与权限关联表 1角色-N权限。
     *
     * @param mRolePerm 角色与权限关联表 1角色-N权限
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MRolePerm mRolePerm) {
        return mRolePermService.save(mRolePerm);
    }

    /**
     * 根据主键删除角色与权限关联表 1角色-N权限。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mRolePermService.removeById(id);
    }

    /**
     * 根据主键更新角色与权限关联表 1角色-N权限。
     *
     * @param mRolePerm 角色与权限关联表 1角色-N权限
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MRolePerm mRolePerm) {
        return mRolePermService.updateById(mRolePerm);
    }

    /**
     * 查询所有角色与权限关联表 1角色-N权限。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MRolePerm> list() {
        return mRolePermService.list();
    }

    /**
     * 根据角色与权限关联表 1角色-N权限主键获取详细信息。
     *
     * @param id 角色与权限关联表 1角色-N权限主键
     * @return 角色与权限关联表 1角色-N权限详情
     */
    @GetMapping("getInfo/{id}")
    public MRolePerm getInfo(@PathVariable Serializable id) {
        return mRolePermService.getById(id);
    }

    /**
     * 分页查询角色与权限关联表 1角色-N权限。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MRolePerm> page(Page<MRolePerm> page) {
        return mRolePermService.page(page);
    }

}
