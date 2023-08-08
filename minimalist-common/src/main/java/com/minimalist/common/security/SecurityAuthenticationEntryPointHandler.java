package com.minimalist.common.security;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.HttpStatus;
import com.minimalist.common.enums.RespEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * 认证异常处理
 */
@Slf4j
@Component
public class SecurityAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
        JakartaServletUtil.write(response, RespEnum.REQUEST_UNAUTH.getDesc(), MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

}
