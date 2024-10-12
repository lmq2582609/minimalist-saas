package com.minimalist.basic.service;

import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.entity.vo.storage.StorageQueryVO;
import com.minimalist.basic.entity.vo.storage.StorageVO;

public interface StorageService {

    /**
     * 添加存储
     * @param storageVO 存储信息
     */
    void addStorage(StorageVO storageVO);

    /**
     * 删除存储 -> 根据存储ID删除
     * @param storageId 存储ID
     */
    void deleteStorageByStorageId(Long storageId);

    /**
     * 修改存储 -> 根据存储ID修改
     * @param storageVO 存储信息
     */
    void updateStorageByStorageId(StorageVO storageVO);

    /**
     * 查询存储列表(分页)
     * @param queryVO 查询条件
     * @return 存储列表
     */
    PageResp<StorageVO> getPageStorageList(StorageQueryVO queryVO);

    /**
     * 根据存储ID查询存储信息
     * @param storageId 存储ID
     * @return 存储信息
     */
    StorageVO getStorageByStorageId(Long storageId);

}
