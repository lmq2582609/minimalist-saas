package com.minimalist.basic.service;

import com.minimalist.basic.entity.po.MRole;
import com.minimalist.basic.entity.vo.role.RoleQueryVO;
import com.minimalist.basic.entity.vo.role.RoleVO;
import com.minimalist.basic.config.mybatis.bo.PageResp;

import java.util.List;

public interface RoleService {

    /**
     * 根据用户ID查询角色
     * @param userId 用户ID
     * @return 角色集合
     */
    List<RoleVO> getRolesByUserId(Long userId);

    /**
     * 根据角色ID查询角色
     * @param roleIds 角色ID集合
     * @return 角色实体集合
     */
    List<MRole> getRolesByRoles(List<Long> roleIds);

    /**
     * 添加角色
     * @param roleVO 角色数据
     */
    void addRole(RoleVO roleVO);

    /**
     * 根据角色ID删除角色
     * @param roleId 角色ID
     */
    void deleteRoleByRoleId(Long roleId);

    /**
     * 根据角色ID修改角色
     * @param roleVO 角色数据
     */
    void updateRoleByRoleId(RoleVO roleVO);

    /**
     * 查询角色(分页) -> 角色管理使用
     * @param queryVO 查询条件
     * @return 角色分页数据
     */
    PageResp<RoleVO> getPageRoleList(RoleQueryVO queryVO);

    /**
     * 根据角色ID查询角色
     * @param roleId 角色ID
     * @return 角色信息
     */
    RoleVO getRoleByRoleId(Long roleId);

    /**
     * 根据租户ID查询角色列表
     * @param tenantId 租户ID
     * @return 角色列表
     */
    List<MRole> getRoleByTenantId(Long tenantId);

}
