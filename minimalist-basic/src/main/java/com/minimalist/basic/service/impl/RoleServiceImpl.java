package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.minimalist.basic.entity.enums.RoleEnum;
import com.minimalist.basic.entity.po.MRole;
import com.minimalist.basic.entity.po.MRolePerm;
import com.minimalist.basic.entity.po.MUserRole;
import com.minimalist.basic.entity.vo.role.RoleQueryVO;
import com.minimalist.basic.entity.vo.role.RoleVO;
import com.minimalist.basic.mapper.MRoleMapper;
import com.minimalist.basic.mapper.MRolePermMapper;
import com.minimalist.basic.mapper.MUserRoleMapper;
import com.minimalist.basic.service.RoleService;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.utils.UnqIdUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private MRoleMapper roleMapper;

    @Autowired
    private MUserRoleMapper userRoleMapper;

    @Autowired
    private MRolePermMapper rolePermMapper;

    /**
     * 根据用户ID查询角色
     * @param userId 用户ID
     * @return 角色集合
     */
    @Override
    public List<RoleVO> getRolesByUserId(Long userId) {
        //查询用户与角色关联关系
        List<MUserRole> userRoleRelation = userRoleMapper.selectUserRoleRelation(userId);
        if (CollectionUtil.isEmpty(userRoleRelation)) {
            return CollectionUtil.list(false);
        }
        List<Long> roleIds = userRoleRelation.stream().map(MUserRole::getRoleId).distinct().toList();
        return roleMapper.selectRoleByRoleIds(roleIds);
    }

    /**
     * 添加角色
     * @param roleVO 角色数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleVO roleVO) {
        //根据编码查询，检查是否重复添加
        MRole mRole = roleMapper.selectRoleByRoleCode(roleVO.getRoleCode());
        Assert.isNull(mRole, () -> new BusinessException(RoleEnum.ErrorMsg.EXISTS_ROLE.getDesc()));
        //角色ID
        long roleId = UnqIdUtil.uniqueId();
        mRole = BeanUtil.copyProperties(roleVO, MRole.class);
        mRole.setRoleId(roleId);
        roleMapper.insert(mRole, true);
        //插入角色和权限关联数据
        List<MRolePerm> mRolePerms = permIdToRolePerm(roleVO.getPermissionsIds(), roleId);
        rolePermMapper.insertBatch(mRolePerms);
    }

    /**
     * 根据角色ID删除角色
     * @param roleId 角色ID
     */
    @Override
    public void deleteRoleByRoleId(Long roleId) {
        //删除角色
        roleMapper.deleteRoleByRoleId(roleId);
        //删除角色与权限关联数据
        LogicDeleteManager.execWithoutLogicDelete(()->
                rolePermMapper.deleteByQuery(QueryWrapper.create().eq(MRolePerm::getRoleId, roleId))
        );
        //删除角色与用户关联数据
        LogicDeleteManager.execWithoutLogicDelete(()->
                userRoleMapper.deleteByQuery(QueryWrapper.create().eq(MUserRole::getRoleId, roleId))
        );
    }

    /**
     * 根据角色ID修改角色
     * @param roleVO 角色数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleByRoleId(RoleVO roleVO) {
        MRole newRole = BeanUtil.copyProperties(roleVO, MRole.class);
        roleMapper.updateRoleByRoleId(newRole);
        //删除原角色与权限关联信息
        LogicDeleteManager.execWithoutLogicDelete(()->
                rolePermMapper.deleteByQuery(QueryWrapper.create().eq(MRolePerm::getRoleId, roleVO.getRoleId()))
        );
        //添加新角色与权限关联信息
        List<MRolePerm> mRolePerms = permIdToRolePerm(roleVO.getPermissionsIds(), roleVO.getRoleId());
        rolePermMapper.insertBatch(mRolePerms);
    }

    /**
     * 查询角色(分页) -> 角色管理使用
     * @param queryVO 查询条件
     * @return 角色分页数据
     */
    @Override
    public PageResp<RoleVO> getPageRoleList(RoleQueryVO queryVO) {
        Page<RoleVO> roleVOPage = roleMapper.selectPageRoleList(queryVO);
        return new PageResp<>(roleVOPage.getRecords(), roleVOPage.getTotalRow());
    }

    /**
     * 根据角色ID查询角色
     * @param roleId 角色ID
     * @return 角色信息
     */
    @Override
    public RoleVO getRoleByRoleId(Long roleId) {
        MRole mRole = roleMapper.selectRoleByRoleId(roleId);
        RoleVO roleVO = BeanUtil.copyProperties(mRole, RoleVO.class);
        //根据角色查询权限，回显数据
        List<MRolePerm> rolePerms = rolePermMapper.selectRolePermByRoleId(roleId);
        List<String> permIds = rolePerms.stream().map(rp -> rp.getPermId().toString()).toList();
        roleVO.setCheckedPermIds(permIds);
        return roleVO;
    }

    /**
     * 根据租户ID查询角色列表
     * @param tenantId 租户ID
     * @return 角色列表
     */
    @Override
    public List<MRole> getRoleByTenantId(Long tenantId) {
        return roleMapper.selectListByQuery(QueryWrapper.create().eq(MRole::getTenantId, tenantId));
    }

    /**
     * 权限ID集合转换角色权限关联数据
     * @param permIds 权限ID集合
     * @param roleId 角色ID
     * @return 角色与权限关联数据
     */
    private List<MRolePerm> permIdToRolePerm(List<Long> permIds, long roleId) {
        return permIds.stream().map(p -> {
            MRolePerm rolePerm = new MRolePerm();
            rolePerm.setRoleId(roleId);
            rolePerm.setPermId(p);
            return rolePerm;
        }).collect(Collectors.toList());
    }

}
