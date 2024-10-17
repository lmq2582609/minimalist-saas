import axios from '~/axios'

//添加参数
export function addConfigApi(data) {
    return axios.post('/basic/config/addConfig', data)
}

//删除参数
export function deleteConfigByConfigIdApi(configId) {
    return axios({
        method: 'DELETE',
        url: '/basic/config/deleteConfigByConfigId',
        params: {
            configId: configId
        }
    })
}

//修改参数
export function updateConfigByConfigIdApi(data) {
    return axios.put('/basic/config/updateConfigByConfigId', data)
}

//获取参数列表 - 分页
export function getPageConfigListApi(params) {
    return axios({
        method: 'GET',
        url: '/basic/config/getPageConfigList',
        params: params
    })
}

//获取参数详细信息
export function getConfigByConfigIdApi(configId) {
    return axios.get(`/basic/config/getConfigByConfigId/${configId}`)
}

