package com.minimalist.basic.service;

import com.minimalist.basic.entity.po.MPerms;
import com.minimalist.basic.entity.vo.perm.PermQueryVO;
import com.minimalist.basic.entity.vo.perm.PermVO;

import java.util.List;

public interface PermService {

    /**
     * 根据角色ID获取权限
     * @param roleIds 角色ID集合
     * @return 权限平铺数据
     */
    List<MPerms> getPermsByRoleId(List<Long> roleIds);

    /**
     * 转换权限树
     * @param permsList 权限数据集合
     * @return 权限VO数据集合
     */
    List<PermVO> permsToTree(List<MPerms> permsList);

    /**
     * 添加权限
     * @param permVO 权限数据
     */
    void addPerm(PermVO permVO);

    /**
     * 根据权限ID删除权限
     * @param permId 权限ID
     */
    void deletePermByPermId(Long permId);

    /**
     * 根据权限ID修改权限
     * @param permVO 权限数据
     */
    void updatePermByPermId(PermVO permVO);

    /**
     * 查询权限列表（全部不分页）
     * @param queryVO 查询参数
     * @return 权限树
     */
    List<PermVO> getPermList(PermQueryVO queryVO);

    /**
     * 查询权限列表 -> 其他地方使用(只获取正常状态的权限)
     * @return 权限树
     */
    List<PermVO> getEnablePermList();

    /**
     * 根据权限ID查询权限
     * @param permId 权限ID
     * @return 权限数据
     */
    PermVO getPermByPermId(Long permId);
}
