<template>
    <div>
        <a-form-item field="accessKey" label="访问密钥" required>
            <a-input v-model="form.accessKey" placeholder="accessKey" />
        </a-form-item>
        <a-form-item field="secretKey" label="私有密钥" required>
            <a-input v-model="form.secretKey" placeholder="secretKey" />
        </a-form-item>
        <a-form-item field="endPoint" label="域名" required tooltip="MinIO服务器的URL">
            <a-input v-model="form.endPoint" placeholder="endPoint" />
        </a-form-item>
        <a-form-item field="bucketName" label="桶名称" required>
            <a-input v-model="form.bucketName" placeholder="bucketName" />
        </a-form-item>
    </div>
</template>
<script setup>
import {getCurrentInstance, reactive, watch} from "vue";
//全局实例
const {proxy} = getCurrentInstance()
//接收父组件参数
const props = defineProps({
    params: {
        type: Object,
        default: () => {}
    }
})
//表单
const form = reactive({
    accessKey: null,
    secretKey: null,
    endPoint: null,
    bucketName: null
})
//获取存储配置
const getStorageConfig = () => {
    return JSON.stringify(form)
}
//暴露组件方法
defineExpose({getStorageConfig})
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //修改回显
    if (props.params?.operationType === proxy.operationType.update.type) {
        form.accessKey = props.params.accessKey
        form.secretKey = props.params.secretKey
        form.endPoint = props.params.endPoint
        form.bucketName = props.params.bucketName
    }
}, { deep: true, immediate: true })
</script>
<style scoped>
</style>
