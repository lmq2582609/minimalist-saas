package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.po.MUserIndex;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * 全局用户账号索引 Mapper（主库）
 */
public interface MUserIndexMapper extends BaseMapper<MUserIndex> {

    /**
     * 根据用户名查询索引
     * @param username 用户名
     * @return 用户索引
     */
    default MUserIndex selectByUsername(String username) {
        return selectOneByQuery(QueryWrapper.create().eq(MUserIndex::getUsername, username));
    }

    /**
     * 根据用户名和租户ID删除索引
     * @param username 用户名
     * @param tenantId 租户ID
     */
    default void deleteByUsernameAndTenantId(String username, Long tenantId) {
        deleteByQuery(QueryWrapper.create()
                .eq(MUserIndex::getUsername, username)
                .eq(MUserIndex::getTenantId, tenantId));
    }

    /**
     * 根据用户名和租户ID更新状态
     * @param username 用户名
     * @param tenantId 租户ID
     * @param status 状态
     */
    default void updateStatusByUsernameAndTenantId(String username, Long tenantId, Integer status) {
        MUserIndex index = new MUserIndex();
        index.setStatus(status);
        updateByQuery(index, QueryWrapper.create()
                .eq(MUserIndex::getUsername, username)
                .eq(MUserIndex::getTenantId, tenantId));
    }

}
