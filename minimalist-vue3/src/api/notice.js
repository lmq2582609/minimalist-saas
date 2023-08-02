import axios from '~/axios'

//添加公告
export function addNoticeApi(data) {
    return axios.post('/basic/notice/addNotice', data)
}

//删除公告
export function deleteNoticeByNoticeIdApi(noticeId) {
    return axios({
        method: 'DELETE',
        url: '/basic/notice/deleteNoticeByNoticeId',
        params: {
            noticeId: noticeId
        }
    })
}

//修改公告
export function updateNoticeByNoticeIdApi(data) {
    return axios.put('/basic/notice/updateNoticeByNoticeId', data)
}

//获取公告列表 - 分页 -> 公告管理使用
export function getPageNoticeListApi(params) {
    return axios({
        method: 'GET',
        url: '/basic/notice/getPageNoticeList',
        params: params
    })
}

//获取公告列表 - 分页 -> 首页使用
export function getPageHomeNoticeListApi(params) {
    return axios({
        method: 'GET',
        url: '/basic/notice/getPageHomeNoticeList',
        params: params
    })
}

//获取公告详细信息
export function getNoticeByNoticeIdApi(noticeId) {
    return axios.get(`/basic/notice/getNoticeByNoticeId/${noticeId}`)
}