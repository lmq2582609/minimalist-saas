package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.RoleEnum;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.enums.UserEnum;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.entity.vo.role.RoleVO;
import com.minimalist.basic.entity.vo.tenant.TenantQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.entity.vo.user.UserVO;
import com.minimalist.basic.manager.UserManager;
import com.minimalist.basic.mapper.*;
import com.minimalist.basic.service.RoleService;
import com.minimalist.basic.service.TenantService;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.utils.UnqIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private MTenantMapper tenantMapper;

    @Autowired
    private MTenantPackageMapper tenantPackageMapper;

    @Autowired
    private MUserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MTenantPackagePermMapper tenantPackagePermMapper;

    @Autowired
    private UserManager userManager;

    @Autowired
    private MUserRoleMapper userRoleMapper;

    /**
     * 添加租户
     * @param tenantVO 租户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTenant(TenantVO tenantVO) {
        //根据租户名查询租户，租户名不能重复
        checkTenantNameExists(tenantVO.getTenantName());
        //根据租户套餐ID查询租户套餐，所选择的租户套餐必须为有效套餐
        MTenantPackage tenantPackage = checkTenantPackageStatus(tenantVO.getPackageId());
        MTenant mTenant = BeanUtil.copyProperties(tenantVO, MTenant.class);
        //生成租户ID
        long tenantId = UnqIdUtil.uniqueId();
        mTenant.setTenantId(tenantId);

        //为租户创建数据
        UserVO userInfo = tenantVO.getUser();
        checkTenantUser(userInfo);

        //为租户创建角色
        long roleId = UnqIdUtil.uniqueId();
        RoleVO roleVO = new RoleVO();
        roleVO.setRoleId(roleId);
        roleVO.setRoleName(RoleEnum.TENANT_ROLE_NAME);
        roleVO.setRoleCode(RoleEnum.TENANT_ROLE_CODE);
        roleVO.setRoleSort(CommonConstant.ZERO);
        roleVO.setStatus(RoleEnum.RoleStatus.ROLE_STATUS_1.getCode());
        roleVO.setRemark("添加租户系统自动创建角色");
        roleVO.setTenantId(tenantId);
        List<String> checkedPermIds = Arrays.asList(tenantPackage.getPermIds().split(","));
        roleVO.setCheckedPermIds(checkedPermIds);
        //租户角色和权限关联关系
        List<MTenantPackagePerm> mTenantPackagePerms = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(tenantPackage.getPackageId());
        List<Long> permissionsIds = mTenantPackagePerms.stream().map(MTenantPackagePerm::getPermId).toList();
        roleVO.setPermissionsIds(permissionsIds);
        roleService.addRole(roleVO);
        //为租户创建用户
        long userId = UnqIdUtil.uniqueId();
        MUser user = new MUser();
        user.setUserId(userId);
        user.setUsername(userInfo.getUsername());
        user.setPassword(userInfo.getPassword());
        user.setNickname(userInfo.getNickname());
        user.setUserRealName(userInfo.getUserRealName());
        user.setEmail(userInfo.getEmail());
        user.setPhone(userInfo.getPhone());
        user.setUserSex(userInfo.getUserSex());
        user.setStatus(UserEnum.UserStatus.USER_STATUS_1.getCode());
        user.setTenantId(tenantId);
        user.setNickname(tenantVO.getTenantName());
        //生成盐值，密码加密
        String salt = RandomUtil.randomString(6);
        user.setSalt(salt);
        //默认密码
        user.setPassword(userManager.passwordEncrypt("123456qwerty", salt));
        userMapper.insert(user);

        //用户与角色关联关系
        MUserRole userRole = new MUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        int insertUserRoleCount = userRoleMapper.insert(userRole);
        Assert.isTrue(insertUserRoleCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //插入租户数据
        mTenant.setUserId(userId);
        int insertCount = tenantMapper.insert(mTenant);
        Assert.isTrue(insertCount > 0, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 校验租户的用户信息
     * @param user 用户信息
     */
    private void checkTenantUser(UserVO user) {
        Assert.notNull(user, () -> new BusinessException(""));



    }

    /**
     * 删除租户 -> 根据租户ID删除
     * @param tenantId 租户ID
     */
    @Override
    public void deleteTenantByTenantId(Long tenantId) {
        Assert.isFalse(CommonConstant.ZERO == tenantId, () -> new BusinessException(TenantEnum.ErrorMsg.SYSTEM_TENANT.getDesc()));
        //根据租户ID查询租户
        MTenant tenant = tenantMapper.selectTenantByTenantId(tenantId);
        Assert.notNull(tenant, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT.getDesc()));
        //删除租户
        tenantMapper.deleteTenantByTenantId(tenantId);
    }

    /**
     * 修改租户 -> 根据租户ID修改
     * @param tenantVO 租户信息
     */
    @Override
    public void updateTenantByTenantId(TenantVO tenantVO) {
        //根据租户套餐ID查询租户套餐，所选择的租户套餐必须为有效套餐
        checkTenantPackageStatus(tenantVO.getPackageId());
        //根据租户ID查询租户
        MTenant tenant = tenantMapper.selectTenantByTenantId(tenantVO.getTenantId());
        Assert.notNull(tenant, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT.getDesc()));
        MTenant newTenant = BeanUtil.copyProperties(tenantVO, MTenant.class);
        //乐观锁字段赋值
        newTenant.updateBeforeSetVersion(tenant.getVersion());
        //更新租户
        tenantMapper.updateTenantByTenantId(newTenant);
    }

    /**
     * 查询租户(分页)
     * @param queryVO 查询条件
     * @return 租户分页数据
     */
    @Override
    public PageResp<TenantVO> getPageTenantList(TenantQueryVO queryVO) {
        //查询租户分页数据
        Page<MTenant> mTenantPage = tenantMapper.selectPageTenantList(queryVO);
        //数据转换
        List<TenantVO> tenantVOList = BeanUtil.copyToList(mTenantPage.getRecords(), TenantVO.class);
        //查询租户绑定的用户信息
        if (CollectionUtil.isNotEmpty(tenantVOList)) {
            List<Long> userIdList = tenantVOList.stream().map(TenantVO::getUserId).toList();
            List<MUser> mUserList = userMapper.selectUserByUserIds(userIdList);
            Map<Long, MUser> userMap = mUserList.stream()
                    .collect(Collectors.toMap(MUser::getUserId, Function.identity(), (v1, v2) -> v1));
            tenantVOList.forEach(t -> {
                MUser user = userMap.get(t.getUserId());
                if (ObjectUtil.isNotNull(user)) {
                    t.setContactName(user.getUserRealName());
                    t.setPhone(user.getPhone());
                    t.setEmail(user.getEmail());
                }
            });
        }
        return new PageResp<>(tenantVOList, mTenantPage.getTotal());
    }

    /**
     * 根据租户ID查询租户
     * @param tenantId 租户ID
     * @return 租户数据
     */
    @Override
    public TenantVO getTenantByTenantId(Long tenantId) {
        //根据租户ID查询租户
        MTenant mTenant = tenantMapper.selectTenantByTenantId(tenantId);
        //查询与租户绑定的用户信息
        MUser mUser = userMapper.selectUserByUserId(mTenant.getUserId());
        TenantVO tenantVO = BeanUtil.copyProperties(mTenant, TenantVO.class);
        tenantVO.setContactName(mUser.getUserRealName());
        tenantVO.setPhone(mUser.getPhone());
        tenantVO.setEmail(mUser.getEmail());
        return tenantVO;
    }

    /**
     * 根据用户ID查询租户
     * @param userId 用户ID
     * @return 租户数据
     */
    @Override
    public TenantVO getTenantByUserId(Long userId) {
        //根据用户ID查询租户
        MTenant mTenant = tenantMapper.selectTenantByUserId(userId);
        return BeanUtil.copyProperties(mTenant, TenantVO.class);
    }

    /**
     * 校验租户名是否存在，存在则抛出异常
     * @param tenantName 租户名
     */
    private void checkTenantNameExists(String tenantName) {
        MTenant tenant = tenantMapper.selectTenantByTenantName(tenantName);
        Assert.isNull(tenant, () -> new BusinessException(TenantEnum.ErrorMsg.EXISTS_TENANT.getDesc()));
    }

    /**
     * 校验租户套餐是否有效，被禁用则抛出异常
     * @param tenantPackageId 租户套餐ID
     */
    private MTenantPackage checkTenantPackageStatus(Long tenantPackageId) {
        MTenantPackage mTenantPackage = tenantPackageMapper.selectTenantPackageByTenantPackageId(tenantPackageId);
        Assert.notNull(mTenantPackage, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT_PACKAGE.getDesc()));
        Assert.isFalse(TenantEnum.TenantPackageStatus.TENANT_PACKAGE_STATUS_0.getCode() == mTenantPackage.getStatus().intValue(),
                () -> new BusinessException(TenantEnum.ErrorMsg.STATUS_TENANT_PACKAGE.getDesc()));
        return mTenantPackage;
    }

}
