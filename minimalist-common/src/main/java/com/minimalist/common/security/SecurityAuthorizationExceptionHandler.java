package com.minimalist.common.security;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.HttpStatus;
import com.minimalist.common.enums.RespEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * 授权异常处理
 */
@Slf4j
@Component
public class SecurityAuthorizationExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.HTTP_FORBIDDEN);
        JakartaServletUtil.write(response, RespEnum.NO_OPERATION_PERMISSION.getDesc(), MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

}
