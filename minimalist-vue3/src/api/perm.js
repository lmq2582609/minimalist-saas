import axios from '~/axios'

//权限类型
export const permType = {
    M: {key: 'M', value: '菜单'},
    B: {key: 'B', value: '按钮'}
}

//权限状态
export const permStatus = {
    disable: {key: 0, value: '禁用'},
    enable: {key: 1, value: '启用'}
}

//添加权限
export function addPermApi(data) {
    return axios.post('/basic/permission/addPerm', data)
}

//删除权限
export function deletePermByPermIdApi(permId) {
    return axios({
        method: 'DELETE',
        url: '/basic/permission/deletePermByPermId',
        params: {
            permId: permId
        }
    })
}

//修改权限
export function updatePermByPermIdApi(data) {
    return axios.put('/basic/permission/updatePermByPermId', data)
}

//获取权限树
export function getPermListApi(params) {
    return axios({
        method: 'GET',
        url: '/basic/permission/getPermList',
        params: params
    })
}


//获取权限树(只查询启用的权限)
export function getEnablePermListApi() {
    return axios.get("/basic/permission/getEnablePermList")
}

//获取权限详细信息
export function getPermByPermIdApi(permId) {
    return axios.get(`/basic/permission/getPermByPermId/${permId}`)
}