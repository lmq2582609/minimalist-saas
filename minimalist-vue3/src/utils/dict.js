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
    //租户套餐列表 -> 额外字典
    tenantPackageList: 'tenant-package-list',
    //部门列表 -> 额外字典
    deptList: 'dict-dept-list',
    //用户列表 -> 额外字典
    userList: 'dict-user-list',
    //角色列表 -> 额外字典
    roleList: 'dict-role-list',
    //岗位列表 -> 额外字典
    postList: 'dict-post-list'
}

//缓存
const sysStore = useSysStore(pinia)

/**
 * 加载字典数据
 * @param dictTypeList 字典类型列表
 * @return 返回一个对象，key是字典类型，value是字典数据列表
 */
export const LoadDicts = (dictTypeList) => {
    const dicts = reactive({});
    //返回结果
    let result = {}
    //待获取的字典类型
    let arr = []
    //检查缓存中是否已有数据
    dictTypeList.forEach(dictType => {
        if (sysStore.dictData[dictType]) {
            //缓存中已有，存储到返回结果
            result[dictType] = sysStore.dictData[dictType]
        } else {
            arr.push(dictType)
        }
    })
    //如果不需要从后端获取字典，直接返回
    if (arr.length === 0) {
        Object.assign(dicts, result);
        return dicts;
    }
    //获取字典
    onMounted(async () => {
        await getDictByDictTypeListApi(arr).then(res => {
            //处理数据
            if (res && res.length > 0) {
                res.forEach(dict => {
                    let dictType = dict.dictType
                    let dictList = dict.dictList
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
                                //转数字
                                d.dictKey = Number(d.dictKey)
                            }
                        }
                    })
                    //存储到缓存
                    sysStore.setDictData(dictType, dictList)
                    //存储到返回结果
                    result[dictType] = dictList
                })
                //合并对象
                Object.assign(dicts, result)
            }
        })
    });
    return dicts
}
