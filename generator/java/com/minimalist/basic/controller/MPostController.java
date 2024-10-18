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
import com.minimalist.basic.entity.po.MPost;
import com.minimalist.basic.service.MPostService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 岗位表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mPost")
public class MPostController {

    @Autowired
    private MPostService mPostService;

    /**
     * 添加岗位表。
     *
     * @param mPost 岗位表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MPost mPost) {
        return mPostService.save(mPost);
    }

    /**
     * 根据主键删除岗位表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mPostService.removeById(id);
    }

    /**
     * 根据主键更新岗位表。
     *
     * @param mPost 岗位表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MPost mPost) {
        return mPostService.updateById(mPost);
    }

    /**
     * 查询所有岗位表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MPost> list() {
        return mPostService.list();
    }

    /**
     * 根据岗位表主键获取详细信息。
     *
     * @param id 岗位表主键
     * @return 岗位表详情
     */
    @GetMapping("getInfo/{id}")
    public MPost getInfo(@PathVariable Serializable id) {
        return mPostService.getById(id);
    }

    /**
     * 分页查询岗位表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MPost> page(Page<MPost> page) {
        return mPostService.page(page);
    }

}
