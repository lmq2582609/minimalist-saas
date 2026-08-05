package com.minimalist.basic.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.minimalist.basic.entity.enums.*;
import com.minimalist.basic.entity.po.MPerms;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.entity.po.MUserDept;
import com.minimalist.basic.entity.po.MUserIndex;
import com.minimalist.basic.entity.po.MUserPost;
import com.minimalist.basic.entity.po.MUserRole;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.basic.entity.vo.perm.PermVO;
import com.minimalist.basic.entity.vo.role.RoleVO;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.entity.vo.user.ImageCaptchaVO;
import com.minimalist.basic.entity.vo.user.RePasswordVO;
import com.minimalist.basic.entity.vo.user.UserInfoVO;
import com.minimalist.basic.entity.vo.user.UserLoginReqVO;
import com.minimalist.basic.entity.vo.user.UserQueryVO;
import com.minimalist.basic.entity.vo.user.UserSettingVO;
import com.minimalist.basic.entity.vo.user.UserVO;
import com.minimalist.basic.manager.TenantManager;
import com.minimalist.basic.manager.UserManager;
import com.minimalist.basic.mapper.MUserDeptMapper;
import com.minimalist.basic.mapper.MUserIndexMapper;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.basic.mapper.MUserPostMapper;
import com.minimalist.basic.mapper.MUserRoleMapper;
import com.minimalist.basic.service.*;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.config.redis.RedisManager;
import com.minimalist.basic.config.tenant.TenantIgnore;
import com.minimalist.basic.utils.*;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MUserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MUserRoleMapper userRoleMapper;

    @Autowired
    private MUserDeptMapper userDeptMapper;

    @Autowired
    private PermService permService;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private MUserPostMapper userPostMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private UserManager userManager;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantManager tenantManager;

    @Autowired
    private ConfigService configService;

    @Autowired
    private FuncConfigService funcConfigService;

    @Autowired
    private MUserIndexMapper userIndexMapper;

    /**
     * 新增用户
     * @param userVO 用户实体
     */
    @Override
    @DSTransactional
    public void addUser(UserVO userVO) {
        //校验用户名全局唯一（查主库 m_user_index）
        MUserIndex existIndex = userIndexMapper.selectByUsername(userVO.getUsername());
        Assert.isNull(existIndex, () -> new BusinessException("用户账号已存在，请更换账号"));
        //校验租户的套餐是否满足条件
        tenantManager.checkTenantPackage(TenantUtil.getTenantId());
        //新增用户数据
        MUser user = BeanUtil.copyProperties(userVO, MUser.class);
        long userId = UnqIdUtil.uniqueId();
        user.setUserId(userId);
        //生成盐值，密码加密
        String salt = RandomUtil.randomString(6);
        user.setSalt(salt);
        if (StrUtil.isNotBlank(userVO.getPassword())) {
            user.setPassword(userManager.passwordEncrypt(userVO.getPassword(), salt));
        } else {
            //设置默认密码 123456qwerty
            user.setPassword(userManager.passwordEncrypt("123456qwerty", salt));
        }
        userMapper.insert(user, true);
        //新增用户关联信息
        userManager.insertUserRelation(userVO.getRoleIds(), userVO.getPostIds(), userVO.getDeptIds(), userId);

        //同步主库用户索引
        Long tenantId = TenantUtil.getTenantId();
        DynamicDataSourceContextHolder.push("master");
        try {
            MUserIndex userIndex = new MUserIndex();
            userIndex.setUsername(userVO.getUsername());
            userIndex.setTenantId(tenantId);
            userIndex.setStatus(StatusEnum.STATUS_1.getCode().intValue());
            userIndexMapper.insert(userIndex, true);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @Override
    @DSTransactional
    public void deleteUserByUserId(Long userId) {
        //先查询用户信息（获取username用于删除索引）
        MUser user = userMapper.selectUserByUserId(userId);
        //删除用户
        userMapper.deleteUserByUserId(userId);
        //删除用户关联信息
        userManager.deleteUserRelation(userId);

        //同步删除主库用户索引
        if (ObjectUtil.isNotNull(user)) {
            Long tenantId = TenantUtil.getTenantId();
            DynamicDataSourceContextHolder.push("master");
            try {
                userIndexMapper.deleteByUsernameAndTenantId(user.getUsername(), tenantId);
            } finally {
                DynamicDataSourceContextHolder.poll();
            }
        }
    }

    /**
     * 修改用户
     * @param userVO 用户数据
     */
    @Override
    @DSTransactional
    public void updateUserByUserId(UserVO userVO) {
        //校验该租户套餐是否满足条件
        tenantManager.checkTenantPackage(TenantUtil.getTenantId());
        //查询用户信息
        MUser oldUser = userMapper.selectUserByUserId(userVO.getUserId());
        //修改用户信息
        MUser newUser = BeanUtil.copyProperties(userVO, MUser.class);
        //是否需要修改密码
        if (StrUtil.isNotBlank(userVO.getPassword())) {
            //密码加密
            newUser.setPassword(userManager.passwordEncrypt(userVO.getPassword(), oldUser.getSalt()));
        }
        //修改用户
        userMapper.updateUserByUserId(newUser);

        //功能开关判断 - 关联数据保护
        Map<String, String> funcConfigMap = funcConfigService.getFuncConfigMap();
        boolean postEnabled = Boolean.parseBoolean(funcConfigMap.getOrDefault("feature.post.enable", "true"));
        boolean deptEnabled = Boolean.parseBoolean(funcConfigMap.getOrDefault("feature.dept.enable", "true"));

        //删除用户关联信息 - 功能关闭时不触碰对应关联
        if (postEnabled || deptEnabled) {
            //至少有一个功能开启，才需要处理关联
            if (postEnabled) {
                LogicDeleteManager.execWithoutLogicDelete(()->
                        userPostMapper.deleteByQuery(QueryWrapper.create().eq(MUserPost::getUserId, userVO.getUserId()))
                );
            }
            if (deptEnabled) {
                LogicDeleteManager.execWithoutLogicDelete(()->
                        userDeptMapper.deleteByQuery(QueryWrapper.create().eq(MUserDept::getUserId, userVO.getUserId()))
                );
            }
        }
        //角色关联始终处理
        LogicDeleteManager.execWithoutLogicDelete(()->
                userRoleMapper.deleteByQuery(QueryWrapper.create().eq(MUserRole::getUserId, userVO.getUserId()))
        );
        //新增用户关联信息
        userManager.insertUserRelation(userVO.getRoleIds(),
                postEnabled ? userVO.getPostIds() : null,
                deptEnabled ? userVO.getDeptIds() : null,
                userVO.getUserId());

        //如果用户状态变更，同步主库索引状态
        if (ObjectUtil.isNotNull(userVO.getStatus()) && !userVO.getStatus().equals(oldUser.getStatus())) {
            Long tenantId = TenantUtil.getTenantId();
            DynamicDataSourceContextHolder.push("master");
            try {
                userIndexMapper.updateStatusByUsernameAndTenantId(oldUser.getUsername(), tenantId, userVO.getStatus().intValue());
            } finally {
                DynamicDataSourceContextHolder.poll();
            }
        }
    }

    /**
     * 查询用户(分页)
     * @param queryVO 查询条件
     * @return 用户分页数据
     */
    @Override
    public PageResp<UserVO> getPageUserList(UserQueryVO queryVO) {
        Page<UserVO> userPage = userMapper.selectPageUserList(queryVO);
        return new PageResp<>(userPage.getRecords(), userPage.getTotalRow());
    }

    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public UserVO getUserByUserId(Long userId) {
        MUser mUser = userMapper.selectUserByUserId(userId);
        //拷贝数据
        UserVO userVO = BeanUtil.copyProperties(mUser, UserVO.class);
        //查询用户与岗位关联数据
        List<MUserPost> userPostList = userPostMapper.selectUserPostRelation(userVO.getUserId());
        userVO.setPostIds(userPostList.stream().map(MUserPost::getPostId).collect(Collectors.toSet()));
        //查询用户与角色关联数据
        List<MUserRole> userRoleList = userRoleMapper.selectUserRoleRelation(userVO.getUserId());
        userVO.setRoleIds(userRoleList.stream().map(MUserRole::getRoleId).collect(Collectors.toSet()));
        //查询用户与部门关联数据
        List<MUserDept> userDeptList = userDeptMapper.selectUserDeptRelation(userVO.getUserId());
        //部门选中回显
        List<String> deptIds = userDeptList.stream().map(d -> d.getDeptId().toString()).toList();
        userVO.setCheckedDeptIds(deptIds);
        return userVO;
    }

    /**
     * 获取用户信息
     */
    @Override
    public UserInfoVO getUserInfo() {
        //获取当前登陆人的userId
        Long userId = StpUtil.getLoginIdAsLong();
        //如果多租户开启 && 当前登陆人是系统租户 && 要查询其他租户数据
        if (TenantUtil.checkTenantOnOff() && TenantUtil.checkIsSystemTenant() && TenantUtil.checkQueryTenantData()) {
            //获取当前操作的租户信息，可能涉及租户切换
            TenantVO tenantVO = CommonConstant.tenantMap.get(TenantUtil.getTenantId());
            if (ObjectUtil.isNull(tenantVO)) {
                throw new BusinessException("获取租户信息为空，请检查");
            }
            //取当前操作租户的用户ID，切换为该租户的管理员身份
            userId = tenantVO.getUserId();
        }
        //查询用户
        MUser user = userMapper.selectUserByUserId(userId);
        if (ObjectUtil.isNull(user)) {
            return new UserInfoVO();
        }
        UserInfoVO userInfoVO = BeanUtil.copyProperties(user, UserInfoVO.class);
        //根据用户ID查询角色
        List<RoleVO> roles = roleService.getRolesByUserId(userId);
        //角色不为空，根据角色处理权限信息
        if (CollectionUtil.isNotEmpty(roles)) {
            //存放角色标识符
            Set<String> roleCodes = CollectionUtil.set(false);
            //汇总角色ID
            List<Long> roleIds = roles.stream()
                    //状态 = 正常
                    .filter(r -> StatusEnum.STATUS_1.getCode().equals(r.getStatus()))
                    .map(r -> {
                        //角色标识符
                        roleCodes.add(r.getRoleCode());
                        //返回角色ID
                        return r.getRoleId();
                    }).distinct().toList();
            //存放菜单数据
            List<MPerms> menuList = CollectionUtil.list(false);
            //根据角色ID查询权限 - 返回权限平铺数据
            List<MPerms> permList = permService.getPermsByRoleId(roleIds);
            //汇总权限标识符集合
            Set<String> permCodes = permList.stream()
                    //状态 = 正常
                    .filter(p -> StatusEnum.STATUS_1.getCode().equals(p.getStatus()))
                    .map(p -> {
                        //如果是菜单，存储到菜单集合
                        if (PermEnum.PermType.MENU.getCode().equals(p.getPermType())) {
                            menuList.add(p);
                        }
                        //返回权限编码
                        return p.getPermCode();
                    })
                    .filter(StrUtil::isNotBlank).collect(Collectors.toSet());
            //将角色标识符存入用户实体
            userInfoVO.setRoles(roleCodes);
            //将权限标识符存入用户实体
            userInfoVO.setPerms(permCodes);
            //将菜单存入用户实体
            userInfoVO.setMenus(permService.permsToTree(menuList));
            //将权限数据向redis存储一份
            redisManager.set(StrUtil.indexedFormat(RedisKeyConstant.USER_ROLE_CACHE_KEY, userId), roleCodes, RedisKeyConstant.USER_PERM_CACHE_EX);
            redisManager.set(StrUtil.indexedFormat(RedisKeyConstant.USER_PERM_CACHE_KEY, userId), permCodes, RedisKeyConstant.USER_PERM_CACHE_EX);
        }
        //读取功能开关配置
        Map<String, String> funcConfigMap = funcConfigService.getFuncConfigMap();
        boolean postEnabled = Boolean.parseBoolean(funcConfigMap.getOrDefault("feature.post.enable", "true"));
        boolean deptEnabled = Boolean.parseBoolean(funcConfigMap.getOrDefault("feature.dept.enable", "true"));
        //系统租户（未切换租户）：功能关闭时从菜单树中过滤掉对应菜单（因为系统租户不修改m_perms.visible，避免通过套餐同步影响所有租户）
        boolean isSystemTenant = TenantUtil.checkTenantOnOff() && TenantUtil.checkIsSystemTenant() && !TenantUtil.checkQueryTenantData();
        if (isSystemTenant && userInfoVO.getMenus() != null) {
            if (!deptEnabled) {
                filterMenuByConfigKey(userInfoVO.getMenus(), "dept_enable_params");
            }
            if (!postEnabled) {
                filterMenuByConfigKey(userInfoVO.getMenus(), "post_enable_params");
            }
        }
        //用户岗位
        if (postEnabled) {
            userInfoVO.setPostList(postService.getPostByUserId(userId));
        } else {
            userInfoVO.setPostList(List.of());
        }
        //用户所属部门
        if (deptEnabled) {
            List<MUserDept> userDeptList = userDeptMapper.selectUserDeptRelation(userId);
            List<Long> deptIds = userDeptList.stream().map(MUserDept::getDeptId).toList();
            userInfoVO.setDeptList(deptService.getDeptByDeptIds(deptIds));
        } else {
            userInfoVO.setDeptList(List.of());
        }
        //设置功能配置到返回对象
        userInfoVO.setFuncConfigs(funcConfigMap);
        return userInfoVO;
    }

    /**
     * 根据主库m_config中的菜单匹配参数，从菜单树中过滤掉对应菜单
     * @param menus 菜单树
     * @param paramsKey 菜单匹配参数key（如 dept_enable_params）
     */
    private void filterMenuByConfigKey(List<PermVO> menus, String paramsKey) {
        ConfigVO paramsConfig = configService.getConfigByConfigKey(paramsKey);
        if (paramsConfig == null) return;
        JSONObject json = JSONUtil.parseObj(paramsConfig.getConfigValue());
        String permName = json.getStr("perm_name");
        String permPath = json.getStr("perm_path");
        removeMenuFromTree(menus, permName, permPath);
    }

    /**
     * 递归遍历菜单树，移除匹配的菜单节点
     */
    private void removeMenuFromTree(List<PermVO> menus, String permName, String permPath) {
        Iterator<PermVO> iterator = menus.iterator();
        while (iterator.hasNext()) {
            PermVO menu = iterator.next();
            if (permName.equals(menu.getPermName()) && permPath.equals(menu.getPermPath())) {
                iterator.remove();
            } else if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                removeMenuFromTree(menu.getChildren(), permName, permPath);
            }
        }
    }

    /**
     * 获取图形验证码
     * @return 图形验证码
     */
    @Override
    public ImageCaptchaVO getImageCaptcha() {
        ConfigVO config = configService.getConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_CAPTCHA_ENABLE);
        boolean loginCaptchaEnable = Boolean.parseBoolean(config.getConfigValue());
        ImageCaptchaVO imageCaptchaVO = new ImageCaptchaVO();
        imageCaptchaVO.setEnable(loginCaptchaEnable);
        if (!loginCaptchaEnable) {
            return imageCaptchaVO;
        }
        //验证码4个随机字符
        CircleCaptcha circleCaptcha = new CircleCaptcha(280, 100, 4, 25);
        //验证码转小写
        String captcha = circleCaptcha.getCode().toLowerCase();
        //为验证码生成对应的ID，1个验证码对应1个ID
        String captchaId = IdUtil.objectId().toLowerCase();
        //redis验证码的key
        String key = StrUtil.indexedFormat(RedisKeyConstant.CAPTCHA_CACHE_KEY, captchaId);
        //存入redis，value是验证码
        redisManager.set(key, captcha, RedisKeyConstant.CAPTCHA_CACHE_EX);
        //构建图形验证码
        imageCaptchaVO.setCaptchaId(captchaId);
        imageCaptchaVO.setCaptchaImg(circleCaptcha.getImageBase64());
        return imageCaptchaVO;
    }

    /**
     * 校验图形验证码
     * @param captcha 验证码
     * @param captchaId 验证码ID
     * @return true通过校验  false未通过校验
     */
    @Override
    public boolean checkImageCaptcha(String captcha, String captchaId) {
        String key = StrUtil.indexedFormat(RedisKeyConstant.CAPTCHA_CACHE_KEY, captchaId.toLowerCase());
        String captchaCache = redisManager.getAndDelete(key);
        return captcha.toLowerCase().equals(captchaCache);
    }

    /**
     * 用户登录
     * 流程：查主库索引路由 → 切租户库验密 → 校验租户状态 → 登录
     * @param reqVO 用户登录信息
     * @return token
     */
    @Override
    public SaTokenInfo userLogin(UserLoginReqVO reqVO) {
        ConfigVO config = configService.getConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_CAPTCHA_ENABLE);
        boolean loginCaptchaEnable = Boolean.parseBoolean(config.getConfigValue());
        //校验验证码是否正确
        if (loginCaptchaEnable) {
            Assert.isTrue(StrUtil.isNotBlank(reqVO.getCaptcha()), () -> new BusinessException(UserEnum.ErrorMsg.CAPTCHA_CONTENT_EMPTY.getDesc()));
            Assert.isTrue(StrUtil.isNotBlank(reqVO.getCaptchaId()), () -> new BusinessException(UserEnum.ErrorMsg.CAPTCHA_ID_EMPTY.getDesc()));
            boolean checkImageCaptcha = checkImageCaptcha(reqVO.getCaptcha(), reqVO.getCaptchaId());
            Assert.isTrue(checkImageCaptcha, () -> new BusinessException(UserEnum.ErrorMsg.CAPTCHA_INCORRECT.getDesc()));
        }

        //① 查主库 m_user_index，获取用户所属租户
        MUserIndex userIndex = userIndexMapper.selectByUsername(reqVO.getUsername());
        Assert.notNull(userIndex, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
        //校验账号状态
        Assert.isTrue(StatusEnum.STATUS_1.getCode().equals(userIndex.getStatus()),
                () -> new BusinessException(UserEnum.ErrorMsg.USER_FROZEN.getDesc()));

        Long tenantId = userIndex.getTenantId();
        boolean needSwitch = CommonConstant.ZERO != tenantId;

        //② 切换到租户数据源验证密码
        MUser loginUser;
        if (needSwitch) {
            DynamicDataSourceContextHolder.push(String.valueOf(tenantId));
        }
        try {
            loginUser = userMapper.selectUserByUsername(reqVO.getUsername());
            Assert.notNull(loginUser, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
            //校验密码是否正确
            String passwordEncrypt = userManager.passwordEncrypt(reqVO.getPassword(), loginUser.getSalt());
            Assert.isTrue(loginUser.getPassword().equals(passwordEncrypt), () -> new BusinessException(UserEnum.ErrorMsg.U_OR_P_INCORRECT.getDesc()));
            //校验用户状态
            Assert.isTrue(StatusEnum.STATUS_1.getCode().equals(loginUser.getStatus()),
                    () -> new BusinessException(UserEnum.ErrorMsg.USER_FROZEN.getDesc()));
        } finally {
            if (needSwitch) {
                DynamicDataSourceContextHolder.poll();
            }
        }

        //③ 校验租户状态（主库 m_tenant）
        TenantVO tenantVO = tenantService.getTenantByTenantId(tenantId);
        Assert.notNull(tenantVO, () -> new BusinessException(UserEnum.ErrorMsg.USER_UNBOUND_TENANT.getDesc()));
        Assert.isTrue(StatusEnum.STATUS_1.getCode().equals(tenantVO.getStatus().intValue()),
                () -> new BusinessException(TenantEnum.ErrorMsg.DISABLED_TENANT.getDesc()));
        tenantManager.checkTenantExpireTime(tenantVO.getExpireTime());

        //④ 登录
        StpUtil.login(loginUser.getUserId());
        //在登录时缓存参数 - 缓存租户ID
        StpUtil.getSession().set(TenantIgnore.TENANT_ID, tenantId);
        return StpUtil.getTokenInfo();
    }

    /**
     * 重置密码
     * @param passwordVO 重置密码实体
     */
    @Override
    public void resetPassword(RePasswordVO passwordVO) {
        //查询用户
        MUser user = userMapper.selectUserByUserId(StpUtil.getLoginIdAsLong());
        Assert.notNull(user, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
        //校验旧密码
        String oldPassword = userManager.passwordEncrypt(passwordVO.getOldPassword(), user.getSalt());
        Assert.isTrue(user.getPassword().equals(oldPassword), () -> new BusinessException(UserEnum.ErrorMsg.OLD_PASSWORD_INCORRECT.getDesc()));
        //新密码加密
        user.setPassword(userManager.passwordEncrypt(passwordVO.getNewPassword(), user.getSalt()));
        //修改
        userMapper.updateUserByUserId(user);
    }

    /**
     * 用户设置 -> 修改用户信息
     * @param settingVO 用户信息
     */
    @Override
    public void updateUserInfo(UserSettingVO settingVO) {
        //查询用户
        MUser user = userMapper.selectUserByUserId(StpUtil.getLoginIdAsLong());
        Assert.notNull(user, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
        MUser updateUser = BeanUtil.copyProperties(settingVO, MUser.class);
        //用户ID
        updateUser.setUserId(user.getUserId());
        //修改
        userMapper.updateUserByUserId(updateUser);
    }

    /**
     * 修改用户头像
     * @param userAvatar 用户头像base64编码
     */
    @Override
    public void updateUserAvatar(String userAvatar) {
        //用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        //校验头像大小
        byte[] base64Decode = Base64.decode(userAvatar);
        ConfigVO config = configService.getConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_USER_AVATAR_SIZE);
        long userAvatarSize = Long.parseLong(config.getConfigValue());
        Assert.isFalse(base64Decode.length > userAvatarSize, () -> new BusinessException(UserEnum.ErrorMsg.USER_AVATAR_SIZE.getDesc()));
        //查询用户
        MUser user = userMapper.selectUserByUserId(userId);
        Assert.notNull(user, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
        //更新用户头像
        MUser updateUser = new MUser();
        updateUser.setUserId(userId);
        updateUser.setUserAvatar(userAvatar);
        userMapper.updateUserByUserId(updateUser);
    }

}
