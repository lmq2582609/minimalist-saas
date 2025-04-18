package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.vo.storage.StorageQueryVO;
import com.minimalist.basic.entity.vo.storage.StorageVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MStorage;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * 存储管理表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MStorageMapper extends BaseMapper<MStorage> {
    /**
     * 根据存储ID查询存储信息
     * @param storageId 存储ID
     * @return 存储信息
     */
    default MStorage selectStorageByStorageId(Long storageId) {
        return selectOneByQuery(QueryWrapper.create().eq(MStorage::getStorageId, storageId));
    }

    /**
     * 根据存储ID删除存储
     * @param storageId 存储ID
     */
    default void deleteStorageByStorageId(Long storageId) {
        deleteByQuery(QueryWrapper.create().eq(MStorage::getStorageId, storageId));
    }

    /**
     * 根据存储ID修改存储信息
     * @param storage 存储信息
     */
    default void updateStorageByStorageId(MStorage storage) {
        updateByQuery(storage, QueryWrapper.create().eq(MStorage::getStorageId, storage.getStorageId()));
    }

    /**
     * 根据条件查询存储信息列表（分页）
     * @param queryVO 查询条件
     * @return 存储信息列表
     */
    default Page<StorageVO> selectPageStorageList(StorageQueryVO queryVO) {
        return paginateAs(queryVO.getPageNum(), queryVO.getPageSize(),
                QueryWrapper.create()
                        .eq(MStorage::getStorageType, queryVO.getStorageType())
                        .eq(MStorage::getStatus, queryVO.getStatus())
                        .like(MStorage::getStorageName, queryVO.getStorageName()),
                StorageVO.class
        );
    }

}
