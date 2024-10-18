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
import com.minimalist.basic.entity.po.MNotice;
import com.minimalist.basic.service.MNoticeService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 通知公告表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mNotice")
public class MNoticeController {

    @Autowired
    private MNoticeService mNoticeService;

    /**
     * 添加通知公告表。
     *
     * @param mNotice 通知公告表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MNotice mNotice) {
        return mNoticeService.save(mNotice);
    }

    /**
     * 根据主键删除通知公告表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mNoticeService.removeById(id);
    }

    /**
     * 根据主键更新通知公告表。
     *
     * @param mNotice 通知公告表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MNotice mNotice) {
        return mNoticeService.updateById(mNotice);
    }

    /**
     * 查询所有通知公告表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MNotice> list() {
        return mNoticeService.list();
    }

    /**
     * 根据通知公告表主键获取详细信息。
     *
     * @param id 通知公告表主键
     * @return 通知公告表详情
     */
    @GetMapping("getInfo/{id}")
    public MNotice getInfo(@PathVariable Serializable id) {
        return mNoticeService.getById(id);
    }

    /**
     * 分页查询通知公告表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MNotice> page(Page<MNotice> page) {
        return mNoticeService.page(page);
    }

}
