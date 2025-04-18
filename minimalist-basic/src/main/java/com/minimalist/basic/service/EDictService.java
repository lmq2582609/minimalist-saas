package com.minimalist.basic.service;

import com.minimalist.basic.entity.vo.dict.DictCacheVO;

/**
 * 额外的字典数据，这些数据不会出现在字典管理表中，因为这些数据是来源于其他表
 */
public interface EDictService {

    /**
     * 获取部门字典数据（额外字典数据）
     * @return 字典数据列表
     */
    DictCacheVO getDeptDictData();

    /**
     * 获取用户字典数据（额外字典数据）
     * @return 字典数据列表
     */
    DictCacheVO getUserDictData();

    /**
     * 获取租户套餐字典数据（额外字典数据）
     * @return 字典数据列表
     */
    DictCacheVO getTenantPackageDictData();

    /**
     * 获取角色字典数据（额外字典数据）
     * @return 字典数据列表
     */
    DictCacheVO getRoleDictData();

    /**
     * 获取岗位字典数据（额外字典数据）
     * @return 字典数据列表
     */
    DictCacheVO getPostDictData();

    /**
     * 获取租户字典数据（额外字典数据）
     * @return 字典数据列表
     */
    DictCacheVO getTenantDictData();

    /**
     * 获取存储方式数据（额外字典数据）
     * @return 字典数据列表
     */
    DictCacheVO getStorageDictData();

}
