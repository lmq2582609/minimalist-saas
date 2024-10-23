package com.minimalist.basic.entity.po;

import com.minimalist.basic.config.mybatis.InsertFullColumnHandler;
import com.minimalist.basic.config.mybatis.UpdateFullColumnHandler;
import com.minimalist.basic.config.mybatis.bo.BaseEntity;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serial;

/**
 * 角色与权限关联表 1角色-N权限 实体类。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(value = "m_role_perm")
public class MRolePerm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限ID
     */
    private Long permId;

}
