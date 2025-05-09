package com.minimalist.basic.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.minimalist.basic.config.eDict.BeanMethod;
import com.minimalist.basic.entity.enums.DictEnum;
import com.minimalist.basic.entity.po.MDict;
import com.minimalist.basic.entity.vo.dict.*;
import com.minimalist.basic.mapper.MDictMapper;
import com.minimalist.basic.service.DictService;
import com.minimalist.basic.service.EDictService;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.config.eDict.EDictConstant;
import com.minimalist.basic.config.mybatis.bo.PageResp;
import com.minimalist.basic.config.redis.RedisManager;
import com.minimalist.basic.utils.RedisKeyConstant;
import com.minimalist.basic.utils.UnqIdUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private MDictMapper dictMapper;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 新增字典
     * @param dictInfoVO 字典实体
     */
    @Override
    public void addDict(DictInfoVO dictInfoVO) {
        //状态默认正常
        dictInfoVO.getDictDataList().forEach(d -> d.setStatus(StatusEnum.STATUS_1.getCode()));
        //数据转换，构建字典实体数据
        List<MDict> dictList = buildDictData(dictInfoVO, true);
        //批量新增
        dictMapper.insertBatch(dictList);
        //缓存处理
        setDictCacheHandler(CollectionUtil.list(false, dictInfoVO.getDictType()));
    }

    /**
     * 删除字典 -> 根据字典ID删除
     * @param dictId 字典ID
     */
    @Override
    public void deleteDictByDictId(Long dictId) {
        //查询字典
        MDict mDict = dictMapper.selectDictByDictId(dictId);
        Assert.notNull(mDict, () -> new BusinessException(DictEnum.ErrorMsg.NONENTITY_DICT.getDesc()));
        //删除字典
        dictMapper.deleteDictByDictId(dictId);
        //删除缓存
        String dictCacheKey = StrUtil.indexedFormat(RedisKeyConstant.DICT_CACHE_KEY, mDict.getDictType());
        redisManager.delete(dictCacheKey);
    }

    /**
     * 删除字典 -> 根据字典类型删除
     * @param dictType 字典类型
     */
    @Override
    public void deleteDictByDictType(String dictType) {
        //删除字典
        dictMapper.deleteDictByDictType(dictType);
        //删除缓存
        String dictCacheKey = StrUtil.indexedFormat(RedisKeyConstant.DICT_CACHE_KEY, dictType);
        redisManager.delete(dictCacheKey);
    }

    /**
     * 修改字典
     * @param dictInfoVO 字典实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictByDictId(DictInfoVO dictInfoVO) {
        //需要新增的数据 -> 没有dictId的数据
        List<DictDataVO> dueInsertData = CollectionUtil.list(false);
        Iterator<DictDataVO> dictDataVOIterator = dictInfoVO.getDictDataList().iterator();
        while (dictDataVOIterator.hasNext()) {
            DictDataVO dictDataVO = dictDataVOIterator.next();
            if (ObjectUtil.isNull(dictDataVO.getDictId())) {
                dueInsertData.add(dictDataVO);
                dictDataVOIterator.remove();
            }
        }
        //处理新增的数据
        if (CollectionUtil.isNotEmpty(dueInsertData)) {
            List<MDict> dictList = dueInsertData.stream().map(dict -> {
                MDict mDict = new MDict();
                mDict.setDictId(UnqIdUtil.uniqueId());
                mDict.setDictKey(dict.getDictKey());
                mDict.setDictValue(dict.getDictValue());
                mDict.setDictOrder(dict.getDictOrder());
                mDict.setStatus(dict.getStatus());
                mDict.setDictType(dictInfoVO.getDictType());
                mDict.setDictName(dictInfoVO.getDictName());
                mDict.setDictDesc(dictInfoVO.getDictDesc());
                mDict.setCreateId(StpUtil.getLoginIdAsLong());
                mDict.setCreateTime(LocalDateTime.now());
                mDict.setUpdateId(StpUtil.getLoginIdAsLong());
                mDict.setUpdateTime(LocalDateTime.now());
                return mDict;
            }).toList();
            //批量新增
            dictMapper.insertBatch(dictList);
        }
        //处理修改的数据
        if (CollectionUtil.isNotEmpty(dictInfoVO.getDictDataList())) {
            List<MDict> dictList = buildDictData(dictInfoVO, false);
            for (MDict mDict : dictList) {
                dictMapper.updateDictByDictId(mDict);
            }
        }
        //缓存处理
        setDictCacheHandler(CollectionUtil.list(false, dictInfoVO.getDictType()));
    }

    /**
     * 查询字典列表(分页)
     * @param queryVO 查询实体
     * @return 分页字典数据列表
     */
    @Override
    public PageResp<DictVO> getPageDictList(DictQueryVO queryVO) {
        Page<DictVO> dictVOPage = dictMapper.selectPageDictList(queryVO);
        return new PageResp<>(dictVOPage.getRecords(), dictVOPage.getTotalRow());
    }

    /**
     * 根据字典类型查询字典
     * @param dictType 字典类型
     * @return 字典数据
     */
    @Override
    public DictInfoVO getDictByDictType(String dictType) {
        //查询字典
        List<MDict> dictList = dictMapper.selectDictListByDictType(CollectionUtil.list(false, dictType));
        Assert.notEmpty(dictList, () -> new BusinessException(DictEnum.ErrorMsg.NONENTITY_DICT.getDesc()));
        //构建返回数据
        DictInfoVO dictInfoVO = new DictInfoVO();
        dictInfoVO.setDictName(dictList.get(0).getDictName());
        dictInfoVO.setDictDesc(dictList.get(0).getDictDesc());
        dictInfoVO.setDictType(dictList.get(0).getDictType());
        List<DictDataVO> dictDataVOList = dictList.stream().map(dict -> {
            DictDataVO dictDataVO = new DictDataVO();
            dictDataVO.setDictId(dict.getDictId());
            dictDataVO.setDictKey(dict.getDictKey());
            dictDataVO.setDictValue(dict.getDictValue());
            dictDataVO.setDictOrder(dict.getDictOrder());
            dictDataVO.setDictClass(dict.getDictClass());
            dictDataVO.setStatus(dict.getStatus());
            return dictDataVO;
        }).collect(Collectors.toList());
        dictInfoVO.setDictDataList(dictDataVOList);
        return dictInfoVO;
    }

    /**
     * 根据字典类型查询字典
     * @param dictTypes 字典类型，为空查询所有字典数据
     * @return 字典数据列表
     */
    @Override
    public List<DictCacheVO> getDictList(List<String> dictTypes) {
        //区分是否要获取额外字典数据
        Iterator<String> dictTypeIter = dictTypes.iterator();
        //获取额外字典
        List<String> extraDictTypeList = CollectionUtil.list(false);
        while (dictTypeIter.hasNext()) {
            String dictType = dictTypeIter.next();
            //如果该字典为额外字典数据
            if (EDictConstant.dictMethodMap.containsKey(dictType)) {
                extraDictTypeList.add(dictType);
                dictTypeIter.remove();
            }
        }
        //返回结果
        List<DictCacheVO> result = CollectionUtil.list(false);
        //额外字典获取
        if (CollectionUtil.isNotEmpty(extraDictTypeList)) {
            extraDictTypeList.forEach(ed -> {
                BeanMethod<EDictService> beanMethod = (BeanMethod<EDictService>) EDictConstant.dictMethodMap.get(ed);
                DictCacheVO dictCacheVO = ReflectUtil.invoke(beanMethod.getBean(), beanMethod.getMethod(), ed);
                result.add(dictCacheVO);
            });
        }
        //常规字典获取
        if (CollectionUtil.isNotEmpty(dictTypes)) {
            //从缓存中获取字典
            List<DictCacheVO> dictCache = getDictCacheHandler(dictTypes);
            //检查从缓存获取的字典是否缺失，缺失说明redis中数据过期
            if (dictTypes.size() != dictCache.size()) {
                //将缺失的字典type找出来
                List<String> dueDictTypeList = CollectionUtil.list(false);
                for (String dictType : dictTypes) {
                    boolean c = false;
                    for (DictCacheVO dictCacheVO : dictCache) {
                        if (dictCacheVO.getDictType().equals(dictType)) {
                            c = true;
                            break;
                        }
                    }
                    if (!c) {
                        dueDictTypeList.add(dictType);
                    }
                }
                //将未获取到的字典，重新获取
                if (CollectionUtil.isNotEmpty(dueDictTypeList)) {
                    dictCache.addAll(setDictCacheHandler(dueDictTypeList));
                }
            }
            result.addAll(dictCache);
        }
        return result;
    }

    /**
     * 添加字典数据到缓存
     */
    private List<DictCacheVO> setDictCacheHandler(List<String> dictType) {
        //返回结果
        List<DictCacheVO> resultList = CollectionUtil.list(false);
        //查询数据库中的字典数据
        List<MDict> mDicts;
        if (CollectionUtil.isEmpty(dictType)) {
            //查询全部
            mDicts = dictMapper.selectListByQuery(QueryWrapper.create()
                    .eq(MDict::getStatus, StatusEnum.STATUS_1.getCode())
                    .orderBy(MDict::getDictOrder, true));
        } else {
            //按字典类型查询
            mDicts = dictMapper.selectDictListByDictType(dictType);
        }
        if (CollectionUtil.isNotEmpty(mDicts)) {
            //转Map，key：字典类型，value：字典数据集合
            Map<String, List<MDict>> dictMap = mDicts.stream().collect(Collectors.groupingBy(MDict::getDictType));
            //批量存储到缓存
            RBatch batch = redissonClient.createBatch();
            dictMap.keySet().forEach(dt -> {
                List<MDict> dictList = dictMap.get(dt);
                String dictCacheKey = StrUtil.indexedFormat(RedisKeyConstant.DICT_CACHE_KEY, dt);
                RBucketAsync<List<DictCacheVO.DictKV>> bucket = batch.getBucket(dictCacheKey);
                //拷贝
                List<DictCacheVO.DictKV> dictKVList = BeanUtil.copyToList(dictList, DictCacheVO.DictKV.class);
                bucket.setAsync(dictKVList, RedisKeyConstant.DICT_CACHE_EX + redisManager.randomSeconds(), TimeUnit.SECONDS);
                //添加到返回值
                DictCacheVO dictCacheVO = new DictCacheVO();
                dictCacheVO.setDictType(dictList.get(0).getDictType());
                dictCacheVO.setDictList(dictKVList);
                resultList.add(dictCacheVO);
            });
            batch.execute();
        }
        return resultList;
    }

    /**
     * 从缓存中获取字典数据
     * @param dictType 字典类型，为空则获取所有字典数据
     * @return 字典数据列表
     */
    private List<DictCacheVO> getDictCacheHandler(List<String> dictType) {
        List<DictCacheVO> resultList = CollectionUtil.list(false);
        //获取部分字典数据
        if (CollectionUtil.isNotEmpty(dictType)) {
            //批量获取字典缓存
            RBatch batch = redissonClient.createBatch();
            dictType.forEach(dt -> {
                String dictCacheKey = StrUtil.indexedFormat(RedisKeyConstant.DICT_CACHE_KEY, dt);
                RBucketAsync<List<DictCacheVO.DictKV>> bucket = batch.getBucket(dictCacheKey);
                bucket.getAsync();
            });
            BatchResult<?> batchResult = batch.execute();
            List<?> responses = batchResult.getResponses();
            if (CollectionUtil.isNotEmpty(responses)) {
                responses.forEach(resp -> {
                    List<DictCacheVO.DictKV> dictVOList = (List<DictCacheVO.DictKV>) resp;
                    if (CollectionUtil.isNotEmpty(dictVOList)) {
                        DictCacheVO dictCacheVO = new DictCacheVO();
                        dictCacheVO.setDictType(dictVOList.get(0).getDictType());
                        dictCacheVO.setDictList(dictVOList);
                        resultList.add(dictCacheVO);
                    }
                });
            }
        } else {
            //获取全部字典数据
            RKeys keys = redissonClient.getKeys();
            String keyPrefix = StrUtil.subBefore(RedisKeyConstant.DICT_CACHE_KEY, ":", false);
            Iterable<String> keysByPattern = keys.getKeysByPattern(keyPrefix + ":*");
            Set<String> keySet = CollectionUtil.set(false);
            keysByPattern.forEach(keySet::add);
            RMap<String, List<DictCacheVO.DictKV>> cacheMap = redissonClient.getMap(keyPrefix);
            Map<String, List<DictCacheVO.DictKV>> dictCacheMap = cacheMap.getAll(keySet);
            if (MapUtil.isNotEmpty(dictCacheMap)) {
                dictCacheMap.values().forEach(dictVOList -> {
                    DictCacheVO dictCacheVO = new DictCacheVO();
                    dictCacheVO.setDictType(dictVOList.get(0).getDictType());
                    dictCacheVO.setDictList(dictVOList);
                    resultList.add(dictCacheVO);
                });
            }
        }
        return resultList;
    }

    /**
     * 构建字典实体数据
     * @param dictInfoVO 字典数据
     * @return 字典实体
     */
    private List<MDict> buildDictData(DictInfoVO dictInfoVO, boolean unqId) {
        return dictInfoVO.getDictDataList().stream()
                .map(dict -> {
                    MDict mDict = new MDict();
                    if (unqId) {
                        mDict.setDictId(UnqIdUtil.uniqueId());
                        mDict.setCreateId(StpUtil.getLoginIdAsLong());
                        mDict.setCreateTime(LocalDateTime.now());
                    } else {
                        mDict.setDictId(dict.getDictId());
                    }
                    mDict.setDictKey(dict.getDictKey());
                    mDict.setDictValue(dict.getDictValue());
                    mDict.setDictOrder(dict.getDictOrder());
                    mDict.setStatus(dict.getStatus());
                    mDict.setDictClass(dict.getDictClass());
                    mDict.setDictType(dictInfoVO.getDictType());
                    mDict.setDictName(dictInfoVO.getDictName());
                    mDict.setDictDesc(dictInfoVO.getDictDesc());
                    mDict.setUpdateId(StpUtil.getLoginIdAsLong());
                    mDict.setUpdateTime(LocalDateTime.now());
                    return mDict;
                }).toList();
    }

}
