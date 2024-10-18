package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.vo.role.RoleQueryVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MRole;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 角色表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MRoleMapper extends BaseMapper<MRole> {

    /**
     * 根据角色编码查询角色
     * @param roleCode 角色编码
     * @return 角色实体
     */
    default MRole selectRoleByRoleCode(String roleCode) {
        return selectOneByQuery(QueryWrapper.create().eq(MRole::getRoleCode, roleCode));
    }

    /**
     * 根据角色ID查询角色
     * @param roleId 角色ID
     * @return 角色实体
     */
    default MRole selectRoleByRoleId(Long roleId) {
        return selectOneByQuery(QueryWrapper.create().eq(MRole::getRoleId, roleId));
    }

    /**
     * 根据角色ID查询角色
     * @param roleId 角色ID
     * @return 角色实体
     */
    default List<MRole> selectRoleByRoleIds(List<Long> roleId) {
        return selectListByQuery(QueryWrapper.create().in(MRole::getRoleId, roleId));
    }

    /**
     * 根据角色ID删除角色
     * @param roleId 角色ID
     */
    default void deleteRoleByRoleId(Long roleId) {
        deleteByQuery(QueryWrapper.create().eq(MRole::getRoleId, roleId));
    }

    /**
     * 根据角色ID修改角色
     * @param role 角色实体
     */
    default void updateRoleByRoleId(MRole role) {
        updateByQuery(role, QueryWrapper.create().eq(MRole::getRoleId, role.getRoleId()));
    }

    /**
     * 查询角色列表(分页)
     * @param queryVO 查询条件
     * @return 角色分页数据
     */
    default Page<MRole> selectPageRoleList(RoleQueryVO queryVO) {
        return paginate(queryVO.getPageNum(), queryVO.getPageSize(),
                QueryWrapper.create()
                        .eq(MRole::getStatus, queryVO.getStatus())
                        .like(MRole::getRoleCode, queryVO.getRoleCode())
                        .like(MRole::getRoleName, queryVO.getRoleName())
        );
    }

    /**
     * 根据租户ID查询角色列表 -> 字典查询
     * @return 部门列表
     */
    default List<MRole> selectRoleDict() {
        return selectListByQuery(QueryWrapper.create().eq(MRole::getStatus, StatusEnum.STATUS_1.getCode()));
    }

}
