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
import com.minimalist.basic.entity.po.MDict;
import com.minimalist.basic.service.MDictService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 字典表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mDict")
public class MDictController {

    @Autowired
    private MDictService mDictService;

    /**
     * 添加字典表。
     *
     * @param mDict 字典表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MDict mDict) {
        return mDictService.save(mDict);
    }

    /**
     * 根据主键删除字典表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mDictService.removeById(id);
    }

    /**
     * 根据主键更新字典表。
     *
     * @param mDict 字典表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MDict mDict) {
        return mDictService.updateById(mDict);
    }

    /**
     * 查询所有字典表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MDict> list() {
        return mDictService.list();
    }

    /**
     * 根据字典表主键获取详细信息。
     *
     * @param id 字典表主键
     * @return 字典表详情
     */
    @GetMapping("getInfo/{id}")
    public MDict getInfo(@PathVariable Serializable id) {
        return mDictService.getById(id);
    }

    /**
     * 分页查询字典表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MDict> page(Page<MDict> page) {
        return mDictService.page(page);
    }

}
