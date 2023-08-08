package com.minimalist.basic.service;

import com.minimalist.basic.entity.vo.dict.*;
import com.minimalist.common.mybatis.bo.PageResp;

import java.util.List;

public interface DictService {

    /**
     * 新增字典
     * @param dictInfoVO 字典实体
     */
    void addDict(DictInfoVO dictInfoVO);

    /**
     * 删除字典 -> 根据字典ID删除
     * @param dictId 字典ID
     */
    void deleteDictByDictId(Long dictId);

    /**
     * 删除字典 -> 根据字典类型删除
     * @param dictType 字典类型
     */
    void deleteDictByDictType(String dictType);

    /**
     * 修改字典
     * @param dictInfoVO 字典实体
     */
    void updateDictByDictId(DictInfoVO dictInfoVO);

    /**
     * 查询字典列表(分页)
     * @param queryVO 查询实体
     * @return 分页字典数据列表
     */
    PageResp<DictVO> getPageDictList(DictQueryVO queryVO);

    /**
     * 根据字典类型查询字典
     * @param dictType 字典类型
     * @return 字典数据
     */
    DictInfoVO getDictByDictType(String dictType);

    /**
     * 根据字典类型查询字典
     * @param dictTypes 字典类型，为空查询所有字典数据
     * @return 字典数据列表
     */
    List<DictCacheVO> getDictList(List<String> dictTypes);

}
