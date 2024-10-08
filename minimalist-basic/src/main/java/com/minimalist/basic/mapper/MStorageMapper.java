package com.minimalist.basic.mapper;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.po.MStorage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalist.basic.entity.vo.storage.StorageQueryVO;
import com.minimalist.common.mybatis.QueryCondition;

import java.util.List;

/**
 * <p>
 * 存储管理表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2024-10-08
 */
public interface MStorageMapper extends BaseMapper<MStorage> {

    /**
     * 根据存储编码查询存储信息
     * @param storageCode 存储编码
     * @return 存储信息
     */
    default MStorage selectStorageByStorageCode(String storageCode) {
        LambdaQueryWrapper<MStorage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MStorage::getStorageCode, storageCode);
        return selectOne(queryWrapper);
    }

    /**
     * 根据存储ID查询存储信息
     * @param storageId 存储ID
     * @return 存储信息
     */
    default MStorage selectStorageByStorageId(Long storageId) {
        LambdaQueryWrapper<MStorage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MStorage::getStorageId, storageId);
        return selectOne(queryWrapper);
    }

    /**
     * 根据code校验是否唯一
     * 修改时，防止code重复
     * @param storageCode 存储编码
     * @param storageId 存储ID
     * @return 行数
     */
    default long checkStorageByStorageCode(String storageCode, Long storageId) {
        LambdaQueryWrapper<MStorage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MStorage::getStorageCode, storageCode);
        if (ObjectUtil.isNotNull(storageId)) {
            queryWrapper.ne(MStorage::getStorageId, storageId);
        }
        return selectCount(queryWrapper);
    }

    /**
     * 根据存储ID删除存储
     * @param storageId 存储ID
     */
    default void deleteStorageByStorageId(Long storageId) {
        LambdaQueryWrapper<MStorage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MStorage::getStorageId, storageId);
        delete(queryWrapper);
    }

    /**
     * 根据存储ID修改存储信息
     * @param storage 存储信息
     */
    default void updateStorageByStorageId(MStorage storage) {
        LambdaQueryWrapper<MStorage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MStorage::getStorageId, storage.getStorageId());
        update(storage, queryWrapper);
    }

    /**
     * 根据条件查询存储信息列表（分页）
     * @param queryVO 查询条件
     * @return 存储信息列表
     */
    default Page<MStorage> selectPageStorageList(StorageQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MStorage>()
                        .likeNotNull(MStorage::getStorageName, queryVO.getStorageName())
                        .likeNotNull(MStorage::getStorageCode, queryVO.getStorageCode())
                        .eqNotNull(MStorage::getStorageType, queryVO.getStorageType())
                        .eqNotNull(MStorage::getStatus, queryVO.getStatus())
        );
    }
}
