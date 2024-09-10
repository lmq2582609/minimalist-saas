package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.RoleEnum;
import com.minimalist.basic.entity.po.MRole;
import com.minimalist.basic.entity.po.MRolePerm;
import com.minimalist.basic.entity.po.MUserRole;
import com.minimalist.basic.entity.vo.role.RoleQueryVO;
import com.minimalist.basic.entity.vo.role.RoleVO;
import com.minimalist.basic.mapper.MRoleMapper;
import com.minimalist.basic.mapper.MUserRoleMapper;
import com.minimalist.basic.service.RoleService;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.mybatis.EntityService;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.utils.UnqIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private MRoleMapper roleMapper;

    @Autowired
    private MUserRoleMapper userRoleMapper;

    @Autowired
    private EntityService entityService;

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
        //角色ids
        List<Long> roleIds = userRoleRelation.stream().map(MUserRole::getRoleId).distinct().toList();
        //查询角色
        return BeanUtil.copyToList(roleMapper.selectRoleByRoleIds(roleIds), RoleVO.class);
    }

    /**
     * 根据角色ID查询角色
     * @param roleIds 角色ID集合
     * @return 角色实体集合
     */
    @Override
    public List<MRole> getRolesByRoles(List<Long> roleIds) {
        LambdaQueryWrapper<MRole> rQuery = new LambdaQueryWrapper<>();
        rQuery.in(MRole::getRoleId, roleIds);
        return roleMapper.selectList(rQuery);
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
        //角色ID赋值
        mRole.setRoleId(roleId);
        //角色权限赋值 - 用于回显
        mRole.setPermIds(CollectionUtil.join(roleVO.getCheckedPermIds(), ","));
        //插入角色
        int insertCount = roleMapper.insert(mRole);
        Assert.isTrue(insertCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //插入角色和权限关联数据
        List<MRolePerm> mRolePerms = permIdToRolePerm(roleVO.getPermissionsIds(), roleId);
        int insertBatchCount = entityService.insertBatch(mRolePerms);
        Assert.isTrue(insertBatchCount == mRolePerms.size(), () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 根据角色ID删除角色
     * @param roleId 角色ID
     */
    @Override
    public void deleteRoleByRoleId(Long roleId) {
        //删除角色
        int deleteCount = roleMapper.deleteRoleByRoleId(roleId);
        Assert.isTrue(deleteCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //删除角色与权限关联数据
        entityService.delete(MRolePerm::getRoleId, roleId);
        //删除角色与用户关联数据
        entityService.delete(MUserRole::getRoleId, roleId);
    }

    /**
     * 根据角色ID修改角色
     * @param roleVO 角色数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleByRoleId(RoleVO roleVO) {
        //查询角色
        MRole mRole = roleMapper.selectRoleByRoleId(roleVO.getRoleId());
        Assert.notNull(mRole, () -> new BusinessException(RoleEnum.ErrorMsg.NONENTITY_ROLE.getDesc()));
        MRole newRole = BeanUtil.copyProperties(roleVO, MRole.class);
        //乐观锁字段赋值
        newRole.updateBeforeSetVersion(mRole.getVersion());
        //角色权限赋值 - 用于回显
        newRole.setPermIds(CollectionUtil.join(roleVO.getCheckedPermIds(), ","));
        //修改角色
        int updateCount = roleMapper.updateRoleByRoleId(newRole);
        Assert.isTrue(updateCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //删除原角色与权限关联信息
        entityService.delete(MRolePerm::getRoleId, roleVO.getRoleId());
        //添加新角色与权限关联信息
        List<MRolePerm> mRolePerms = permIdToRolePerm(roleVO.getPermissionsIds(), roleVO.getRoleId());
        int insertBatchCount = entityService.insertBatch(mRolePerms);
        Assert.isTrue(insertBatchCount == mRolePerms.size(), () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 查询角色(分页) -> 角色管理使用
     * @param queryVO 查询条件
     * @return 角色分页数据
     */
    @Override
    public PageResp<RoleVO> getPageRoleList(RoleQueryVO queryVO) {
        Page<MRole> mRolePage = roleMapper.selectPageRoleList(queryVO);
        List<RoleVO> roleVOList = BeanUtil.copyToList(mRolePage.getRecords(), RoleVO.class);
        return new PageResp<>(roleVOList, mRolePage.getTotal());
    }

    /**
     * 根据角色ID查询角色
     * @param roleId 角色ID
     * @return 角色信息
     */
    @Override
    public RoleVO getRoleByRoleId(Long roleId) {
        //查询角色
        MRole mRole = roleMapper.selectRoleByRoleId(roleId);
        RoleVO roleVO = BeanUtil.copyProperties(mRole, RoleVO.class);
        //角色选中权限回显
        if (StrUtil.isNotBlank(mRole.getPermIds())) {
            roleVO.setCheckedPermIds(StrUtil.split(mRole.getPermIds(), ","));
        }
        return roleVO;
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
