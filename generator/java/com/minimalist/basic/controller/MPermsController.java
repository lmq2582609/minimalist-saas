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
import com.minimalist.basic.entity.po.MPerms;
import com.minimalist.basic.service.MPermsService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 权限表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mPerms")
public class MPermsController {

    @Autowired
    private MPermsService mPermsService;

    /**
     * 添加权限表。
     *
     * @param mPerms 权限表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MPerms mPerms) {
        return mPermsService.save(mPerms);
    }

    /**
     * 根据主键删除权限表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mPermsService.removeById(id);
    }

    /**
     * 根据主键更新权限表。
     *
     * @param mPerms 权限表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MPerms mPerms) {
        return mPermsService.updateById(mPerms);
    }

    /**
     * 查询所有权限表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MPerms> list() {
        return mPermsService.list();
    }

    /**
     * 根据权限表主键获取详细信息。
     *
     * @param id 权限表主键
     * @return 权限表详情
     */
    @GetMapping("getInfo/{id}")
    public MPerms getInfo(@PathVariable Serializable id) {
        return mPermsService.getById(id);
    }

    /**
     * 分页查询权限表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MPerms> page(Page<MPerms> page) {
        return mPermsService.page(page);
    }

}
