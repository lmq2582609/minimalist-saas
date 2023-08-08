package com.minimalist.common.mybatis.bo;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity {

    /** ID自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 逻辑删除  0已删除  1未删除 */
    @TableLogic
    private Boolean deleted;

    /** 创建人ID */
    @TableField(value = "create_id", fill = FieldFill.INSERT)
    private Long createId;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新人ID */
    @TableField(value = "update_id", fill = FieldFill.INSERT_UPDATE)
    private Long updateId;

    /** 更新时间 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 版本号 */
    @Version
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
