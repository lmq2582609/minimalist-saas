package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.vo.perm.PermQueryVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MPerms;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 权限表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MPermsMapper extends BaseMapper<MPerms> {

    /**
     * 根据权限ID获取权限（多条）
     * @param permsIds 权限ID集合
     * @return 权限集合
     */
    default List<MPerms> selectPermsByPermsIds(List<Long> permsIds) {
        return selectListByQuery(QueryWrapper.create().in(MPerms::getPermId, permsIds));
    }

    /**
     * 根据权限ID获取权限（单条）
     * @param permId 权限ID
     * @return 权限实体
     */
    default MPerms selectPermsByPermId(Long permId) {
        return selectOneByQuery(QueryWrapper.create().eq(MPerms::getPermId, permId));
    }

    /**
     * 根据权限ID修改权限
     * @param perms 权限实体
     */
    default void updatePermsByPermId(MPerms perms) {
        updateByQuery(perms, QueryWrapper.create().eq(MPerms::getPermId, perms.getPermId()));
    }

    /**
     * 根据权限ID查询直属下级数量
     * @param permId 权限ID
     * @return 下级数量
     */
    default long selectChildrenCountByPermId(Long permId) {
        return selectCountByQuery(QueryWrapper.create().eq(MPerms::getParentPermId, permId));
    }

    /**
     * 根据权限ID删除权限
     * @param permId 权限ID
     */
    default void deletePermsByPermId(Long permId) {
        deleteByQuery(QueryWrapper.create().eq(MPerms::getPermId, permId));
    }

    /**
     * 根据条件查询权限列表
     * @param queryVO 查询参数
     * @return 权限列表
     */
    default List<MPerms> selectPermList(PermQueryVO queryVO) {
        return selectListByQuery(QueryWrapper.create()
                .eq(MPerms::getStatus, queryVO.getStatus())
                .like(MPerms::getPermName, queryVO.getPermName())
                .like(MPerms::getPermType, queryVO.getPermType())
        );
    }

}
