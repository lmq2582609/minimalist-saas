package com.minimalist.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minimalist.common.security.user.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Data
public class SecurityUserDetails implements UserDetails {

    /**
     * 用户信息
     */
    private User user;

    public SecurityUserDetails(User user) {
        this.user = user;
    }

    /**
     * 存储SpringSecurity所需要的权限信息的集合，序列化时忽略该字段
     */
    @JsonIgnore
    private List<GrantedAuthority> authorities;

    /**
     * 返回用户权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getGrantedAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 指示用户的帐户是否已过期。无法对过期的帐户进行身份验证。
     * 返回值: 如果用户的帐户有效（即未过期），则为true；如果不再有效（即过期），则为false
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指示用户是锁定还是解锁。无法对锁定的用户进行身份验证。
     * 返回值: 如果用户未锁定，则为true，否则为false
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示用户的凭据（密码）是否已过期。过期的凭据阻止身份验证。
     * 返回值: 如果用户的凭据有效（即未过期），则为true；如果不再有效（即过期），则为false
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 指示用户是启用还是禁用。无法对禁用的用户进行身份验证。
     * 返回值: 如果用户已启用，则为true，否则为false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
