package com.minimalist.basic.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.minimalist.basic.entity.enums.StatusEnum;
import com.minimalist.basic.entity.po.*;
import com.minimalist.basic.entity.vo.dict.DictCacheVO;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
import com.minimalist.basic.mapper.*;
import com.minimalist.basic.service.EDictService;
import com.minimalist.basic.config.eDict.EDict;
import com.minimalist.basic.config.eDict.EDictConstant;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.TenantUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 额外的字典数据，这些数据不会出现在字典管理表中，因为这些数据是来源于其他表
 */
@Service
public class EDictServiceImpl implements EDictService {

    @Autowired
    private MDeptMapper deptMapper;

    @Autowired
    private MUserMapper userMapper;

    @Autowired
    private MRoleMapper roleMapper;

    @Autowired
    private MPostMapper postMapper;

    @Autowired
    private MTenantMapper tenantMapper;

    @Autowired
    private MTenantPackageMapper tenantPackageMapper;

    @Autowired
    private MStorageMapper storageMapper;

    /**
     * 获取部门字典数据
     * @return 字典数据列表
     */
    @Override
    @EDict(dictType = EDictConstant.DEPT_LIST)
    public DictCacheVO getDeptDictData() {
        DictCacheVO dictCacheVO = new DictCacheVO();
        dictCacheVO.setDictType(EDictConstant.DEPT_LIST);
        //查询部门列表
        List<MDept> deptList = deptMapper.selectDeptDict();
        if (CollectionUtil.isNotEmpty(deptList)) {
            List<DictCacheVO.DictKV> dictKVList = deptList.stream().map(dept -> {
                DictCacheVO.DictKV dictKV = new DictCacheVO.DictKV();
                dictKV.setDictKey(dept.getDeptId().toString());
                dictKV.setDictValue(dept.getDeptName());
                dictKV.setDictType(EDictConstant.DEPT_LIST);
                return dictKV;
            }).toList();
            dictCacheVO.setDictList(dictKVList);
        }
        return dictCacheVO;
    }

    /**
     * 获取用户字典数据
     * @return 字典数据列表
     */
    @Override
    @EDict(dictType = EDictConstant.USER_LIST)
    public DictCacheVO getUserDictData() {
        return getUserDictList(EDictConstant.USER_LIST);
    }

    /**
     * 获取租户套餐字典数据（额外字典数据）
     * @return 字典数据列表
     */
    @Override
    @EDict(dictType = EDictConstant.TENANT_PACKAGE_LIST)
    public DictCacheVO getTenantPackageDictData() {
        DictCacheVO dictCacheVO = new DictCacheVO();
        dictCacheVO.setDictType(EDictConstant.TENANT_PACKAGE_LIST);
        //查询所有租户套餐
        List<MTenantPackage> tenantPackageList = tenantPackageMapper.selectTenantPackageDict();
        if (CollectionUtil.isNotEmpty(tenantPackageList)) {
            List<DictCacheVO.DictKV> dictKVList = tenantPackageList.stream().map(dept -> {
                DictCacheVO.DictKV dictKV = new DictCacheVO.DictKV();
                dictKV.setDictKey(dept.getPackageId().toString());
                dictKV.setDictValue(dept.getPackageName());
                dictKV.setDictType(EDictConstant.TENANT_PACKAGE_LIST);
                return dictKV;
            }).toList();
            dictCacheVO.setDictList(dictKVList);
        }
        return dictCacheVO;
    }

    /**
     * 获取角色字典数据
     * @return 字典数据列表
     */
    @Override
    @EDict(dictType = EDictConstant.ROLE_LIST)
    public DictCacheVO getRoleDictData() {
        DictCacheVO dictCacheVO = new DictCacheVO();
        dictCacheVO.setDictType(EDictConstant.ROLE_LIST);
        //查询角色列表
        List<MRole> roleList = roleMapper.selectRoleDict();
        if (CollectionUtil.isNotEmpty(roleList)) {
            List<DictCacheVO.DictKV> dictKVList = roleList.stream().map(role -> {
                DictCacheVO.DictKV dictKV = new DictCacheVO.DictKV();
                dictKV.setDictKey(role.getRoleId().toString());
                dictKV.setDictValue(role.getRoleName());
                dictKV.setDictType(EDictConstant.ROLE_LIST);
                return dictKV;
            }).toList();
            dictCacheVO.setDictList(dictKVList);
        }
        return dictCacheVO;
    }

    /**
     * 获取岗位字典数据（额外字典数据）
     * @return 字典数据列表
     */
    @Override
    @EDict(dictType = EDictConstant.POST_LIST)
    public DictCacheVO getPostDictData() {
        DictCacheVO dictCacheVO = new DictCacheVO();
        dictCacheVO.setDictType(EDictConstant.POST_LIST);
        //查询岗位列表
        List<MPost> postList = postMapper.selectPostDict();
        if (CollectionUtil.isNotEmpty(postList)) {
            List<DictCacheVO.DictKV> dictKVList = postList.stream().map(post -> {
                DictCacheVO.DictKV dictKV = new DictCacheVO.DictKV();
                dictKV.setDictKey(post.getPostId().toString());
                dictKV.setDictValue(post.getPostName());
                dictKV.setDictType(EDictConstant.POST_LIST);
                return dictKV;
            }).toList();
            dictCacheVO.setDictList(dictKVList);
        }
        return dictCacheVO;
    }

    /**
     * 获取租户字典数据（额外字典数据）
     * @return 字典数据列表
     */
    @Override
    @EDict(dictType = EDictConstant.TENANT_LIST)
    public DictCacheVO getTenantDictData() {
        DictCacheVO dictCacheVO = new DictCacheVO();
        dictCacheVO.setDictType(EDictConstant.TENANT_LIST);
        //查询租户列表
        List<MTenant> tenantList = tenantMapper.selectTenantDict();
        if (CollectionUtil.isNotEmpty(tenantList)) {
            List<DictCacheVO.DictKV> dictKVList = tenantList.stream()
                    .map(tenant -> {
                DictCacheVO.DictKV dictKV = new DictCacheVO.DictKV();
                dictKV.setDictKey(tenant.getTenantId().toString());
                dictKV.setDictValue(tenant.getTenantName());
                dictKV.setDictType(EDictConstant.TENANT_LIST);
                return dictKV;
            }).toList();
            dictCacheVO.setDictList(dictKVList);
        }
        return dictCacheVO;
    }

    /**
     * 获取存储方式数据（额外字典数据）
     * @return 字典数据列表
     */
    @Override
    @EDict(dictType = EDictConstant.STORAGE_LIST)
    public DictCacheVO getStorageDictData() {
        DictCacheVO dictCacheVO = new DictCacheVO();
        dictCacheVO.setDictType(EDictConstant.STORAGE_LIST);
        //系统租户，查询全部存储
        if (TenantUtil.checkIsSystemTenant()) {
            List<MStorage> mStorages = storageMapper.selectListByQuery(QueryWrapper.create()
                    .eq(MStorage::getStatus, StatusEnum.STATUS_1.getCode()));
            List<DictCacheVO.DictKV> dictKVList = mStorages.stream()
                    .map(storage -> {
                        DictCacheVO.DictKV dictKV = new DictCacheVO.DictKV();
                        dictKV.setDictKey(storage.getStorageId().toString());
                        dictKV.setDictValue(storage.getStorageName());
                        dictKV.setDictType(EDictConstant.STORAGE_LIST);
                        return dictKV;
                    }).toList();
            dictCacheVO.setDictList(dictKVList);
        } else {
            //普通租户，查询租户自己的存储
            TenantVO tenantVO = CommonConstant.tenantMap.get(TenantUtil.getTenantId());
            MStorage storage = storageMapper.selectStorageByStorageId(tenantVO.getStorageId());
            List<DictCacheVO.DictKV> dictKVList = CollectionUtil.list(false);
            DictCacheVO.DictKV dictKV = new DictCacheVO.DictKV();
            dictKV.setDictKey(storage.getStorageId().toString());
            dictKV.setDictValue(storage.getStorageName());
            dictKV.setDictType(EDictConstant.STORAGE_LIST);
            dictKVList.add(dictKV);
            dictCacheVO.setDictList(dictKVList);
        }
        return dictCacheVO;
    }

    /**
     * 获取用户字典数据
     * @param dictType 字典类型
     * @return 字典数据
     */
    private DictCacheVO getUserDictList(String dictType) {
        //返回结果
        DictCacheVO dictCacheVO = new DictCacheVO();
        dictCacheVO.setDictType(dictType);
        //查询用户列表
        List<MUser> userList = userMapper.selectUserDict();
        if (CollectionUtil.isNotEmpty(userList)) {
            List<DictCacheVO.DictKV> dictKVList = userList.stream().map(dept -> {
                DictCacheVO.DictKV dictKV = new DictCacheVO.DictKV();
                dictKV.setDictKey(dept.getUserId().toString());
                dictKV.setDictValue(dept.getNickname());
                dictKV.setDictType(dictType);
                return dictKV;
            }).toList();
            dictCacheVO.setDictList(dictKVList);
        }
        return dictCacheVO;
    }

}
