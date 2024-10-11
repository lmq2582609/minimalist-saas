import axios from '~/axios'

//获取字典列表 - 分页
export function getPageDictListApi(params) {
    return axios({
        method: 'GET',
        url: '/basic/dict/getPageDictList',
        params: params
    })
}

//添加字典
export function addDictApi(data) {
    return axios.post('/basic/dict/addDict', data)
}

//根据字典类型查询字典 - 单个类型
export function getDictByDictTypeApi(dictType) {
    return axios.get(`/basic/dict/getDictByDictType/${dictType}`)
}

//根据字典类型查询字典 - 多个类型
export function getDictByDictTypeListApi(dictTypes) {
    return axios.get(`/basic/dict/getDictList/${dictTypes}`)
}

//删除字典 -> 根据字典类型删除
export function deleteDictByDictTypeApi(dictType) {
    return axios({
        method: 'DELETE',
        url: '/basic/dict/deleteDictByDictType',
        params: {
            dictType: dictType
        }
    })
}

//删除字典 -> 根据字典ID删除
export function deleteDictByDictIdApi(dictId) {
    return axios({
        method: 'DELETE',
        url: '/basic/dict/deleteDictByDictId',
        params: {
            dictId: dictId
        }
    })
}

//修改字典
export function updateDictApi(data) {
    return axios.put('/basic/dict/updateDictByDictId', data)
}
