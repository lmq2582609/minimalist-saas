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
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.service.MUserService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 用户表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mUser")
public class MUserController {

    @Autowired
    private MUserService mUserService;

    /**
     * 添加用户表。
     *
     * @param mUser 用户表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MUser mUser) {
        return mUserService.save(mUser);
    }

    /**
     * 根据主键删除用户表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mUserService.removeById(id);
    }

    /**
     * 根据主键更新用户表。
     *
     * @param mUser 用户表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MUser mUser) {
        return mUserService.updateById(mUser);
    }

    /**
     * 查询所有用户表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MUser> list() {
        return mUserService.list();
    }

    /**
     * 根据用户表主键获取详细信息。
     *
     * @param id 用户表主键
     * @return 用户表详情
     */
    @GetMapping("getInfo/{id}")
    public MUser getInfo(@PathVariable Serializable id) {
        return mUserService.getById(id);
    }

    /**
     * 分页查询用户表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MUser> page(Page<MUser> page) {
        return mUserService.page(page);
    }

}
