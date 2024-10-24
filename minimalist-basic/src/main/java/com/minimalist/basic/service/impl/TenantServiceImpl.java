package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.minimalist.basic.entity.enums.RoleEnum;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.entity.vo.tenant.TenantQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.entity.vo.user.UserVO;
import com.minimalist.basic.manager.TenantManager;
import com.minimalist.basic.manager.UserManager;
import com.minimalist.basic.mapper.*;
import com.minimalist.basic.service.RoleService;
import com.minimalist.basic.service.TenantService;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.UnqIdUtil;
import com.mybatisflex.core.paginate.Page;
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
    private MRoleMapper roleMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MTenantPackagePermMapper tenantPackagePermMapper;

    @Autowired
    private UserManager userManager;

    @Autowired
    private MUserRoleMapper userRoleMapper;

    @Autowired
    private TenantManager tenantManager;

    @Autowired
    private MRolePermMapper rolePermMapper;

    /**
     * 添加租户
     * @param tenantVO 租户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTenant(TenantVO tenantVO) {
        //根据租户名查询租户，租户名不能重复
        checkTenantNameExists(tenantVO.getTenantName());
        MTenant mTenant = BeanUtil.copyProperties(tenantVO, MTenant.class);
        long tenantId = UnqIdUtil.uniqueId();

        //为租户创建用户
        UserVO userInfo = tenantVO.getUser();
        checkAddTenantUser(userInfo);
        long userId = UnqIdUtil.uniqueId();
        userInfo.setUserId(userId);
        addTenantUser(userInfo, tenantId);

        //为租户创建角色
        long roleId = UnqIdUtil.uniqueId();
        addTenantRole(roleId, tenantId, tenantVO.getPackageId());
        //用户与角色关联关系
        addTenantUserRole(userId, roleId);

        //插入租户数据
        mTenant.setUserId(userId);
        mTenant.setTenantId(tenantId);
        tenantMapper.insert(mTenant);
    }

    /**
     * 删除租户 -> 根据租户ID删除
     * @param tenantId 租户ID
     */
    @Override
    public void deleteTenantByTenantId(Long tenantId) {
        tenantMapper.deleteTenantByTenantId(tenantId);
    }

    /**
     * 修改租户 -> 根据租户ID修改
     * @param tenantVO 租户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantByTenantId(TenantVO tenantVO) {
        //根据租户ID查询租户
        MTenant tenant = tenantMapper.selectTenantByTenantId(tenantVO.getTenantId());
        Assert.notNull(tenant, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT.getDesc()));
        MTenant newTenant = BeanUtil.copyProperties(tenantVO, MTenant.class);
        //更新租户
        tenantMapper.updateTenantByTenantId(newTenant);
        //如果租户套餐变更，则修改租户套餐
        if (!tenantVO.getPackageId().equals(tenant.getPackageId())) {
            //查询租户下所有角色
            List<MRole> roleList = roleService.getRoleByTenantId(tenant.getTenantId());
            //修改租户权限
            tenantManager.updateTenantPermission(roleList, tenantVO.getPackageId());
        }
    }

    /**
     * 查询租户(分页)
     * @param queryVO 查询条件
     * @return 租户分页数据
     */
    @Override
    public PageResp<TenantVO> getPageTenantList(TenantQueryVO queryVO) {
        //查询租户分页数据
        Page<TenantVO> tenantVOPage = tenantMapper.selectPageTenantList(queryVO);
        //查询租户绑定的用户信息
        if (CollectionUtil.isNotEmpty(tenantVOPage.getRecords())) {
            List<Long> userIdList = tenantVOPage.getRecords().stream().map(TenantVO::getUserId).toList();
            List<MUser> mUserList = userMapper.selectUserByUserIds(userIdList);
            Map<Long, MUser> userMap = mUserList.stream()
                    .collect(Collectors.toMap(MUser::getUserId, Function.identity(), (v1, v2) -> v1));
            tenantVOPage.getRecords().forEach(t -> {
                MUser user = userMap.get(t.getUserId());
                if (ObjectUtil.isNotNull(user)) {
                    t.setContactName(user.getUserRealName());
                    t.setPhone(user.getPhone());
                    t.setEmail(user.getEmail());
                }
            });
        }
        return new PageResp<>(tenantVOPage.getRecords(), tenantVOPage.getTotalRow());
    }

    /**
     * 根据租户ID查询租户
     * @param tenantId 租户ID
     * @return 租户数据
     */
    @Override
    public TenantVO getTenantByTenantId(Long tenantId) {
        MTenant mTenant = tenantMapper.selectTenantByTenantId(tenantId);
        MUser mUser = userMapper.selectUserByUserId(mTenant.getUserId());
        TenantVO tenantVO = BeanUtil.copyProperties(mTenant, TenantVO.class);
        tenantVO.setContactName(mUser.getUserRealName());
        tenantVO.setPhone(mUser.getPhone());
        tenantVO.setEmail(mUser.getEmail());
        return tenantVO;
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
     * 校验租户的用户信息
     * @param user 用户信息
     */
    private void checkAddTenantUser(UserVO user) {
        Assert.notNull(user, () -> new BusinessException(TenantEnum.ErrorMsg.ADD_TENANT_USER_NULL.getDesc()));
        Assert.notBlank(user.getUsername(), () -> new BusinessException(TenantEnum.ErrorMsg.ADD_TENANT_USERNAME_NULL.getDesc()));
        Assert.notBlank(user.getPassword(), () -> new BusinessException(TenantEnum.ErrorMsg.ADD_TENANT_PASSWORD_NULL.getDesc()));
        Assert.notBlank(user.getNickname(), () -> new BusinessException(TenantEnum.ErrorMsg.ADD_TENANT_NICKNAME_NULL.getDesc()));
        Assert.notBlank(user.getUserRealName(), () -> new BusinessException(TenantEnum.ErrorMsg.ADD_TENANT_REALNAME_NULL.getDesc()));
        Assert.notBlank(user.getPhone(), () -> new BusinessException(TenantEnum.ErrorMsg.ADD_TENANT_PHONE_NULL.getDesc()));
        Assert.notNull(user.getUserSex(), () -> new BusinessException(TenantEnum.ErrorMsg.ADD_TENANT_USERSEX_NULL.getDesc()));
    }

    private void addTenantRole(Long roleId, Long tenantId, Long tenantPackageId) {
        MRole role = new MRole();
        role.setRoleId(roleId);
        role.setRoleName(RoleEnum.Role.ADMIN.getName());
        role.setRoleCode(RoleEnum.Role.ADMIN.getCode());
        role.setRoleSort(CommonConstant.ZERO);
        role.setRemark("系统自动创建角色");
        role.setTenantId(tenantId);
        //插入角色
        roleMapper.insert(role);
        //插入角色和权限关联数据
        List<MTenantPackagePerm> mTenantPackagePerms = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(tenantPackageId);
        List<MRolePerm> rolePerms = mTenantPackagePerms.stream().map(tpp -> {
            MRolePerm rolePerm = new MRolePerm();
            rolePerm.setRoleId(roleId);
            rolePerm.setPermId(tpp.getPermId());
            return rolePerm;
        }).toList();
        rolePermMapper.insertBatch(rolePerms);

    }

    private void addTenantUser(UserVO userInfo, Long tenantId) {
        MUser user = new MUser();
        user.setUserId(userInfo.getUserId());
        user.setUsername(userInfo.getUsername());
        user.setNickname(userInfo.getNickname());
        user.setUserRealName(userInfo.getUserRealName());
        user.setEmail(userInfo.getEmail());
        user.setPhone(userInfo.getPhone());
        user.setUserSex(userInfo.getUserSex());
        //生成盐值，密码加密
        String salt = RandomUtil.randomString(6);
        user.setSalt(salt);
        user.setPassword(userManager.passwordEncrypt(userInfo.getPassword(), salt));
        user.setTenantId(tenantId);
        userMapper.insert(user);
    }

    private void addTenantUserRole(Long userId, Long roleId) {
        MUserRole userRole = new MUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);
    }

}
