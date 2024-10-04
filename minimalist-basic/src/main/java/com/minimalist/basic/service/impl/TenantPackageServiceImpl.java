package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.entity.vo.tenant.TenantPackageQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantPackageVO;
import com.minimalist.basic.manager.TenantManager;
import com.minimalist.basic.mapper.*;
import com.minimalist.basic.service.RoleService;
import com.minimalist.basic.service.TenantPackageService;
import com.minimalist.common.constant.CommonConstant;
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
public class TenantPackageServiceImpl implements TenantPackageService {

    @Autowired
    private MTenantPackageMapper tenantPackageMapper;

    @Autowired
    private MTenantPackagePermMapper tenantPackagePermMapper;

    @Autowired
    private MTenantMapper tenantMapper;

    @Autowired
    private EntityService entityService;

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
        mTenantPackage.setStatus(TenantEnum.TenantPackageStatus.TENANT_PACKAGE_STATUS_1.getCode());
        //插入租户套餐
        tenantPackageMapper.insert(mTenantPackage);
        //构造套餐与权限关联数据
        List<MTenantPackagePerm> mTenantPackagePerms = buildTenantPackagePerm(tenantPackageVO.getPermissionsIds(), tenantPackageId);
        //插入套餐与权限的关联数据
        entityService.insertBatch(mTenantPackagePerms);
    }

    /**
     * 删除租户套餐 -> 根据租户套餐ID删除租户套餐
     * @param tenantPackageId 租户套餐ID
     */
    @Override
    public void deleteTenantPackageByTenantPackageId(Long tenantPackageId) {
        Assert.isFalse(CommonConstant.ZERO == tenantPackageId, () -> new BusinessException(TenantEnum.ErrorMsg.SYSTEM_TENANT_PACKAGE.getDesc()));
        //查询租户套餐
        MTenantPackage mTenantPackage = tenantPackageMapper.selectTenantPackageByTenantPackageId(tenantPackageId);
        Assert.notNull(mTenantPackage, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT_PACKAGE.getDesc()));
        //检查租户套餐是否被使用，被使用则不能删除
        long tenantCount = tenantMapper.selectTenantCountByTenantPackageId(tenantPackageId);
        Assert.isTrue(tenantCount <= 0, () -> new BusinessException(TenantEnum.ErrorMsg.USE_TENANT_PACKAGE.getDesc()));
        //删除
        tenantPackageMapper.deleteTenantPackageByTenantPackageId(tenantPackageId);
    }

    /**
     * 修改租户套餐 -> 根据租户套餐ID修改
     * @param tenantPackageVO 租户套餐信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantPackageByTenantPackageId(TenantPackageVO tenantPackageVO) {
        //查询租户套餐
        MTenantPackage oldTenantPackage = tenantPackageMapper.selectTenantPackageByTenantPackageId(tenantPackageVO.getPackageId());
        Assert.notNull(oldTenantPackage, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT_PACKAGE.getDesc()));
        MTenantPackage newTenantPackage = BeanUtil.copyProperties(tenantPackageVO, MTenantPackage.class);
        //乐观锁字段复制
        newTenantPackage.updateBeforeSetVersion(oldTenantPackage.getVersion());
        tenantPackageMapper.updateTenantPackageByTenantPackageId(newTenantPackage);
        //删除原租户套餐与权限关联数据
        entityService.delete(MTenantPackagePerm::getPackageId, newTenantPackage.getPackageId());
        //插入新租户套餐与权限关联数据
        List<MTenantPackagePerm> mTenantPackagePerms = buildTenantPackagePerm(tenantPackageVO.getPermissionsIds(), newTenantPackage.getPackageId());
        entityService.insertBatch(mTenantPackagePerms);

        //查询套餐的权限
        List<MTenantPackagePerm> oldPerms = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(tenantPackageVO.getPackageId());
        String op = oldPerms.stream().map(p -> p.getPermId().toString()).collect(Collectors.joining(","));
        //修改后的套餐权限
        String np = tenantPackageVO.getPermissionsIds().stream().map(Object::toString).collect(Collectors.joining(","));
        //如果套餐的旧权限和修改后的新权限不一致，需要修改所有使用改套餐租户的权限
        if (!op.equals(np)) {
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
        //分页查询租户套餐
        Page<MTenantPackage> packagePage = tenantPackageMapper.selectPageTenantPackageList(queryVO);
        //数据转换
        List<TenantPackageVO> tenantPackageVOList = BeanUtil.copyToList(packagePage.getRecords(), TenantPackageVO.class);
        //返回数据时，不查询每个套餐的权限
        return new PageResp<>(tenantPackageVOList, packagePage.getTotal());
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
     * 根据租户ID查询权限
     * @param tenantId 租户ID
     * @return 权限数据列表
     */
    @Override
    public List<MPerms> getPermsByTenantId(Long tenantId) {
        //根据租户ID查询租户
        MTenant mTenant = tenantMapper.selectTenantByTenantId(tenantId);
        if (ObjectUtil.isNull(mTenant)) {
            return CollectionUtil.list(false);
        }
        //根据租户套餐查询关联的权限ID
        List<MTenantPackagePerm> mTenantPackagePermList = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(mTenant.getPackageId());
        if (CollectionUtil.isEmpty(mTenantPackagePermList)) {
            return CollectionUtil.list(false);
        }
        //权限ID集合
        List<Long> permIds = mTenantPackagePermList.stream().map(MTenantPackagePerm::getPermId).toList();
        //根据权限ID查询权限
        return permsMapper.selectPermsByPermsIds(permIds);
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
