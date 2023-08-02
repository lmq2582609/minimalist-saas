package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.minimalist.basic.config.mybatis.QueryCondition;
import com.minimalist.basic.entity.enums.PermEnum;
import com.minimalist.basic.entity.po.MPerms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalist.basic.entity.vo.perm.PermQueryVO;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
public interface MPermsMapper extends BaseMapper<MPerms> {

    /**
     * 根据权限ID获取权限（多条）
     * @param permsIds 权限ID集合
     * @return 权限集合
     */
    default List<MPerms> selectPermsByPermsIds(List<Long> permsIds) {
        LambdaQueryWrapper<MPerms> pQuery = new LambdaQueryWrapper<>();
        pQuery.in(MPerms::getPermId, permsIds);
        return selectList(pQuery);
    }

    /**
     * 根据权限ID获取权限（单条）
     * @param permId 权限ID
     * @return 权限实体
     */
    default MPerms selectPermsByPermId(Long permId) {
        LambdaQueryWrapper<MPerms> pQuery = new LambdaQueryWrapper<>();
        pQuery.eq(MPerms::getPermId, permId);
        return selectOne(pQuery);
    }

    /**
     * 根据权限ID修改权限
     * @param perms 权限实体
     * @return 受影响行数
     */
    default int updatePermsByPermId(MPerms perms) {
        return update(perms, new LambdaUpdateWrapper<MPerms>().eq(MPerms::getPermId, perms.getPermId()));
    }

    /**
     * 根据权限ID查询直属下级数量
     * @param permId 权限ID
     * @return 下级数量
     */
    default long selectChildrenCountByPermId(Long permId) {
        LambdaQueryWrapper<MPerms> pQuery = new LambdaQueryWrapper<>();
        pQuery.eq(MPerms::getParentPermId, permId);
        return selectCount(pQuery);
    }

    /**
     * 根据权限ID删除权限
     * @param permId 权限ID
     * @return 受影响行数
     */
    default int deletePermsByPermId(Long permId) {
        LambdaQueryWrapper<MPerms> pQuery = new LambdaQueryWrapper<>();
        pQuery.eq(MPerms::getPermId, permId);
        return delete(pQuery);
    }

    /**
     * 根据条件查询权限列表
     * @param queryVO 查询参数
     * @return 权限列表
     */
    default List<MPerms> selectPermList(PermQueryVO queryVO) {
        return selectList(new QueryCondition<MPerms>()
                .likeNotNull(MPerms::getPermName, queryVO.getPermName())
                .likeNotNull(MPerms::getPermType, queryVO.getPermType())
                .eqNotNull(MPerms::getStatus, queryVO.getStatus()));
    }

    /**
     * 查询权限列表(只查询启用的权限)
     * @return 权限列表
     */
    default List<MPerms> getEnablePermList() {
        return selectList(new LambdaQueryWrapper<MPerms>()
                .eq(MPerms::getStatus, PermEnum.PermStatus.PERM_STATUS_1.getCode()));
    }

}
