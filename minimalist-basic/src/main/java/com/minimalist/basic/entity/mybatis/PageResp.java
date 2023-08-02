package com.minimalist.basic.entity.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应
 */
@Data
@AllArgsConstructor
public class PageResp<T> {

    /** 数据 */
    private List<T> records = Collections.emptyList();

    /** 总条数 */
    private long total = 0;

}
