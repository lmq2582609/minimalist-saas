package com.minimalist.basic.entity.vo.tenant;

import com.minimalist.basic.utils.Add;
import com.minimalist.basic.utils.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "租户数据源实体")
public class TenantDatasourceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "数据库名称不能为空", groups = {Add.class, Update.class})
    @Schema(name = "datasourceName", description = "数据库名称", type = "string")
    private String datasourceName;

    @NotBlank(message = "数据库主机不能为空", groups = {Add.class, Update.class})
    @Schema(name = "host", description = "数据库主机地址", type = "string")
    private String host;

    @NotBlank(message = "数据库端口不能为空", groups = {Add.class, Update.class})
    @Schema(name = "port", description = "数据库端口", type = "string")
    private String port;

    @NotBlank(message = "数据库用户名不能为空", groups = {Add.class, Update.class})
    @Schema(name = "username", description = "数据库用户名", type = "string")
    private String username;

    @NotBlank(message = "数据库密码不能为空", groups = {Add.class, Update.class})
    @Schema(name = "password", description = "数据库密码", type = "string")
    private String password;

    /**
     * 拼接完整的 JDBC URL
     */
    public String buildJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + datasourceName
                + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai"
                + "&allowMultiQueries=true&useAffectedRows=true&rewriteBatchedStatements=true";
    }

    /**
     * 拼接不指定数据库的 JDBC URL（用于建库）
     */
    public String buildJdbcUrlWithoutDb() {
        return "jdbc:mysql://" + host + ":" + port
                + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai"
                + "&allowMultiQueries=true";
    }

}
