package com.minimalist.basic.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minimalist.basic.entity.po.MDict;
import com.minimalist.basic.entity.vo.dict.DictQueryVO;
import com.minimalist.common.mybatis.QueryCondition;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-05-27
 */
public interface MDictMapper extends BaseMapper<MDict> {

    /**
     * 根据字典ID查询字典
     * @param dictId 字典ID
     * @return 字典数据
     */
    default MDict selectDictByDictId(Long dictId) {
        return selectOne(new QueryCondition<MDict>().eqNotNull(MDict::getDictId, dictId));
    }

    /**
     * 根据字典ID删除字典
     * @param dictId 字典ID
     * @return 受影响行数
     */
    default int deleteDictByDictId(Long dictId) {
        return delete(new LambdaQueryWrapper<MDict>().eq(MDict::getDictId, dictId));
    }

    /**
     * 根据字典类型删除字典
     * @param dictType 字典类型
     * @return 受影响行数
     */
    default int deleteDictByDictType(String dictType) {
        return delete(new LambdaQueryWrapper<MDict>().eq(MDict::getDictType, dictType));
    }

    /**
     * 分页查询字典
     * @param queryVO 查询实体
     * @return 分页数据
     */
    default Page<MDict> selectPageDictList(DictQueryVO queryVO) {
        return selectPage(new Page<>(queryVO.getPageNum(), queryVO.getPageSize()),
                new QueryCondition<MDict>()
                        .likeNotNull(MDict::getDictName, queryVO.getDictName())
                        .likeNotNull(MDict::getDictType, queryVO.getDictType())
                        .eqNotNull(MDict::getStatus, queryVO.getStatus())
                        .groupByy(MDict::getDictType));
    }

    /**
     * 根据类型查询字典数据
     * @param dictTypeList 字典类型列表
     * @return 字典列表
     */
    default List<MDict> selectDictListByDictType(List<String> dictTypeList) {
        return selectList(new LambdaQueryWrapper<MDict>()
                .in(MDict::getDictType, dictTypeList)
                .orderByAsc(MDict::getDictOrder));
    }

    /**
     * 根据字典ID修改字典
     * @param dict 字典实体
     * @return 受影响行数
     */
    default int updateDictByDictId(MDict dict) {
        return update(dict, new LambdaUpdateWrapper<MDict>().eq(MDict::getDictId, dict.getDictId()));
    }

}
