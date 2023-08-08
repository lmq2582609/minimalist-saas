package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.RoleEnum;
import com.minimalist.basic.entity.po.MRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minimalist.basic.entity.vo.role.RoleQueryVO;
import com.minimalist.common.mybatis.QueryCondition;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-05-04
 */
public interface MRoleMapper extends BaseMapper<MRole> {

    /**
     * 根据角色编码查询角色
     * @param roleCode 角色编码
     * @return 角色实体
     */
    default MRole selectRoleByRoleCode(String roleCode) {
        return selectOne(new LambdaQueryWrapper<MRole>().eq(MRole::getRoleCode, roleCode));
    }

    /**
     * 根据角色ID查询角色
     * @param roleId 角色ID
     * @return 角色实体
     */
    default MRole selectRoleByRoleId(Long roleId) {
        return selectOne(new LambdaQueryWrapper<MRole>().eq(MRole::getRoleId, roleId));
    }

    /**
     * 根据角色ID查询角色
     * @param roleId 角色ID
     * @return 角色实体
     */
    default List<MRole> selectRoleByRoleIds(List<Long> roleId) {
        return selectList(new LambdaQueryWrapper<MRole>().in(MRole::getRoleId, roleId));
    }

    /**
     * 根据角色ID删除角色
     * @param roleId 角色ID
     * @return 受影响行数
     */
    default int deleteRoleByRoleId(Long roleId) {
        return delete(new LambdaQueryWrapper<MRole>().eq(MRole::getRoleId, roleId));
    }

    /**
     * 根据角色ID修改角色
     * @param role 角色实体
     * @return 受影响行数
     */
    default int updateRoleByRoleId(MRole role) {
        return update(role, new LambdaUpdateWrapper<MRole>().eq(MRole::getRoleId, role.getRoleId()));
    }

    /**
     * 查询角色列表(分页)
     * @param queryVO 查询条件
     * @return 角色分页数据
     */
    default Page<MRole> selectPageRoleList(RoleQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MRole>()
                        .likeNotNull(MRole::getRoleName, queryVO.getRoleName())
                        .likeNotNull(MRole::getRoleCode, queryVO.getRoleCode())
                        .eqNotNull(MRole::getStatus, queryVO.getStatus()));
    }

    /**
     * 根据租户ID查询角色列表 -> 字典查询
     * @return 部门列表
     */
    default List<MRole> selectRoleDict() {
        LambdaQueryWrapper<MRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(MRole::getRoleId, MRole::getRoleName);
        queryWrapper.eq(MRole::getStatus, RoleEnum.RoleStatus.ROLE_STATUS_1.getCode());
        return selectList(queryWrapper);
    }

}
