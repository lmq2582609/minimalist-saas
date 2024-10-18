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
 * 用户与岗位关联表 1用户-N岗位 实体类。
 *
 * @author asus
 * @since 2024-10-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(value = "m_user_post", onInsert = InsertFullColumnHandler.class, onUpdate = UpdateFullColumnHandler.class)
public class MUserPost extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 岗位ID
     */
    private Long postId;

}
