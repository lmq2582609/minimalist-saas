package com.minimalist.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.minimalist.common.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ValidateUtil {

    /**
     * 手动校验
     */
    public static <T> void valid(T t) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> errors = validator.validate(t);
        List<String> errorMsg = errors.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(errorMsg)) {
            StringJoiner sj = new StringJoiner("，");
            errorMsg.forEach(sj::add);
            throw new BusinessException(sj.toString());
        }
    }

}
