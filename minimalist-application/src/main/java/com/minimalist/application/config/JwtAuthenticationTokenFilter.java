package com.minimalist.application.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpStatus;
import com.minimalist.basic.service.UserService;
import com.minimalist.common.constant.CommonConstant;
import com.minimalist.common.enums.RespEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        Cookie cookie = JakartaServletUtil.getCookie(request, CommonConstant.REQ_HEADER_JWT_KEY);
        if (ObjectUtil.isNotNull(cookie) && StrUtil.isNotBlank(cookie.getValue())) {
            String token = URLUtil.decode(cookie.getValue());
            try {
                //使用getBean获取UserService，防止循环依赖
                UserService userService = SpringUtil.getBean(UserService.class);
                //校验和设置用户身份
                userService.checkAndSetAuthentication(token);
            } catch (Exception e) {
                log.warn("校验和设置用户身份异常，", e);
                response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
                JakartaServletUtil.write(response, RespEnum.REQUEST_UNAUTH.getDesc(), MediaType.APPLICATION_JSON_UTF8_VALUE);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
