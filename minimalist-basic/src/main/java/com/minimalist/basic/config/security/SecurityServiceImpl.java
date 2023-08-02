package com.minimalist.basic.config.security;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalist.basic.service.UserService;
import com.minimalist.basic.entity.bo.SecurityUserDetails;
import com.minimalist.basic.entity.enums.UserEnum;
import com.minimalist.basic.entity.po.MUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SecurityServiceImpl implements UserDetailsService {

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
        Assert.notNull(user, () -> new UsernameNotFoundException(UserEnum.ErrorMsg.USER_FROZEN.getDesc()));
        //封装UserDetails对象返回
        return new SecurityUserDetails(userService.getUserInfo(user.getUserId()));
    }

}
