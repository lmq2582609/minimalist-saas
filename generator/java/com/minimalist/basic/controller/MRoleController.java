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
import com.minimalist.basic.entity.po.MRole;
import com.minimalist.basic.service.MRoleService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 角色表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mRole")
public class MRoleController {

    @Autowired
    private MRoleService mRoleService;

    /**
     * 添加角色表。
     *
     * @param mRole 角色表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MRole mRole) {
        return mRoleService.save(mRole);
    }

    /**
     * 根据主键删除角色表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mRoleService.removeById(id);
    }

    /**
     * 根据主键更新角色表。
     *
     * @param mRole 角色表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MRole mRole) {
        return mRoleService.updateById(mRole);
    }

    /**
     * 查询所有角色表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MRole> list() {
        return mRoleService.list();
    }

    /**
     * 根据角色表主键获取详细信息。
     *
     * @param id 角色表主键
     * @return 角色表详情
     */
    @GetMapping("getInfo/{id}")
    public MRole getInfo(@PathVariable Serializable id) {
        return mRoleService.getById(id);
    }

    /**
     * 分页查询角色表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MRole> page(Page<MRole> page) {
        return mRoleService.page(page);
    }

}
