package com.minimalist.common.exception;

import com.minimalist.common.enums.RespEnum;
import lombok.Data;
import java.io.Serializable;

@Data
public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private Integer code;

    /** 异常信息 */
    private String message;

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        this.code = RespEnum.FAILED.getCode();
        this.message = message;
    }

}
