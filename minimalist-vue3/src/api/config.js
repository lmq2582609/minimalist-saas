import axios from '~/axios'

//添加参数
export function addConfigApi(data) {
    return axios.post('/common/config/addConfig', data)
}

//删除参数
export function deleteConfigByConfigIdApi(configId) {
    return axios({
        method: 'DELETE',
        url: '/common/config/deleteConfigByConfigId',
        params: {
            configId: configId
        }
    })
}

//修改参数
export function updateConfigByConfigIdApi(data) {
    return axios.put('/common/config/updateConfigByConfigId', data)
}

//获取参数列表 - 分页
export function getPageConfigListApi(params) {
    return axios({
        method: 'GET',
        url: '/common/config/getPageConfigList',
        params: params
    })
}

//获取参数详细信息
export function getConfigByConfigIdApi(configId) {
    return axios.get(`/common/config/getConfigByConfigId/${configId}`)
}

//刷新参数缓存
export function refreshConfigApi() {
    return axios.get('/common/config/refreshConfigCache')
}
