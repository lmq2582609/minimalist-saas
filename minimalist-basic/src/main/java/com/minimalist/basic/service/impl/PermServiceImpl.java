package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.entity.enums.PermEnum;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.entity.vo.perm.PermQueryVO;
import com.minimalist.basic.entity.vo.perm.PermVO;
import com.minimalist.basic.mapper.*;
import com.minimalist.basic.service.PermService;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.TenantUtil;
import com.minimalist.basic.utils.UnqIdUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

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
        permsMapper.insert(mPerms, true);
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
        permsMapper.deletePermsByPermId(permId);
    }

    /**
     * 根据权限ID修改权限
     * @param permVO 权限数据
     */
    @Override
    public void updatePermByPermId(PermVO permVO) {
        MPerms newPerms = BeanUtil.copyProperties(permVO, MPerms.class);
        permsMapper.updatePermsByPermId(newPerms);
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
        QueryWrapper queryWrapper = QueryWrapper.create().eq(MPerms::getStatus, StatusEnum.STATUS_1.getCode());
        List<MPerms> enablePermList = permsMapper.selectListByQuery(queryWrapper);
        return permsToTree(enablePermList);
    }

    /**
     * 查询租户权限列表 -> (只获取正常状态的权限)
     * @return 权限树
     */
    @Override
    public List<PermVO> getTenantEnablePermList() {
        //获取要操作的租户ID
        long tenantId = TenantUtil.getTenantId();
        //如果是系统租户，查询全部
        if (CommonConstant.ZERO == tenantId) {
            return getEnablePermList();
        }
        //查询租户及套餐权限
        MTenant tenant = tenantMapper.selectTenantByTenantId(tenantId);
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
