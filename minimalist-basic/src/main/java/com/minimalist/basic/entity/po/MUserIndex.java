package com.minimalist.basic.entity.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 全局用户账号索引表（主库）
 * 用于登录时根据用户名路由到对应租户数据源
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(value = "m_user_index")
public class MUserIndex implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** ID自增 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 用户账号（全局唯一） */
    private String username;

    /** 所属租户ID */
    private Long tenantId;

    /** 状态 0禁用 1正常 */
    private Integer status;

}
