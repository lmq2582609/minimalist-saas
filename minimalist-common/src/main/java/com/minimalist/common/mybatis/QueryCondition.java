package com.minimalist.common.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import java.util.List;

public class QueryCondition<T> extends LambdaQueryWrapper<T> {

    /**
     * 查询指定字段
     * @param column 列
     * @return 查询对象
     */
    public QueryCondition<T> selectColumns(SFunction<T, ?> column) {
        return (QueryCondition<T>) super.select(column);
    }

    /**
     * 模糊查询：LIKE '%值%'
     * @param column 列
     * @param val 值
     * @return 查询对象
     */
    public QueryCondition<T> likeNotNull(SFunction<T, ?> column, String val) {
        if (StrUtil.isNotBlank(val)) {
            return (QueryCondition<T>) super.like(true, column, val);
        }
        return this;
    }

    /**
     * 模糊查询：LIKE '值%'
     * @param column 列
     * @param val 值
     * @return 查询对象
     */
    public QueryCondition<T> likeRightNotNull(SFunction<T, ?> column, String val) {
        if (StrUtil.isNotBlank(val)) {
            return (QueryCondition<T>) super.likeRight(true, column, val);
        }
        return this;
    }

    /**
     * 模糊查询：LIKE '%值'
     * @param column 列
     * @param val 值
     * @return 查询对象
     */
    public QueryCondition<T> likeLeftNotNull(SFunction<T, ?> column, String val) {
        if (StrUtil.isNotBlank(val)) {
            return (QueryCondition<T>) super.likeLeft(true, column, val);
        }
        return this;
    }

    /**
     * 匹配查询
     * @param column 列
     * @param val 值
     * @return 查询对象
     */
    public QueryCondition<T> eqNotNull(SFunction<T, ?> column, Object val) {
        if (ObjectUtil.isNotNull(val)) {
            return (QueryCondition<T>) super.eq(true, column, val);
        }
        return this;
    }

    /**
     * IN查询
     * @param column 列
     * @param vals 值列表
     * @return 查询对象
     */
    public <V> QueryCondition<T> inNotEmpty(SFunction<T, ?> column, List<V> vals) {
        if (CollectionUtil.isNotEmpty(vals)) {
            return (QueryCondition<T>) super.in(true, column, vals);
        }
        return this;
    }

    /**
     * 分组
     * @param column 列
     * @return 查询对象
     */
    public QueryCondition<T> groupByy(SFunction<T, ?> column) {
        return (QueryCondition<T>) super.groupBy(true, column);
    }

    /**
     * 排序 升序
     * @param column 列
     * @return 查询对象
     */
    public QueryCondition<T> orderByAscc(SFunction<T, ?> column) {
        return (QueryCondition<T>) super.orderByAsc(column);
    }

    /**
     * 排序 降序
     * @param column 列
     * @return 查询对象
     */
    public QueryCondition<T> orderByDescc(SFunction<T, ?> column) {
        return (QueryCondition<T>) super.orderByDesc(column);
    }

    /**
     * 小于等于
     * @param column 列
     * @return 查询对象
     */
    public QueryCondition<T> lee(SFunction<T, ?> column, Object val) {
        return (QueryCondition<T>) super.le(column, val);
    }

}
