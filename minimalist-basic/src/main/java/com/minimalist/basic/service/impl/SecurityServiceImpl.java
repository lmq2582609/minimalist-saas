package com.minimalist.basic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.entity.vo.user.UserInfoVO;
import com.minimalist.basic.service.TenantService;
import com.minimalist.basic.service.UserService;
import com.minimalist.basic.entity.po.MUser;
import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.security.SecurityUserDetails;
import com.minimalist.common.security.user.User;
import com.minimalist.common.security.user.UserEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class SecurityServiceImpl implements UserDetailsService {

    @Autowired
    private TenantService tenantService;

    /**
     * security 加载用户数据
     * @param username 用户名
     * @return SecurityUserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //使用getBean获取UserService，防止循环依赖
        UserService userService = SpringUtil.getBean(UserService.class);
        //根据用户名查询用户
        MUser user = userService.selectUserByUsername(username);
        //用户名或密码错误
        Assert.notNull(user, () -> new BusinessException(UserEnum.ErrorMsg.U_OR_P_INCORRECT.getDesc()));
        //账户已被冻结
        Assert.isTrue(UserEnum.UserStatus.USER_STATUS_1.getCode().equals(user.getStatus()),
                () -> new BusinessException(UserEnum.ErrorMsg.USER_FROZEN.getDesc()));
        //根据用户ID查询租户
        TenantVO tenantVO = tenantService.getTenantByUserId(user.getUserId());
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
        //构建用户权限
        UserInfoVO userInfo = userService.getUserInfo(user.getUserId());
        //封装UserDetails对象返回
        User userDetail = BeanUtil.copyProperties(user, User.class);
        userDetail.setPerms(userInfo.getPerms());
        userDetail.setRoles(userInfo.getRoles());
        return new SecurityUserDetails(userDetail);
    }

    private List<GrantedAuthority> buildUserPermission(UserInfoVO userInfo) {
        //把"角色编码"和"权限编码"构建为GrantedAuthority对象存入authorities
        List<GrantedAuthority> grantedAuthorityList = CollectionUtil.list(false);
        if (CollectionUtil.isNotEmpty(userInfo.getPerms())) {
            userInfo.getPerms().forEach(permCode -> {
                grantedAuthorityList.add(new SimpleGrantedAuthority(permCode));
            });
        }
        if (CollectionUtil.isNotEmpty(userInfo.getRoles())) {
            userInfo.getRoles().forEach(roleCode -> {
                grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_" + roleCode));
            });
        }
        return grantedAuthorityList;
    }

}
