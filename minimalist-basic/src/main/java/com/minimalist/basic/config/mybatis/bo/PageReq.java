package com.minimalist.basic.config.mybatis.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * 分页请求
 */
@Getter
@Setter
public class PageReq {

    /** 页码 */
    private Long pageNum = 1L;

    /** 分页条数 默认10条 */
    private Long pageSize = 10L;

}
