package com.minimalist.basic.entity.vo.user;

import com.minimalist.common.mybatis.bo.Pager;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "用户查询实体")
public class UserQueryVO extends Pager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(name = "userRealName", description = "用户真实姓名", type = "string")
    private String userRealName;

    @Schema(name = "phone", description = "手机号码", type = "string")
    private String phone;

    @Schema(name = "dept", description = "选择的部门", type = "string")
    private Long dept;

    @Schema(name = "status", description = "用户状态", type = "integer")
    private Integer status;

}
