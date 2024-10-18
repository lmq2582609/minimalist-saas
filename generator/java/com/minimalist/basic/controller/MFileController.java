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
import com.minimalist.basic.entity.po.MFile;
import com.minimalist.basic.service.MFileService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 * 文件表 控制层。
 *
 * @author asus
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/mFile")
public class MFileController {

    @Autowired
    private MFileService mFileService;

    /**
     * 添加文件表。
     *
     * @param mFile 文件表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody MFile mFile) {
        return mFileService.save(mFile);
    }

    /**
     * 根据主键删除文件表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return mFileService.removeById(id);
    }

    /**
     * 根据主键更新文件表。
     *
     * @param mFile 文件表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody MFile mFile) {
        return mFileService.updateById(mFile);
    }

    /**
     * 查询所有文件表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<MFile> list() {
        return mFileService.list();
    }

    /**
     * 根据文件表主键获取详细信息。
     *
     * @param id 文件表主键
     * @return 文件表详情
     */
    @GetMapping("getInfo/{id}")
    public MFile getInfo(@PathVariable Serializable id) {
        return mFileService.getById(id);
    }

    /**
     * 分页查询文件表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<MFile> page(Page<MFile> page) {
        return mFileService.page(page);
    }

}
