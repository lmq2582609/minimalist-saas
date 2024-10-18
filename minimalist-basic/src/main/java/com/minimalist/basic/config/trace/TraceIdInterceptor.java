package com.minimalist.basic.config.trace;

import cn.hutool.core.util.IdUtil;
import com.minimalist.basic.utils.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 链路追踪拦截器
 * 请求前添加traceId并放到响应头中，请求后将清除
 */
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = IdUtil.randomUUID();
        MDC.put(CommonConstant.TRACE_ID, traceId);
        response.setHeader(CommonConstant.TRACE_ID, traceId);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.clear();
    }

}