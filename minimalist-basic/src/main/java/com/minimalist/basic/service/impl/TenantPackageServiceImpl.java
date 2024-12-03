package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.entity.vo.tenant.TenantPackageQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantPackageVO;
import com.minimalist.basic.manager.TenantManager;
import com.minimalist.basic.mapper.*;
import com.minimalist.basic.service.RoleService;
import com.minimalist.basic.service.TenantPackageService;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.UnqIdUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TenantPackageServiceImpl implements TenantPackageService {

    @Autowired
    private MTenantPackageMapper tenantPackageMapper;

    @Autowired
    private MTenantPackagePermMapper tenantPackagePermMapper;

    @Autowired
    private MTenantMapper tenantMapper;

    @Autowired
    private MPermsMapper permsMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TenantManager tenantManager;

    /**
     * 添加租户套餐
     * @param tenantPackageVO 租户套餐信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTenantPackage(TenantPackageVO tenantPackageVO) {
        MTenantPackage mTenantPackage = BeanUtil.copyProperties(tenantPackageVO, MTenantPackage.class);
        long tenantPackageId = UnqIdUtil.uniqueId();
        mTenantPackage.setPackageId(tenantPackageId);
        tenantPackageMapper.insert(mTenantPackage, true);
        //插入套餐与权限的关联数据
        List<MTenantPackagePerm> mTenantPackagePerms = buildTenantPackagePerm(tenantPackageVO.getPermissionsIds(), tenantPackageId);
        tenantPackagePermMapper.insertBatch(mTenantPackagePerms);
    }

    /**
     * 删除租户套餐 -> 根据租户套餐ID删除租户套餐
     * @param tenantPackageId 租户套餐ID
     */
    @Override
    public void deleteTenantPackageByTenantPackageId(Long tenantPackageId) {
        //检查租户套餐是否被使用，被使用则不能删除
        long tenantCount = tenantMapper.selectTenantCountByTenantPackageId(tenantPackageId);
        Assert.isTrue(tenantCount <= 0, () -> new BusinessException(TenantEnum.ErrorMsg.USE_TENANT_PACKAGE.getDesc()));
        //删除租户套餐
        tenantPackageMapper.deleteTenantPackageByTenantPackageId(tenantPackageId);
        //删除套餐与权限关联数据
        tenantPackagePermMapper.deleteByQuery(QueryWrapper.create().eq(MTenantPackagePerm::getPackageId, tenantPackageId));
    }

    /**
     * 修改租户套餐 -> 根据租户套餐ID修改
     * @param tenantPackageVO 租户套餐信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantPackageByTenantPackageId(TenantPackageVO tenantPackageVO) {
        //修改租户套餐数据
        MTenantPackage newTenantPackage = BeanUtil.copyProperties(tenantPackageVO, MTenantPackage.class);
        tenantPackageMapper.updateTenantPackageByTenantPackageId(newTenantPackage);
        //查询租户套餐的权限 - 修改前的权限
        List<MTenantPackagePerm> oldPerms = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(tenantPackageVO.getPackageId());
        String op = oldPerms.stream().map(p -> p.getPermId().toString()).collect(Collectors.joining(","));
        //修改后的套餐权限
        String np = tenantPackageVO.getPermissionsIds().stream().map(Object::toString).collect(Collectors.joining(","));
        //如果套餐的旧权限和修改后的新权限不一致，需要修改所有使用改套餐租户的权限
        if (!op.equals(np)) {
            //套餐的权限修改
            //1. 删除原套餐与权限关联数据
            LogicDeleteManager.execWithoutLogicDelete(()->
                    tenantPackagePermMapper.deleteByQuery(QueryWrapper.create().eq(MTenantPackagePerm::getPackageId, newTenantPackage.getPackageId()))
            );
            //2. 插入新套餐与权限关联数据
            List<MTenantPackagePerm> mTenantPackagePerms = buildTenantPackagePerm(tenantPackageVO.getPermissionsIds(), newTenantPackage.getPackageId());
            tenantPackagePermMapper.insertBatch(mTenantPackagePerms);
            //根据套餐查租户
            List<MTenant> tenants = tenantMapper.selectTenantByTenantPackageId(tenantPackageVO.getPackageId());
            for (MTenant tenant : tenants) {
                //查询租户下所有角色
                List<MRole> roleList = roleService.getRoleByTenantId(tenant.getTenantId());
                //修改租户权限
                tenantManager.updateTenantPermission(roleList, tenantPackageVO.getPackageId());
            }
        }
    }

    /**
     * 查询租户套餐(分页)
     * @param queryVO 查询条件
     * @return 租户套餐分页数据
     */
    @Override
    public PageResp<TenantPackageVO> getPageTenantPackageList(TenantPackageQueryVO queryVO) {
        Page<TenantPackageVO> tenantPackageVOPage = tenantPackageMapper.selectPageTenantPackageList(queryVO);
        return new PageResp<>(tenantPackageVOPage.getRecords(), tenantPackageVOPage.getTotalRow());
    }

    /**
     * 根据租户套餐ID查询租户套餐
     * @param tenantPackageId 租户套餐ID
     * @return 租户套餐数据
     */
    @Override
    public TenantPackageVO getTenantPackageByTenantPackageId(Long tenantPackageId) {
        //根据租户ID查询租户套餐
        MTenantPackage mTenantPackage = tenantPackageMapper.selectTenantPackageByTenantPackageId(tenantPackageId);
        TenantPackageVO tenantPackageVO = BeanUtil.copyProperties(mTenantPackage, TenantPackageVO.class);
        //套餐选中权限回显
        List<MTenantPackagePerm> tenantPackagePerms = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(tenantPackageId);
        List<String> permIds = tenantPackagePerms.stream().map(p -> p.getPermId().toString()).toList();
        tenantPackageVO.setCheckedPermIds(permIds);
        return tenantPackageVO;
    }

    /**
     * 构建租户套餐与权限关联数据
     * @param permissionsIds 权限ID集合
     * @param tenantPackageId 租户ID
     * @return 租户套餐与权限关联数据
     */
    private List<MTenantPackagePerm> buildTenantPackagePerm(List<Long> permissionsIds, Long tenantPackageId) {
        return permissionsIds.stream().map(permId -> {
            MTenantPackagePerm tpp = new MTenantPackagePerm();
            tpp.setPackageId(tenantPackageId);
            tpp.setPermId(permId);
            return tpp;
        }).toList();
    }

}
