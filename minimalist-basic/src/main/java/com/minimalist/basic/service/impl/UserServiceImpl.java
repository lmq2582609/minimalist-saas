package com.minimalist.basic.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.enums.PermEnum;
import com.minimalist.basic.entity.enums.RoleEnum;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.po.MPerms;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.entity.po.MUserDept;
import com.minimalist.basic.entity.po.MUserPost;
import com.minimalist.basic.entity.po.MUserRole;
import com.minimalist.basic.entity.vo.role.RoleVO;
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
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.basic.mapper.MUserPostMapper;
import com.minimalist.basic.mapper.MUserRoleMapper;
import com.minimalist.basic.service.*;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.constant.RedisKeyConstant;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.module.entity.vo.config.ConfigVO;
import com.minimalist.common.module.service.ConfigService;
import com.minimalist.common.mybatis.EntityService;
import com.minimalist.common.mybatis.bo.PageResp;
import com.minimalist.common.redis.RedisManager;
import com.minimalist.basic.entity.enums.UserEnum;
import com.minimalist.common.tenant.IgnoreTenant;
import com.minimalist.common.utils.SafetyUtil;
import com.minimalist.common.utils.UnqIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private EntityService entityService;

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

    /**
     * 新增用户
     * @param userVO 用户实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserVO userVO) {
        //租户ID，用于和新增用户建立绑定关系
        long tenantId = SafetyUtil.getLonginUserTenantId();
        //校验用户名唯一
        userManager.checkUsernameUniqueness(userVO.getUsername(), null);
        //校验邮箱唯一
        userManager.checkUserEmailUniqueness(userVO.getEmail(), null);
        //校验该租户套餐是否满足条件
        tenantManager.checkTenantPackage(tenantId);

        //新增用户数据
        MUser user = BeanUtil.copyProperties(userVO, MUser.class);
        long userId = UnqIdUtil.uniqueId();
        user.setUserId(userId);
        user.setTenantId(tenantId);
        user.setStatus(UserEnum.UserStatus.USER_STATUS_1.getCode());
        //生成盐值，密码加密
        String salt = RandomUtil.randomString(6);
        user.setSalt(salt);
        if (StrUtil.isNotBlank(userVO.getPassword())) {
            user.setPassword(userManager.passwordEncrypt(userVO.getPassword(), salt));
        } else {
            //设置默认密码
            user.setPassword(userManager.passwordEncrypt("123456qwerty", salt));
        }
        //处理用户所在部门
        if (CollectionUtil.isNotEmpty(userVO.getCheckedDeptIds())) {
            user.setDeptIds(CollectionUtil.join(userVO.getCheckedDeptIds(), ","));
        }
        userMapper.insert(user);
        //新增用户关联信息
        userManager.insertUserRelation(userVO.getRoleIds(), userVO.getPostIds(), userVO.getDeptIds(), userId);
    }

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserByUserId(Long userId) {
        //检查租户ID，要删除的用户的租户必须与本次操作人的租户一致
        tenantManager.checkTenantEqual(userId, StpUtil.getLoginIdAsLong());
        userMapper.deleteUserByUserId(userId);
        //删除用户关联信息
        userManager.deleteUserRelation(userId);
    }

    /**
     * 修改用户
     * @param userVO 用户数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserByUserId(UserVO userVO) {
        //校验用户名唯一
        userManager.checkUsernameUniqueness(userVO.getUsername(), userVO.getUserId());
        //校验邮箱唯一
        userManager.checkUserEmailUniqueness(userVO.getEmail(), userVO.getUserId());
        //校验该租户套餐是否满足条件
        tenantManager.checkTenantPackage(SafetyUtil.getLonginUserTenantId());
        //检查租户ID，要修改的用户的租户必须与本次操作人的租户一致
        MUser optUser = tenantManager.checkTenantEqual(userVO.getUserId(), StpUtil.getLoginIdAsLong());

        //修改用户信息
        MUser newUser = BeanUtil.copyProperties(userVO, MUser.class);
        //是否需要修改密码
        if (StrUtil.isNotBlank(userVO.getPassword())) {
            //密码加密
            newUser.setPassword(userManager.passwordEncrypt(userVO.getPassword(), optUser.getSalt()));
        }
        //乐观锁字段赋值
        newUser.updateBeforeSetVersion(optUser.getVersion());
        //处理用户所在部门
        if (CollectionUtil.isNotEmpty(userVO.getCheckedDeptIds())) {
            newUser.setDeptIds(CollectionUtil.join(userVO.getCheckedDeptIds(), ","));
        }
        //修改用户
        userMapper.updateUserByUserId(newUser);
        //删除用户关联信息
        userManager.deleteUserRelation(optUser.getUserId());
        //新增用户关联信息
        userManager.insertUserRelation(userVO.getRoleIds(), userVO.getPostIds(), userVO.getDeptIds(), optUser.getUserId());
    }

    /**
     * 查询用户(分页)
     * @param queryVO 查询条件
     * @return 用户分页数据
     */
    @Override
    public PageResp<UserVO> getPageUserList(UserQueryVO queryVO) {
        Page<MUser> userPage = userMapper.selectPageUserList(queryVO);
        //数据转换
        List<UserVO> userVOList = BeanUtil.copyToList(userPage.getRecords(), UserVO.class);
        return new PageResp<>(userVOList, userPage.getTotal());
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
        userVO.setDeptIds(userDeptList.stream().map(MUserDept::getDeptId).collect(Collectors.toSet()));
        //部门选中回显
        if (StrUtil.isNotBlank(mUser.getDeptIds())) {
            userVO.setCheckedDeptIds(StrUtil.split(mUser.getDeptIds(), ","));
        }
        return userVO;
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public MUser selectUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    /**
     * 获取用户信息
     * @return 用户VO
     */
    @Override
    public UserInfoVO getUserInfo(Long userId) {
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
                    .filter(r -> RoleEnum.RoleStatus.ROLE_STATUS_1.getCode().equals(r.getStatus()))
                    .map(r -> {
                        //角色标识符
                        roleCodes.add(r.getRoleCode());
                        //返回角色ID
                        return r.getRoleId();
                    }).distinct().toList();
            //存放菜单数据
            List<MPerms> menuList = CollectionUtil.list(false);
            //根据角色ID查询权限 - 返回权限平铺数据
            List<MPerms> permList = permService.getPermsByUserId(roleIds);
            //汇总权限标识符集合
            Set<String> permCodes = permList.stream()
                    //状态 = 正常
                    .filter(p -> PermEnum.PermStatus.PERM_STATUS_1.getCode().equals(p.getStatus()))
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
        }
        //用户岗位
        userInfoVO.setPostList(postService.getPostByUserId(userId));
        //用户所属部门
        if (StrUtil.isNotBlank(user.getDeptIds())) {
            List<Long> deptIds = StrUtil.split(user.getDeptIds(), ",")
                    .stream().map(Long::parseLong).toList();
            userInfoVO.setDeptList(deptService.getDeptByDeptIds(deptIds));
        }
        return userInfoVO;
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
        String key = StrUtil.indexedFormat(RedisKeyConstant.CAPTCHA_CACHE_KEY, captchaId);
        String captchaCache = redisManager.getAndDelete(key);
        return captcha.toLowerCase().equals(captchaCache);
    }

    /**
     * 用户登录
     * @param reqVO 用户登录信息
     * @return token
     */
    @Override
    public SaTokenInfo userLogin(UserLoginReqVO reqVO) {
        ConfigVO config = configService.getConfigByConfigKey(CommonConstant.SYSTEM_CONFIG_CAPTCHA_ENABLE);
        boolean loginCaptchaEnable = Boolean.parseBoolean(config.getConfigValue());
        //校验验证码是否正确
        if (loginCaptchaEnable) {
            Assert.isTrue(StringUtils.hasText(reqVO.getCaptcha()), () -> new BusinessException(UserEnum.ErrorMsg.CAPTCHA_ID_EMPTY.getDesc()));
            Assert.isTrue(StringUtils.hasText(reqVO.getCaptchaId()), () -> new BusinessException(UserEnum.ErrorMsg.CAPTCHA_CONTENT_EMPTY.getDesc()));
            boolean checkImageCaptcha = checkImageCaptcha(reqVO.getCaptcha(), reqVO.getCaptchaId());
            Assert.isTrue(checkImageCaptcha, () -> new BusinessException(UserEnum.ErrorMsg.CAPTCHA_INCORRECT.getDesc()));
        }
        MUser loginUser = userMapper.selectUserByUsername(reqVO.getUsername());
        Assert.notNull(loginUser, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
        //校验密码是否正确
        String passwordEncrypt = userManager.passwordEncrypt(reqVO.getPassword(), loginUser.getSalt());
        Assert.isTrue(loginUser.getPassword().equals(passwordEncrypt), () -> new BusinessException(UserEnum.ErrorMsg.U_OR_P_INCORRECT.getDesc()));
        //校验用户状态
        Assert.isTrue(UserEnum.UserStatus.USER_STATUS_1.getCode().equals(loginUser.getStatus()),
                () -> new BusinessException(UserEnum.ErrorMsg.USER_FROZEN.getDesc()));
        //根据用户ID查询租户
        TenantVO tenantVO = tenantService.getTenantByUserId(loginUser.getUserId());
        //账户未绑定租户
        Assert.notNull(tenantVO, () -> new BusinessException(UserEnum.ErrorMsg.USER_UNBOUND_TENANT.getDesc()));
        //租户状态
        Assert.isTrue(TenantEnum.TenantStatus.TENANT_STATUS_1.getCode().equals(tenantVO.getStatus().intValue()),
                () -> new BusinessException(TenantEnum.ErrorMsg.DISABLED_TENANT.getDesc()));
        //获取当天最晚时间，23:59:59
        LocalDateTime localDateTime = LocalDateTimeUtil.endOfDay(LocalDateTime.now());
        //检查租户是否过期，过期提示不允许登录
        Duration duration = LocalDateTimeUtil.between(localDateTime, tenantVO.getExpireTime());
        //如果租户到期时间 < 当天，返回负，说明已到期
        long exHours = duration.toHours();
        Assert.isFalse(exHours <= 0, () -> new BusinessException(TenantEnum.ErrorMsg.EX_TENANT.getDesc()));
        StpUtil.login(loginUser.getUserId());
        // 在登录时缓存参数
        StpUtil.getSession().set(IgnoreTenant.TENANT_ID, loginUser.getTenantId());
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
        //乐观锁参数赋值
        user.updateBeforeSetVersion(user.getVersion());
        //修改
        int updateCount = userMapper.updateUserByUserId(user);
        Assert.isTrue(updateCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
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
        //乐观锁参数赋值
        updateUser.updateBeforeSetVersion(user.getVersion());
        //修改
        int updateCount = userMapper.updateUserByUserId(updateUser);
        Assert.isTrue(updateCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
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
        //乐观锁字段赋值
        updateUser.updateBeforeSetVersion(user.getVersion());
        int updateCount = userMapper.updateUserByUserId(updateUser);
        Assert.isTrue(updateCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

}
