package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalist.basic.entity.enums.PermEnum;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.entity.vo.perm.PermQueryVO;
import com.minimalist.basic.entity.vo.perm.PermVO;
import com.minimalist.basic.mapper.*;
import com.minimalist.basic.service.PermService;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.utils.SafetyUtil;
import com.minimalist.common.utils.UnqIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PermServiceImpl implements PermService {

    @Autowired
    private MPermsMapper permsMapper;

    @Autowired
    private MRolePermMapper rolePermMapper;

    @Autowired
    private MTenantMapper tenantMapper;

    @Autowired
    private MTenantPackagePermMapper tenantPackagePermMapper;

    /**
     * 根据角色ID获取权限
     * @param roleIds 角色ID集合
     * @return 权限平铺数据
     */
    @Override
    public List<MPerms> getPermsByRoleId(List<Long> roleIds) {
        //查询角色与权限关联关系
        List<MRolePerm> rolePermRelation = rolePermMapper.selectRolePermByRoleIds(roleIds);
        if (CollectionUtil.isEmpty(rolePermRelation)) {
            return CollectionUtil.list(false);
        }
        //权限ids
        List<Long> permsIds = rolePermRelation.stream().map(MRolePerm::getPermId).distinct().toList();
        //返回权限平铺数据
        return permsMapper.selectPermsByPermsIds(permsIds);
    }

    /**
     * 转换权限树
     * @param permsList 权限数据集合
     * @return 权限数据集合
     */
    public List<PermVO> permsToTree(List<MPerms> permsList) {
        if (CollectionUtil.isEmpty(permsList)) {
            return CollectionUtil.list(false);
        }
        //查找顶级节点
        List<PermVO> rootNodeList = permsList.stream()
                .filter(p -> ObjectUtil.isNotNull(p.getParentPermId()))
                .filter(p -> CommonConstant.ZERO == p.getParentPermId())
                .map(p -> BeanUtil.copyProperties(p, PermVO.class))
                .sorted(Comparator.comparing(PermVO::getPermSort))
                .toList();
        //查找子节点
        findChildren(permsList, rootNodeList);
        return rootNodeList;
    }

    /**
     * 添加权限
     * @param permVO 权限数据
     */
    @Override
    public void addPerm(PermVO permVO) {
        MPerms mPerms = BeanUtil.copyProperties(permVO, MPerms.class);
        mPerms.setPermId(UnqIdUtil.uniqueId());
        permsMapper.insert(mPerms);
    }

    /**
     * 删除权限 -> 根据权限ID删除
     * @param permId 权限ID
     */
    @Override
    public void deletePermByPermId(Long permId) {
        //检查是否包含下级，包含下级不允许删除
        long childrenCount = permsMapper.selectChildrenCountByPermId(permId);
        Assert.isFalse(childrenCount > 0, () -> new BusinessException(PermEnum.ErrorMsg.CONTAIN_CHILDREN.getDesc()));
        int deleteCount = permsMapper.deletePermsByPermId(permId);
        Assert.isTrue(deleteCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 根据权限ID修改权限
     * @param permVO 权限数据
     */
    @Override
    public void updatePermByPermId(PermVO permVO) {
        //查询权限
        MPerms mPerms = permsMapper.selectPermsByPermId(permVO.getPermId());
        Assert.notNull(mPerms, () -> new BusinessException(PermEnum.ErrorMsg.NONENTITY_PERM.getDesc()));
        MPerms newPerms = BeanUtil.copyProperties(permVO, MPerms.class);
        //乐观锁字段赋值
        newPerms.updateBeforeSetVersion(mPerms.getVersion());
        //修改
        int updateCount = permsMapper.updatePermsByPermId(newPerms);
        Assert.isTrue(updateCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 查询权限列表（全部不分页）
     * @param queryVO 查询参数
     * @return 权限树
     */
    @Override
    public List<PermVO> getPermList(PermQueryVO queryVO) {
        return permsToTree(permsMapper.selectPermList(queryVO));
    }

    /**
     * 查询系统租户权限列表（只查询启用的权限）
     * @return 权限树
     */
    @Override
    public List<PermVO> getEnablePermList() {
        List<MPerms> enablePermList = permsMapper.getEnablePermList();
        return permsToTree(enablePermList);
    }

    /**
     * 查询租户权限列表 -> (只获取正常状态的权限)
     * @return 权限树
     */
    @Override
    public List<PermVO> getTenantEnablePermList() {
        //检查是否为系统租户
        boolean isAdmin = SafetyUtil.checkIsSystemTenant();
        //从cookie中获取租户ID，如果有值表示需要查询这个租户的数据
        String cookieTenantId = SafetyUtil.getCookieTenantId();
        if (isAdmin && StrUtil.isBlank(cookieTenantId)) {
            //如果是系统租户，并且cookie中没有tenantId，则查询perm表所有数据
            List<MPerms> enablePermList = permsMapper.getEnablePermList();
            return permsToTree(enablePermList);
        }
        //租户查询，只查询租户套餐关联的权限
        long tenantId = Optional.ofNullable(cookieTenantId)
                .map(Long::parseLong).orElse(SafetyUtil.getLonginUserTenantId());
        //查询租户
        MTenant tenant = tenantMapper.selectTenantByTenantId(tenantId);
        //查询租户套餐权限
        List<MTenantPackagePerm> tenantPackagePerms = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(tenant.getPackageId());
        List<Long> permIds = tenantPackagePerms.stream().map(MTenantPackagePerm::getPermId).toList();
        List<MPerms> enablePermList = permsMapper.selectPermsByPermsIds(permIds);
        return permsToTree(enablePermList);
    }

    /**
     * 根据权限ID查询权限
     * @param permId 权限ID
     * @return 权限数据
     */
    @Override
    public PermVO getPermByPermId(Long permId) {
        return BeanUtil.copyProperties(permsMapper.selectPermsByPermId(permId), PermVO.class);
    }

    /**
     * 查找子节点
     * @param permsList 权限数据集合
     * @param rootNodeList 权限顶级节点数据集合
     */
    private void findChildren(List<MPerms> permsList, List<PermVO> rootNodeList) {
        //遍历顶级节点
        rootNodeList.forEach(node -> {
            //存储子节点
            List<PermVO> childrenNodes = CollectionUtil.list(false);
            //从权限数据集合中查找子节点
            permsList.forEach(p -> {
                //节点是否关联
                if (node.getPermId().equals(p.getParentPermId())) {
                    childrenNodes.add(BeanUtil.copyProperties(p, PermVO.class));
                }
                //显示排序
                childrenNodes.sort(Comparator.comparing(PermVO::getPermSort));
            });
            //如果有关联的子节点
            if (CollectionUtil.isNotEmpty(childrenNodes)) {
                //将查询到的子节点挂在顶级节点上
                node.setChildren(childrenNodes);
                //对子节点继续递归，查找子节点的下级
                findChildren(permsList, childrenNodes);
            }
        });
    }

}
