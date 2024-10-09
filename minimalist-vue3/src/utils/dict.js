import pinia from '../store'
import { useSysStore } from '../store/module/sys-store.js'
import { getDictByDictTypeListApi } from '../api/dict'
import { reactive, onMounted } from 'vue';

export const DICT = {
    //是/否
    yesNo: 'yes-no',
    //字典状态
    dictStatus: 'dict-status',
    //字典样式
    dictClass: 'dict-class',
    //权限状态
    permStatus: 'perm-status',
    //部门状态
    deptStatus: 'dept-status',
    //权限类型
    permType: 'perm-type',
    //角色状态
    roleStatus: 'role-status',
    //岗位状态
    postStatus: 'post-status',
    //租户状态
    tenantStatus: 'tenant-status',
    //用户状态
    userStatus: 'user-status',
    //用户性别
    userSex: 'user-sex',
    //租户套餐状态
    tenantPackageStatus: 'tenant-package-status',
    //公告状态
    noticeStatus: 'notice-status',
    //公告类型
    noticeType: 'notice-type',
    //文件状态
    fileStatus: 'file-status',
    //文件来源
    fileSource: 'file-source',
    //文件存储平台
    filePlatform: 'file-platform',
    //参数状态
    configStatus: 'config-status',
    //通用状态 - 数字
    commonNumberStatus: 'common-number-status',
    //存储类型
    storageType: 'storage-type',

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
    tenantList: 'dict-tenant-list'
}

//缓存
const sysStore = useSysStore(pinia)

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
