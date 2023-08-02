import axios from '~/axios'

//上传文件
export function uploadFileApi(data, onUploadProgress) {
    return axios.post('/basic/file/uploadFile', data, {
        onUploadProgress:(e)=>{
            //获取上传进度
            if (onUploadProgress) {
                onUploadProgress(e)
            }
        }
    })
}

//删除文件 -> 根据文件URL删除
export function deleteFileApi(fileId) {
    return axios({
        method: 'DELETE',
        url: '/basic/file/deleteFile',
        params: {
            fileId: fileId
        }
    })
}

//获取文件列表 - 分页
export function getPageFileListApi(params) {
    return axios({
        method: 'GET',
        url: '/basic/file/getPageFileList',
        params: params
    })
}