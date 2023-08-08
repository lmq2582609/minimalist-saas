package com.minimalist.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.minimalist.common.utils.SpringSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Mybatis-plus 公共字段自动填充
 */
@Slf4j
@Component
public class FillColumnsHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = SpringSecurityUtil.getUserId();
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createId", () -> userId, Long.class);
        this.strictInsertFill(metaObject, "createTime", () -> now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateId", () -> userId, Long.class);
        this.strictInsertFill(metaObject, "updateTime", () -> now, LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateId", SpringSecurityUtil::getUserId, Long.class);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }

}
