package com.minimalist.basic.mapper;

import com.minimalist.basic.entity.vo.dict.DictQueryVO;
import com.mybatisflex.core.BaseMapper;
import com.minimalist.basic.entity.po.MDict;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;

/**
 * 字典表 映射层。
 *
 * @author 小太阳
 * @since 2024-10-18
 */
public interface MDictMapper extends BaseMapper<MDict> {
    /**
     * 根据字典ID查询字典
     * @param dictId 字典ID
     * @return 字典数据
     */
    default MDict selectDictByDictId(Long dictId) {
        return selectOneByQuery(QueryWrapper.create().eq(MDict::getDictId, dictId));
    }

    /**
     * 根据字典ID删除字典
     * @param dictId 字典ID
     */
    default void deleteDictByDictId(Long dictId) {
        deleteByQuery(QueryWrapper.create().eq(MDict::getDictId, dictId));
    }

    /**
     * 根据字典类型删除字典
     * @param dictType 字典类型
     */
    default void deleteDictByDictType(String dictType) {
        deleteByQuery(QueryWrapper.create().eq(MDict::getDictType, dictType));
    }

    /**
     * 分页查询字典
     * @param queryVO 查询实体
     * @return 分页数据
     */
    default Page<MDict> selectPageDictList(DictQueryVO queryVO) {
        return paginate(queryVO.getPageNum(), queryVO.getPageSize(),
                QueryWrapper.create().eq(MDict::getStatus, queryVO.getStatus())
                        .like(MDict::getDictName, queryVO.getDictName())
                        .like(MDict::getDictType, queryVO.getDictType())
                        .orderBy(MDict::getDictOrder, true)
                        .groupBy(MDict::getDictType));
    }

    /**
     * 根据类型查询字典数据
     * @param dictTypeList 字典类型列表
     * @return 字典列表
     */
    default List<MDict> selectDictListByDictType(List<String> dictTypeList) {
        return selectListByQuery(QueryWrapper.create().in(MDict::getDictType, dictTypeList).orderBy(MDict::getDictOrder, true));
    }

    /**
     * 根据字典ID修改字典
     * @param dict 字典实体
     */
    default void updateDictByDictId(MDict dict) {
        updateByQuery(dict, QueryWrapper.create().eq(MDict::getDictId, dict.getDictId()));
    }

    /**
     * 根据字典类型和字典key查询字典数据
     * @param dictType 字典类型
     * @param dictKey 字典key
     * @return 字典数据
     */
    default MDict selectDictByDictTypeAndKey(String dictType, String dictKey) {
        return selectOneByQuery(QueryWrapper.create()
                .eq(MDict::getDictType, dictType)
                .eq(MDict::getDictKey, dictKey));
    }
}
