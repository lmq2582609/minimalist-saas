package com.minimalist.application.config;

import com.minimalist.common.exception.BusinessException;
import com.minimalist.common.enums.RespEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.xss.core.XssException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数异常
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(value = {IllegalArgumentException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<Object> paramsException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            log.warn(e.getMessage(), e);
            return ResponseEntity.status(RespEnum.PARAM_ERROR.getCode()).body(RespEnum.PARAM_ERROR.getDesc());
        } else if (e instanceof MethodArgumentNotValidException) {
            //参数异常处理
            log.warn(e.getMessage(), e);
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringJoiner sj = new StringJoiner("，");
            errors.forEach(error -> sj.add(error.getDefaultMessage()));
            return ResponseEntity.status(RespEnum.PARAM_ERROR.getCode()).body(sj.toString());
        } else if (e instanceof ConstraintViolationException) {
            //参数异常处理
            log.warn(e.getMessage(), e);
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) e).getConstraintViolations();
            StringJoiner sj = new StringJoiner("，");
            constraintViolations.forEach(c -> sj.add(c.getMessageTemplate()));
            return ResponseEntity.status(RespEnum.PARAM_ERROR.getCode()).body(sj.toString());
        } else {
            return exception(e);
        }
    }

    /**
     * 自定义异常
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<Object> customException(Exception e) {
        //业务异常
        if (e instanceof BusinessException) {
            //记录日志
            log(e);
            //响应 业务异常
            return ResponseEntity.status(((BusinessException) e).getCode()).body(e.getMessage());
        } else {
            return exception(e);
        }
    }

    /**
     * XSS异常
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(value = {HttpMessageNotReadableException.class, MethodArgumentConversionNotSupportedException.class})
    public ResponseEntity<Object> xssException(Exception e) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(e);
        if (rootCause instanceof XssException) {
            //记录日志
            log(e);
            return ResponseEntity.status(RespEnum.PARAM_XSS_ERROR.getCode()).body(RespEnum.PARAM_XSS_ERROR.getDesc());
        } else {
            return exception(e);
        }
    }

    /**
     * 未知异常
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> exception(Exception e) {
        //记录日志
        log(e);
        //响应 系统异常
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RespEnum.FAILED.getDesc());
    }

    private void log(Exception e) {
        //记录日志
        log.error(e.getMessage(), e);
        //邮件、通知、告警

    }

}
