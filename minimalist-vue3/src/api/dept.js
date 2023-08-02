import axios from '~/axios'

//添加部门
export function addDeptApi(data) {
    return axios.post('/basic/dept/addDept', data)
}

//删除部门
export function deleteDeptByDeptIdApi(deptId) {
    return axios({
        method: 'DELETE',
        url: '/basic/dept/deleteDeptByDeptId',
        params: {
            deptId: deptId
        }
    })
}

//修改部门
export function updateDeptByDeptIdApi(data) {
    return axios.put('/basic/dept/updateDeptByDeptId', data)
}

//获取部门树 -> 部门管理中使用
export function getDeptListApi(params) {
    return axios({
        method: 'GET',
        url: '/basic/dept/getDeptList',
        params: params
    })
}

//获取部门树(只查询状态为正常的部门)
export function getEnableDeptListApi() {
    return axios.get("/basic/dept/getEnableDeptList")
}

//获取部门详细信息
export function getDeptByDeptIdApi(deptId) {
    return axios.get(`/basic/dept/getDeptByDeptId/${deptId}`)
}