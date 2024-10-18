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
import com.minimalist.basic.entity.po.MUserRole;
import com.minimalist.basic.service.MUserRoleService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 用户与角色关联表 1用户-N角色 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mUserRole")
public class MUserRoleController {

    @Autowired
    private MUserRoleService mUserRoleService;

    /**
     * 添加用户与角色关联表 1用户-N角色。
     *
     * @param mUserRole 用户与角色关联表 1用户-N角色
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MUserRole mUserRole) {
        return mUserRoleService.save(mUserRole);
    }

    /**
     * 根据主键删除用户与角色关联表 1用户-N角色。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mUserRoleService.removeById(id);
    }

    /**
     * 根据主键更新用户与角色关联表 1用户-N角色。
     *
     * @param mUserRole 用户与角色关联表 1用户-N角色
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MUserRole mUserRole) {
        return mUserRoleService.updateById(mUserRole);
    }

    /**
     * 查询所有用户与角色关联表 1用户-N角色。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MUserRole> list() {
        return mUserRoleService.list();
    }

    /**
     * 根据用户与角色关联表 1用户-N角色主键获取详细信息。
     *
     * @param id 用户与角色关联表 1用户-N角色主键
     * @return 用户与角色关联表 1用户-N角色详情
     */
    @GetMapping("getInfo/{id}")
    public MUserRole getInfo(@PathVariable Serializable id) {
        return mUserRoleService.getById(id);
    }

    /**
     * 分页查询用户与角色关联表 1用户-N角色。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MUserRole> page(Page<MUserRole> page) {
        return mUserRoleService.page(page);
    }

}
