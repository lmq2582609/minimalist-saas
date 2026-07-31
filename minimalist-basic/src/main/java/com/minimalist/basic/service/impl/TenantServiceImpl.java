package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.minimalist.basic.config.redis.RedisManager;
import com.minimalist.basic.entity.enums.RoleEnum;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.entity.vo.tenant.TenantDatasourceVO;
import com.minimalist.basic.entity.vo.tenant.TenantQueryVO;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.entity.vo.user.UserVO;
import com.minimalist.basic.manager.TenantManager;
import com.minimalist.basic.manager.UserManager;
import com.minimalist.basic.mapper.*;
import com.minimalist.basic.service.RoleService;
import com.minimalist.basic.service.TenantDbInitService;
import com.minimalist.basic.service.TenantService;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.RedisKeyConstant;
import com.minimalist.basic.utils.UnqIdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
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
    private MTenantDatasourceMapper tenantDatasourceMapper;

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

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private MUserIndexMapper userIndexMapper;

    @Autowired
    private TenantDbInitService tenantDbInitService;

    @Autowired
    private MPermsMapper permsMapper;

    /**
     * 添加租户
     * 流程：校验 → 建表 → 注册数据源 → 切租户库初始化数据 → 写主库 → 发布消息
     * @param tenantVO 租户信息
     */
    @Override
    @DSTransactional
    public void addTenant(TenantVO tenantVO) {
        //根据租户名查询租户，租户名不能重复
        checkTenantNameExists(tenantVO.getTenantName());

        //校验用户信息
        UserVO userInfo = tenantVO.getUser();
        checkAddTenantUser(userInfo);
        //校验用户名全局唯一
        MUserIndex existIndex = userIndexMapper.selectByUsername(userInfo.getUsername());
        Assert.isNull(existIndex, () -> new BusinessException("用户账号已存在，请更换账号"));

        long tenantId = UnqIdUtil.uniqueId();
        long userId = UnqIdUtil.uniqueId();
        userInfo.setUserId(userId);
        TenantDatasourceVO tenantDatasourceVO = tenantVO.getTenantDatasource();
        Assert.notNull(tenantDatasourceVO, () -> new BusinessException("租户数据源信息不能为空"));

        //① 连接目标数据库，执行建表模板SQL
        tenantDbInitService.initTenantDatabase(tenantDatasourceVO);

        //② 先动态注册数据源（必须在切换数据源之前注册）
        tenantManager.dynamicAddDatasource(String.valueOf(tenantId), tenantDatasourceVO);

        //③ 在主库上下文中查询套餐权限数据（必须在切换租户之前完成）
        List<MTenantPackagePerm> packagePerms = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(tenantVO.getPackageId());
        List<MPerms> permsList = CollectionUtil.list(false);
        if (CollectionUtil.isNotEmpty(packagePerms)) {
            List<Long> permIds = packagePerms.stream().map(MTenantPackagePerm::getPermId).toList();
            permsList = permsMapper.selectListByQuery(QueryWrapper.create().in(MPerms::getPermId, permIds));
        }

        //④ 切换到租户数据源，初始化数据
        DynamicDataSourceContextHolder.push(String.valueOf(tenantId));
        try {
            //创建租户管理员用户
            addTenantUser(userInfo, tenantId);
            //创建租户管理员角色 + 角色权限关联
            long roleId = UnqIdUtil.uniqueId();
            addTenantRole(roleId, packagePerms);
            //用户与角色关联关系
            addTenantUserRole(userId, roleId);
            //拷贝套餐权限到租户库 m_perms
            if (CollectionUtil.isNotEmpty(permsList)) {
                permsMapper.insertBatch(permsList);
            }
        } finally {
            DynamicDataSourceContextHolder.poll();
        }

        //④ 写入主库
        MTenant mTenant = BeanUtil.copyProperties(tenantVO, MTenant.class);
        mTenant.setDatasource(tenantDatasourceVO.getDatasourceName());
        mTenant.setUserId(userId);
        mTenant.setTenantId(tenantId);
        tenantMapper.insert(mTenant, true);

        //插入租户数据源连接信息
        MTenantDatasource tenantDatasource = new MTenantDatasource();
        tenantDatasource.setTenantId(tenantId);
        tenantDatasource.setDatasourceId(UnqIdUtil.uniqueId());
        tenantDatasource.setDatasourceName(tenantDatasourceVO.getDatasourceName());
        tenantDatasource.setHost(tenantDatasourceVO.getHost());
        tenantDatasource.setPort(tenantDatasourceVO.getPort());
        tenantDatasource.setUsername(tenantDatasourceVO.getUsername());
        tenantDatasource.setPassword(tenantDatasourceVO.getPassword());
        tenantDatasourceMapper.insert(tenantDatasource, true);

        //插入主库用户索引
        MUserIndex userIndex = new MUserIndex();
        userIndex.setUsername(userInfo.getUsername());
        userIndex.setTenantId(tenantId);
        userIndex.setStatus(StatusEnum.STATUS_1.getCode().intValue());
        userIndexMapper.insert(userIndex, true);

        //⑤ 发布消息 - 缓存租户信息
        tenantVO.setTenantId(tenantId);
        redisManager.publishMessage(RedisKeyConstant.TENANT_DATA_TOPIC_KEY + "." + CommonConstant.ADD, JSONUtil.toJsonStr(tenantVO));
    }

    /**
     * 删除租户 -> 根据租户ID删除
     * @param tenantId 租户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenantByTenantId(Long tenantId) {
        //删除租户数据源信息
        tenantDatasourceMapper.deleteTenantDatasourceByTenantId(tenantId);
        //发布消息 - 删除缓存中的租户信息
        redisManager.publishMessage(RedisKeyConstant.TENANT_DATA_TOPIC_KEY + "." + CommonConstant.DELETE, String.valueOf(tenantId));
        //删除租户数据
        tenantMapper.deleteTenantByTenantId(tenantId);
    }

    /**
     * 修改租户 -> 根据租户ID修改
     * @param tenantVO 租户信息
     */
    @Override
    @DSTransactional
    public void updateTenantByTenantId(TenantVO tenantVO) {
        //根据租户ID查询租户
        MTenant tenant = tenantMapper.selectTenantByTenantId(tenantVO.getTenantId());
        Assert.notNull(tenant, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT.getDesc()));
        MTenant newTenant = BeanUtil.copyProperties(tenantVO, MTenant.class);

        //更新租户数据源信息
        TenantDatasourceVO tenantDatasourceVO = tenantVO.getTenantDatasource();
        if (ObjectUtil.isNotNull(tenantDatasourceVO)) {
            //删除旧数据源信息
            tenantDatasourceMapper.deleteTenantDatasourceByTenantId(tenant.getTenantId());
            //数据源名称
            newTenant.setDatasource(tenantDatasourceVO.getDatasourceName());
            //插入新数据源
            MTenantDatasource tenantDatasource = new MTenantDatasource();
            tenantDatasource.setTenantId(tenant.getTenantId());
            tenantDatasource.setDatasourceId(UnqIdUtil.uniqueId());
            tenantDatasource.setDatasourceName(tenantDatasourceVO.getDatasourceName());
            tenantDatasource.setHost(tenantDatasourceVO.getHost());
            tenantDatasource.setPort(tenantDatasourceVO.getPort());
            tenantDatasource.setUsername(tenantDatasourceVO.getUsername());
            tenantDatasource.setPassword(tenantDatasourceVO.getPassword());
            tenantDatasourceMapper.insert(tenantDatasource, true);
        }

        //更新租户
        tenantMapper.updateTenantByTenantId(newTenant);
        //如果租户套餐变更，则修改租户权限
        if (!tenantVO.getPackageId().equals(tenant.getPackageId())) {
            //先在主库查询新套餐的权限数据
            List<MTenantPackagePerm> newPackagePerms = tenantPackagePermMapper.selectTenantPackagePermByTenantPackageId(tenantVO.getPackageId());
            List<Long> newPermIds = CollectionUtil.isNotEmpty(newPackagePerms)
                    ? newPackagePerms.stream().map(MTenantPackagePerm::getPermId).toList()
                    : CollectionUtil.list(false);
            List<MPerms> newPermsList = CollectionUtil.isNotEmpty(newPermIds)
                    ? permsMapper.selectListByQuery(QueryWrapper.create().in(MPerms::getPermId, newPermIds))
                    : CollectionUtil.list(false);

            //切换到租户数据源更新权限
            DynamicDataSourceContextHolder.push(String.valueOf(tenant.getTenantId()));
            try {
                //更新租户管理员角色的权限关联
                List<MRole> roleList = roleService.getRoleByTenantId(tenant.getTenantId());
                for (MRole role : roleList) {
                    if (RoleEnum.Role.ADMIN.getCode().equals(role.getRoleCode())) {
                        //删除旧关联
                        rolePermMapper.deleteByQuery(QueryWrapper.create().eq(MRolePerm::getRoleId, role.getRoleId()));
                        //插入新关联
                        if (CollectionUtil.isNotEmpty(newPackagePerms)) {
                            List<MRolePerm> rolePerms = newPackagePerms.stream().map(tpp -> {
                                MRolePerm rp = new MRolePerm();
                                rp.setRoleId(role.getRoleId());
                                rp.setPermId(tpp.getPermId());
                                return rp;
                            }).toList();
                            rolePermMapper.insertBatch(rolePerms);
                        }
                    }
                }
                //重新拷贝权限到租户库 m_perms
                permsMapper.deleteByQuery(QueryWrapper.create());
                if (CollectionUtil.isNotEmpty(newPermsList)) {
                    permsMapper.insertBatch(newPermsList);
                }
            } finally {
                DynamicDataSourceContextHolder.poll();
            }
        }

        //发布消息 - 缓存租户信息
        redisManager.publishMessage(RedisKeyConstant.TENANT_DATA_TOPIC_KEY + "." + CommonConstant.ADD, JSONUtil.toJsonStr(tenantVO));
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
        //跨库查询每个租户的联系人信息
        if (CollectionUtil.isNotEmpty(tenantVOPage.getRecords())) {
            for (TenantVO t : tenantVOPage.getRecords()) {
                fillTenantContactInfo(t);
            }
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
        if (ObjectUtil.isNull(mTenant)) {
            return null;
        }
        TenantVO tenantVO = BeanUtil.copyProperties(mTenant, TenantVO.class);
        //跨库查询联系人信息
        fillTenantContactInfo(tenantVO);
        //查询数据源信息
        MTenantDatasource tenantDatasource = tenantDatasourceMapper.selectTenantDatasourceByTenantId(tenantId);
        if (ObjectUtil.isNotNull(tenantDatasource)) {
            tenantVO.setTenantDatasource(BeanUtil.copyProperties(tenantDatasource, TenantDatasourceVO.class));
        }
        return tenantVO;
    }

    /**
     * 跨库查询租户联系人信息
     * 系统租户（tenant_id=0）直接查主库，其他租户切换到对应数据源查询
     * @param tenantVO 租户信息
     */
    private void fillTenantContactInfo(TenantVO tenantVO) {
        if (ObjectUtil.isNull(tenantVO.getUserId())) {
            return;
        }
        Long tid = tenantVO.getTenantId();
        MUser mUser;
        if (CommonConstant.ZERO == tid) {
            //系统租户，用户在主库
            mUser = userMapper.selectUserByUserId(tenantVO.getUserId());
        } else {
            //其他租户，切换到租户库查询
            DynamicDataSourceContextHolder.push(String.valueOf(tid));
            try {
                mUser = userMapper.selectUserByUserId(tenantVO.getUserId());
            } finally {
                DynamicDataSourceContextHolder.poll();
            }
        }
        if (ObjectUtil.isNotNull(mUser)) {
            tenantVO.setContactName(mUser.getUserRealName());
            tenantVO.setPhone(mUser.getPhone());
            tenantVO.setEmail(mUser.getEmail());
        }
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

    private void addTenantRole(Long roleId, List<MTenantPackagePerm> packagePerms) {
        MRole role = new MRole();
        role.setRoleId(roleId);
        role.setRoleName(RoleEnum.Role.ADMIN.getName());
        role.setRoleCode(RoleEnum.Role.ADMIN.getCode());
        role.setRoleSort(CommonConstant.ZERO);
        role.setRemark("系统自动创建角色");
        //插入角色（租户库）
        roleMapper.insert(role, true);
        //插入角色和权限关联数据（租户库）
        if (CollectionUtil.isNotEmpty(packagePerms)) {
            List<MRolePerm> rolePerms = packagePerms.stream().map(tpp -> {
                MRolePerm rolePerm = new MRolePerm();
                rolePerm.setRoleId(roleId);
                rolePerm.setPermId(tpp.getPermId());
                return rolePerm;
            }).toList();
            rolePermMapper.insertBatch(rolePerms);
        }
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
        userMapper.insert(user, true);
    }

    private void addTenantUserRole(Long userId, Long roleId) {
        MUserRole userRole = new MUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole, true);
    }

}
