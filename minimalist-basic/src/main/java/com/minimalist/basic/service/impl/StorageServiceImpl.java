package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.PostEnum;
import com.minimalist.basic.entity.enums.StorageEnum;
import com.minimalist.basic.entity.po.MStorage;
import com.minimalist.basic.entity.vo.storage.StorageQueryVO;
import com.minimalist.basic.entity.vo.storage.StorageVO;
import com.minimalist.basic.mapper.MStorageMapper;
import com.minimalist.basic.service.StorageService;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.utils.UnqIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private MStorageMapper storageMapper;

    /**
     * 添加存储
     * @param storageVO 存储信息
     */
    @Override
    public void addStorage(StorageVO storageVO) {
        long count = storageMapper.checkStorageByStorageCode(storageVO.getStorageCode(), null);
        Assert.isFalse(count > 0, () -> new BusinessException(StorageEnum.ErrorMsg.EXISTS_STORAGE_CODE.getDesc()));
        MStorage storage = BeanUtil.copyProperties(storageVO, MStorage.class);
        storage.setStatus(StorageEnum.Status.STATUS_1.getCode());
        storage.setStorageId(UnqIdUtil.uniqueId());
        storageMapper.insert(storage);
    }

    /**
     * 删除存储 -> 根据存储ID删除
     * @param storageId 存储ID
     */
    @Override
    public void deleteStorageByStorageId(Long storageId) {
        storageMapper.deleteStorageByStorageId(storageId);
    }

    /**
     * 修改存储 -> 根据存储ID修改
     * @param storageVO 存储信息
     */
    @Override
    public void updateStorageByStorageId(StorageVO storageVO) {
        long count = storageMapper.checkStorageByStorageCode(storageVO.getStorageCode(), storageVO.getStorageId());
        Assert.isFalse(count > 0, () -> new BusinessException(StorageEnum.ErrorMsg.EXISTS_STORAGE_CODE.getDesc()));
        MStorage oldStorage = storageMapper.selectStorageByStorageId(storageVO.getStorageId());
        Assert.notNull(oldStorage, () -> new BusinessException(StorageEnum.ErrorMsg.NONENTITY_STORAGE.getDesc()));
        MStorage storage = BeanUtil.copyProperties(storageVO, MStorage.class);
        storage.updateBeforeSetVersion(oldStorage.getVersion());
        storageMapper.updateStorageByStorageId(storage);
    }

    /**
     * 查询存储列表(分页)
     * @param queryVO 查询条件
     * @return 存储列表
     */
    @Override
    public PageResp<StorageVO> getPageStorageList(StorageQueryVO queryVO) {
        Page<MStorage> storagePage = storageMapper.selectPageStorageList(queryVO);
        List<StorageVO> storageVOList = BeanUtil.copyToList(storagePage.getRecords(), StorageVO.class);
        return new PageResp<>(storageVOList, storagePage.getTotal());
    }

    /**
     * 根据存储ID查询存储信息
     * @param storageId 存储ID
     * @return 存储信息
     */
    @Override
    public StorageVO getStorageByStorageId(Long storageId) {
        return BeanUtil.copyProperties(storageMapper.selectStorageByStorageId(storageId), StorageVO.class);
    }

}
