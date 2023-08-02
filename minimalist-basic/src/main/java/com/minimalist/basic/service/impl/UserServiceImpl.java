package com.minimalist.basic.service.impl;

import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.entity.bo.SecurityUserDetails;
import com.minimalist.basic.entity.constant.CommonConstant;
import com.minimalist.basic.entity.constant.RedisKeyConstant;
import com.minimalist.basic.entity.enums.PermEnum;
import com.minimalist.basic.entity.enums.RespEnum;
import com.minimalist.basic.entity.enums.RoleEnum;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.enums.UserEnum;
import com.minimalist.basic.entity.mybatis.PageResp;
import com.minimalist.basic.entity.po.MPerms;
import com.minimalist.basic.entity.po.MTenant;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.basic.entity.po.MUserDept;
import com.minimalist.basic.entity.po.MUserPost;
import com.minimalist.basic.entity.po.MUserRole;
import com.minimalist.basic.entity.vo.role.RoleVO;
import com.minimalist.basic.entity.vo.user.ImageCaptchaVO;
import com.minimalist.basic.entity.vo.user.RePasswordVO;
import com.minimalist.basic.entity.vo.user.UserInfoVO;
import com.minimalist.basic.entity.vo.user.UserLoginReqVO;
import com.minimalist.basic.entity.vo.user.UserQueryVO;
import com.minimalist.basic.entity.vo.user.UserSettingVO;
import com.minimalist.basic.entity.vo.user.UserVO;
import com.minimalist.basic.mapper.MTenantMapper;
import com.minimalist.basic.mapper.MUserDeptMapper;
import com.minimalist.basic.mapper.MUserMapper;
import com.minimalist.basic.mapper.MUserPostMapper;
import com.minimalist.basic.mapper.MUserRoleMapper;
import com.minimalist.basic.service.DeptService;
import com.minimalist.basic.service.PermService;
import com.minimalist.basic.service.PostService;
import com.minimalist.basic.service.RoleService;
import com.minimalist.basic.service.UserService;
import com.minimalist.basic.utils.RedisManager;
import com.minimalist.basic.utils.SafetyUtil;
import com.minimalist.basic.utils.SpringSecurityUtil;
import com.minimalist.basic.utils.UnqIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    /** jwt token密钥 */
    @Value("${systemConfig.tokenSecret}")
    private String tokenSecret;

    /** 登录验证码是否开启 */
    @Value("${systemConfig.loginCaptchaEnable}")
    private boolean loginCaptchaEnable;

    /** 用户头像大小限制 */
    @Value("${systemConfig.userAvatarSize}")
    private Long userAvatarSize;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    private MTenantMapper tenantMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private DeptService deptService;

    /**
     * 新增用户
     * @param userVO 用户实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserVO userVO) {
        //是否为管理员
        boolean isAdmin = SafetyUtil.checkIsAdminByTenantId();
        //不是管理员，添加用户需要校验额度
        if (!isAdmin) {
            //租户ID
            Long tenantId = SpringSecurityUtil.getTenantId();
            //查询租户
            MTenant mTenant = tenantMapper.selectTenantByTenantId(tenantId);
            Assert.notNull(mTenant, () -> new BusinessException(TenantEnum.ErrorMsg.NONENTITY_TENANT.getDesc()));
            //查询该租户下的用户数量
            long userCount = userMapper.selectUserCountByTenantId(tenantId);
            Assert.isFalse(userCount >= mTenant.getAccountCount(),
                    () -> new BusinessException(TenantEnum.ErrorMsg.TENANT_USER_COUNT_LIMIT.getDesc()));
        }
        //校验用户准确性
        checkUserAccuracy(null, userVO.getUsername(), userVO.getPhone(), userVO.getEmail());
        //拷贝
        MUser user = BeanUtil.copyProperties(userVO, MUser.class);
        //密码加密
        user.setPassword(passwordEncoder.encode(userVO.getPassword()));
        //userId
        long userId = UnqIdUtil.uniqueId();
        user.setUserId(userId);
        //处理部门
        user.setDeptIds(CollectionUtil.join(userVO.getCheckedDeptIds(), ","));
        //新增用户
        int insertCount = userMapper.insert(user);
        Assert.isTrue(insertCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //新增用户关联信息
        insertUserRelation(userVO.getRoleIds(), userVO.getPostIds(), userVO.getDeptIds(), userId);
    }

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserByUserId(Long userId) {
        //查询用户
        MUser user = userMapper.selectUserByUserId(userId);
        Assert.notNull(user, () -> new UsernameNotFoundException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
        //检查是否是管理员
        boolean isAdmin = SafetyUtil.checkIsAdminByTenantId();
        if (!isAdmin) {
            //检查租户ID，要删除的用户的租户必须与本次操作人的租户一致
            SafetyUtil.checkTenantIdIsTamperWithData(user.getTenantId());
        }
        //删除用户
        int deleteCount = userMapper.deleteUserByUserId(userId);
        Assert.isTrue(deleteCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //删除用户关联信息
        deleteUserRelation(userId);
    }

    /**
     * 修改用户
     * @param userVO 用户数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserByUserId(UserVO userVO) {
        //校验用户数据准确性
        MUser user = checkUserAccuracy(userVO.getUserId(), userVO.getUsername(), userVO.getPhone(), userVO.getEmail());
        //检查是否是管理员
        boolean isAdmin = SafetyUtil.checkIsAdminByTenantId();
        if (!isAdmin) {
            //检查租户ID，要删除的用户的租户必须与本次操作人的租户一致
            SafetyUtil.checkTenantIdIsTamperWithData(user.getTenantId());
        }
        //拷贝
        MUser newUser = BeanUtil.copyProperties(userVO, MUser.class);
        //是否需要修改密码
        if (StrUtil.isNotBlank(userVO.getPassword())) {
            //密码加密
            newUser.setPassword(passwordEncoder.encode(userVO.getPassword()));
        }
        //乐观锁字段赋值
        newUser.updateBeforeSetVersion(user.getVersion());
        //处理部门
        newUser.setDeptIds(CollectionUtil.join(userVO.getCheckedDeptIds(), ","));
        //修改用户
        int updateCount = userMapper.updateUserByUserId(newUser);
        Assert.isTrue(updateCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //删除用户关联信息
        deleteUserRelation(user.getUserId());
        //新增用户关联信息
        insertUserRelation(userVO.getRoleIds(), userVO.getPostIds(), userVO.getDeptIds(), user.getUserId());
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
        userInfoVO.setDeptList(deptService.getDeptByUserId(userId));
        return userInfoVO;
    }

    /**
     * 获取图形验证码
     * @return 图形验证码
     */
    @Override
    public ImageCaptchaVO getImageCaptcha() {
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
    public String userLogin(UserLoginReqVO reqVO) {
        //校验验证码是否正确
        if (loginCaptchaEnable) {
            Assert.isTrue(StringUtils.hasText(reqVO.getCaptcha()), () -> new BusinessException(UserEnum.ErrorMsg.CAPTCHA_ID_EMPTY.getDesc()));
            Assert.isTrue(StringUtils.hasText(reqVO.getCaptchaId()), () -> new BusinessException(UserEnum.ErrorMsg.CAPTCHA_CONTENT_EMPTY.getDesc()));
            boolean checkImageCaptcha = checkImageCaptcha(reqVO.getCaptcha(), reqVO.getCaptchaId());
            Assert.isTrue(checkImageCaptcha, () -> new BusinessException(UserEnum.ErrorMsg.CAPTCHA_INCORRECT.getDesc()));
        }
        //登录认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(reqVO.getUsername(), reqVO.getPassword());
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new BusinessException(UserEnum.ErrorMsg.U_OR_P_INCORRECT.getDesc());
        }
        Assert.notNull(authenticate, () -> new BusinessException(UserEnum.ErrorMsg.U_OR_P_INCORRECT.getDesc()));
        //认证成功
        SecurityUserDetails userDetails = (SecurityUserDetails) authenticate.getPrincipal();
        String userId = userDetails.getUser().getUserId().toString();
        //生成token
        String token = JWT.create().setKey(tokenSecret.getBytes()).setPayload("userId", userId).sign();
        //用户信息存入redis，过期时间：1天
        String key = StrUtil.indexedFormat(RedisKeyConstant.USERLOGIN_CACHE_KEY, userId);
        redisManager.set(key, userDetails, RedisKeyConstant.USERLOGIN_CACHE_EX);
        return StrUtil.concat(true, CommonConstant.JWT_SEPARATOR, token);
    }

    /**
     * 退出登录
     * @param userId 用户ID
     */
    @Override
    public void logout(Long userId) {
        String key = StrUtil.indexedFormat(RedisKeyConstant.USERLOGIN_CACHE_KEY, userId.toString());
        redisManager.delete(key);
    }

    /**
     * 重置密码
     * @param passwordVO 重置密码实体
     */
    @Override
    public void resetPassword(RePasswordVO passwordVO) {
        //查询用户
        MUser user = userMapper.selectUserByUserId(SpringSecurityUtil.getUserId());
        Assert.notNull(user, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
        //校验旧密码
        boolean checkOldPassword = passwordEncoder.matches(passwordVO.getOldPassword(), user.getPassword());
        Assert.isTrue(checkOldPassword, () -> new BusinessException(UserEnum.ErrorMsg.OLD_PASSWORD_INCORRECT.getDesc()));
        //新密码加密
        user.setPassword(passwordEncoder.encode(passwordVO.getNewPassword()));
        //乐观锁参数赋值
        user.updateBeforeSetVersion(user.getVersion());
        //修改
        int updateCount = userMapper.updateUserByUserId(user);
        Assert.isTrue(updateCount == 1, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 校验和设置用户身份
     * @param token 令牌
     */
    @Override
    public void checkAndSetAuthentication(String token) {
        token = token.replace(CommonConstant.JWT_SEPARATOR, "");
        //校验token
        boolean verify = JWT.of(token).setKey(tokenSecret.getBytes()).verify();
        //凭证无效
        Assert.isTrue(verify, () -> new BusinessException(RespEnum.REQUEST_UNAUTH.getDesc()));
        //解析token
        String userId = JWT.of(token).setKey(tokenSecret.getBytes()).getPayloads().get("userId", String.class);
        //从redis中获取用户信息
        String redisKey = StrUtil.indexedFormat(RedisKeyConstant.USERLOGIN_CACHE_KEY, userId);
        SecurityUserDetails userDetails = redisManager.get(redisKey);
        //登录过期
        Assert.notNull(userDetails, () -> new BusinessException(RespEnum.REQUEST_UNAUTH.getDesc()));
        //存入SecurityContextHolder，获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    /**
     * 用户设置 -> 修改用户信息
     * @param settingVO 用户信息
     */
    @Override
    public void updateUserInfo(UserSettingVO settingVO) {
        //查询用户
        MUser user = userMapper.selectUserByUserId(SpringSecurityUtil.getUserId());
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
        Long userId = SpringSecurityUtil.getUserId();
        //校验头像大小
        byte[] base64Decode = Base64.decode(userAvatar);
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

    /**
     * 删除用户角色、岗位关联信息
     * @param userId 用户ID
     */
    private void deleteUserRelation(Long userId) {
        //删除用户与角色关联关系 -> 真实删除
        entityService.delete(MUserRole::getUserId, userId);
        //删除用户与岗位关联关系 -> 真实删除
        entityService.delete(MUserPost::getUserId, userId);
        //删除用户与部门关联关系 -> 真实删除
        entityService.delete(MUserDept::getUserId, userId);
    }

    /**
     * 新增用户角色、岗位关联信息
     * @param roleIds 角色ID列表
     * @param postIds 岗位ID列表
     * @param deptIds 部门ID列表
     */
    private void insertUserRelation(Set<Long> roleIds, Set<Long> postIds, Set<Long> deptIds, Long userId) {
        //插入用户与角色关联关系
        List<MUserRole> userRoleList = roleIds.stream().map(r -> {
            MUserRole userRole = new MUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(r);
            return userRole;
        }).toList();
        int userRoleInsertBatchCount = entityService.insertBatch(userRoleList);
        Assert.isTrue(roleIds.size() == userRoleInsertBatchCount, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //插入用户与岗位关联关系
        List<MUserPost> userPostList = postIds.stream().map(p -> {
            MUserPost userPost = new MUserPost();
            userPost.setUserId(userId);
            userPost.setPostId(p);
            return userPost;
        }).toList();
        int userPostInsertBatchCount = entityService.insertBatch(userPostList);
        Assert.isTrue(postIds.size() == userPostInsertBatchCount, () -> new BusinessException(RespEnum.FAILED.getDesc()));
        //插入用户与部门关联关系
        List<MUserDept> userDeptList = deptIds.stream().map(d -> {
            MUserDept userDept = new MUserDept();
            userDept.setUserId(userId);
            userDept.setDeptId(d);
            return userDept;
        }).toList();
        int userDeptInsertBatchCount = entityService.insertBatch(userDeptList);
        Assert.isTrue(deptIds.size() == userDeptInsertBatchCount, () -> new BusinessException(RespEnum.FAILED.getDesc()));
    }

    /**
     * 校验用户准确性
     * @param username 用户名
     * @param phone 手机号
     * @param email 邮箱
     */
    private MUser checkUserAccuracy(Long userId, String username, String phone, String email) {
        MUser userByUsername = userMapper.selectUserByUsername(username);
        MUser userByPhone = userMapper.selectUserByPhone(phone);
        MUser userByEmail = userMapper.selectUserByEmail(email);
        //userId为空，新增数据前校验
        if (ObjectUtil.isNull(userId)) {
            //校验用户名唯一
            Assert.isNull(userByUsername, () -> new BusinessException(UserEnum.ErrorMsg.EXISTS_ACCOUNT.getDesc()));
            //校验手机号唯一
            Assert.isNull(userByPhone, () -> new BusinessException(UserEnum.ErrorMsg.PHONE_ACCOUNT.getDesc()));
            //校验邮箱唯一
            Assert.isNull(userByEmail, () -> new BusinessException(UserEnum.ErrorMsg.EMAIL_ACCOUNT.getDesc()));
        } else {
            //修改数据前校验
            //查询用户
            MUser user = userMapper.selectUserByUserId(userId);
            Assert.notNull(user, () -> new BusinessException(UserEnum.ErrorMsg.NONENTITY_ACCOUNT.getDesc()));
            //校验手机号唯一
            if (ObjectUtil.isNotNull(userByUsername)) {
                Assert.isTrue(userByUsername.getUserId().equals(userId), () -> new BusinessException(UserEnum.ErrorMsg.EXISTS_ACCOUNT.getDesc()));
            }
            //校验手机号唯一
            if (ObjectUtil.isNotNull(userByPhone)) {
                Assert.isTrue(userByPhone.getUserId().equals(userId), () -> new BusinessException(UserEnum.ErrorMsg.PHONE_ACCOUNT.getDesc()));
            }
            //校验邮箱唯一
            if (ObjectUtil.isNotNull(userByEmail)) {
                Assert.isTrue(userByEmail.getUserId().equals(userId), () -> new BusinessException(UserEnum.ErrorMsg.EMAIL_ACCOUNT.getDesc()));
            }
            return user;
        }
        return null;
    }

}
