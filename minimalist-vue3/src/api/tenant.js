import axios from '~/axios'

//添加租户
export function addTenantApi(data) {
    return axios.post('/basic/tenant/addTenant', data)
}
//删除租户
export function deleteTenantByTenantIdApi(tenantId) {
    return axios({
        method: 'DELETE',
        url: '/basic/tenant/deleteTenantByTenantId',
        params: {
            tenantId: tenantId
        }
    })
}
//修改租户
export function updateTenantByTenantIdApi(data) {
    return axios.put('/basic/tenant/updateTenantByTenantId', data)
}
//获取租户列表 - 分页
export function getPageTenantListApi(params) {
    return axios({
        method: 'GET',
        url: '/basic/tenant/getPageTenantList',
        params: params
    })
}
//获取租户详细信息
export function getTenantByTenantIdApi(tenantId) {
    return axios.get(`/basic/tenant/getTenantByTenantId/${tenantId}`)
}