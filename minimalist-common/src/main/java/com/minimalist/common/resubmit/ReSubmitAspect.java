package com.minimalist.common.resubmit;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.minimalist.common.constant.RedisKeyConstant;
import com.minimalist.common.enums.RespEnum;
import com.minimalist.common.redis.RedisManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Comparator;
import java.util.TreeMap;

/**
 * 防重复提交处理
 */
@Slf4j
@Aspect
@Component
public class ReSubmitAspect {

    @Autowired
    private RedisManager redisManager;

    @Around("@annotation(re)")
    public Object around(ProceedingJoinPoint joinPoint, ReSubmit re) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ReSubmit reSubmit = method.getAnnotation(ReSubmit.class);
        //如果不需要防重复提交校验，则放行
        if (ObjectUtil.isNull(reSubmit)) {
            return joinPoint.proceed();
        }
        //获取请求参数
        String lockKey = getReqParams(joinPoint, method);
        //根据请求参数，加锁
        boolean acquireLock = redisManager.tryLock(lockKey, reSubmit.waitTime(), reSubmit.leaseTime());
        //重复提交
        if (!acquireLock) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(RespEnum.RESUBMIT_ERROR.getDesc());
        }
        try {
            return joinPoint.proceed();
        } finally {
            redisManager.unLock(lockKey);
        }
    }

    /**
     * 获取请求参数
     * @param joinPoint joinPoint
     * @return 缓存key
     */
    private String getReqParams(ProceedingJoinPoint joinPoint, Method method) {
        //获取请求参数
        Object [] args = joinPoint.getArgs();
        //方法的参数
        Parameter[] parameters = method.getParameters();
        //参数有序存放
        TreeMap<String, Object> paramTreeMap = MapUtil.newTreeMap(Comparator.naturalOrder());
        //参数转为key
        if (ArrayUtil.isNotEmpty(args) && ArrayUtil.isNotEmpty(parameters) && args.length == parameters.length) {
            //取参数
            for (int i = 0; i < args.length; i++) {
                //参数值
                Object arg = args[i];
                //参数
                Parameter param = parameters[i];
                //是否为基本数据类型，可能是单参数
                boolean basicType = ClassUtil.isBasicType(arg.getClass());
                if (basicType || arg instanceof String) {
                    paramTreeMap.put(param.getName(), arg);
                } else {
                    //其余参数视为对象，转Map
                    String json = JSONUtil.toJsonStr(arg);
                    JSONObject jsonObject = JSONUtil.parseObj(json);
                    paramTreeMap.putAll(jsonObject);
                }
            }
            return StrUtil.indexedFormat(RedisKeyConstant.REPEAT_SUBMIT_KEY, SecureUtil.sha256(JSONUtil.toJsonStr(paramTreeMap)));
        }
        //获取IP
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        //使用 IP:包.类名.方法名 生成一个key
        String sha256 = SecureUtil.sha256(JakartaServletUtil.getClientIP(request) + ":" + method.getDeclaringClass().getName() + "." + method.getName());
        return StrUtil.indexedFormat(RedisKeyConstant.REPEAT_SUBMIT_KEY, sha256);
    }

}
