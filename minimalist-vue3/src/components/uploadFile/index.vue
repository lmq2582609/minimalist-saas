<template>
    <div>
        <a-upload :list-type="listType" :action="uploadFileUrl" :custom-request="customUploadFile"
                  v-model:file-list="fileList" :accept="props.accept" :limit="limit"
                  image-preview with-credentials :multiple="multiple" />
    </div>
</template>

<script setup>
//封面图
import {getCurrentInstance, ref, watch, onMounted } from "vue";
import { uploadFileApi } from "~/api/file.js";

//全局实例
const {proxy} = getCurrentInstance()
//接收父组件参数
const props = defineProps({
    //文件来源 -> 若上传文件，需传入此参数，用于标识文件的作用
    fileSource: {
        type: Number,
        default: 0
    },
    //上传文件类型限制
    accept: {
        type: String,
        default: ''
    },
    //是否支持多文件上传
    multiple: {
        type: Boolean,
        default: false
    },
    //文件列表类型 -> 'text' | 'picture' | 'picture-card'
    listType: {
        type: String,
        default: 'text'
    },
    //上传限制
    limit: {
        type: Number,
        default: 5
    },
    //文件列表
    fileList: {
        type: Array,
        default: () => []
    }
})
//v-model
const fileList = ref([])
//上传文件地址
const uploadFileUrl = import.meta.env.VITE_UPLOAD_FILE_URL
//自定义上传
const customUploadFile = (option) => {
    //上传进度监控
    const onUploadProgress = (e) => {
        option.onProgress(e.progress * 100)
    }
    //上传参数
    const formData = new FormData();
    formData.append("file", option.fileItem.file);
    formData.append("fileSource", props.fileSource);
    uploadFileApi(formData, onUploadProgress).then(res => {
        //调用onSuccess方法将响应数据附加到fileItem中的response字段上
        option.onSuccess(res)
        proxy.$msg.success(proxy.operationType.upload.success)
    }).catch(e => {
        //上传失败
        option.onError(e)
    })
}
//获取上传文件的ID，逗号分割
const getUploadFileId = () => {
    let uploadFileId = []
    //上传的文件有response属性，回显的图片只有url
    let fl = fileList.value
    for (let i = 0; i < fl.length; i++) {
        //从response中取文件ID
        uploadFileId.push(fl[i].response.fileId)
    }
    return uploadFileId.join(",")
}
//暴露子组件
defineExpose({getUploadFileId})
//监听参数变化
watch(() => props.fileList, (newVal, oldVal) => {
    //先清空文件列表
    fileList.value = []
    //父组件传递文件列表回显，有值则回显
    if (props.fileList.length > 0) {
        fileList.value = props.fileList
    }
}, { deep: true, immediate: true })
</script>

<style scoped></style>
