import axios from '~/axios'

//查询所有功能配置
export function getFuncConfigListApi() {
    return axios.get('/basic/funcConfig/getFuncConfigList')
}

//修改功能配置
export function updateFuncConfigApi(data) {
    return axios.put('/basic/funcConfig/updateFuncConfig', data)
}
