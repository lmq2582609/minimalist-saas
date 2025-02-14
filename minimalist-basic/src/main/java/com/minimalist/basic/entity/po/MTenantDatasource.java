package com.minimalist.basic.entity.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serial;

/**
 * 租户数据源表 实体类。
 *
 * @author asus
 * @since 2025-02-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(value = "m_tenant_datasource")
public class MTenantDatasource implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** ID自增 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 数据源ID
     */
    private Long datasourceId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源连接
     */
    private String datasourceUrl;

    /**
     * 数据源用户名
     */
    private String username;

    /**
     * 数据源密码
     */
    private String password;

}
