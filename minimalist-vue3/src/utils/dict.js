import { getDictByDictTypeListApi } from '../api/dict'
import { reactive, onMounted } from 'vue';

export const DICT = {
    //是/否
    yesNo: 'yes-no',
    //字典样式
    dictClass: 'dict-class',
    //权限类型
    permType: 'perm-type',
    //用户性别
    userSex: 'user-sex',
    //公告类型
    noticeType: 'notice-type',
    //文件来源
    fileSource: 'file-source',
    //通用状态 - 数字
    commonNumberStatus: 'common-number-status',
    //存储类型
    storageType: 'storage-type',
    //多租户数据隔离方式
    tenantDataIsolation: 'tenant-data-isolation',

    /**************** 额外字典 ***************/
    //租户套餐列表 -> 额外字典
    tenantPackageList: 'tenant-package-list',
    //部门列表 -> 额外字典
    deptList: 'dict-dept-list',
    //用户列表 -> 额外字典
    userList: 'dict-user-list',
    //角色列表 -> 额外字典
    roleList: 'dict-role-list',
    //岗位列表 -> 额外字典
    postList: 'dict-post-list',
    //租户列表 -> 额外字典
    tenantList: 'dict-tenant-list',
    //存储列表 -> 额外字典
    storageList: 'storage-list'
}

/**
 * 加载字典数据
 * @param dictTypeList 字典类型列表
 * @return 返回一个对象，key是字典类型，value是字典数据列表
 */
export const LoadDicts = (dictTypeList) => {
    let result = reactive({})
    if (!dictTypeList || dictTypeList.length === 0) {
        return result;
    }
    //获取字典
    onMounted(async () => {
        await getDictByDictTypeListApi(dictTypeList).then(res => {
            //处理数据
            if (res && res.length > 0) {
                res.forEach(dict => {
                    let dictType = dict.dictType
                    let dictList = dict.dictList || []
                    dictList.forEach(d => {
                        if (/^true|false$/.test(d.dictKey)) {
                            //转布尔
                            d.dictKey = d.dictKey === 'true'
                        } else if (isNaN(d.dictKey)) {
                            //转字符串
                            d.dictKey = String(d.dictKey)
                        } else {
                            if (d.dictKey.length > 13) {
                                //数字超过13位，转字符串
                                d.dictKey = String(d.dictKey)
                            } else {
                                if (d.dictKey instanceof String) {
                                    d.dictKey = String(d.dictKey)
                                } else {
                                    //转数字
                                    d.dictKey = Number(d.dictKey)
                                }
                            }
                        }
                    })
                    //存储到返回结果
                    result[dictType] = dictList
                })
            }
        })
    });
    return result
}
