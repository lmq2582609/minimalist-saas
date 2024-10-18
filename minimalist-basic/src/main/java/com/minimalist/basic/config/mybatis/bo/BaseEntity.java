package com.minimalist.basic.config.mybatis.bo;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseEntity {

    /** ID自增 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 逻辑删除 */
    @Column(isLogicDelete = true)
    private Boolean deleted;

    /** 创建人ID */
    private Long createId;

    /** 更新人ID */
    private Long updateId;

    /** 创建时间 */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /** 版本号 */
    @Column(version = true, onInsertValue = "0", onUpdateValue = "version + 1")
    private Integer version;

    /**
     * 更新时，设置version乐观锁字段
     * @param version 版本号
     */
    public void updateBeforeSetVersion(Integer version) {
        if (ObjectUtil.isNotNull(version)) {
            this.version = version;
        }
    }

}
