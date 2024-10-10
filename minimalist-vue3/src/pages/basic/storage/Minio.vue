<template>
    <div>

        <!-- 修改时展示 -->
        <a-row justify="space-between" v-if="proxy.operationType.update.type === optType">
            <a-form-item class="w-[49%]" field="accessKey" label="访问密钥">
                <a-input v-model="form.accessKey" placeholder="accessKey" />
            </a-form-item>
            <a-form-item class="w-[49%]" field="secretKey" label="私有密钥">
                <a-input v-model="form.secretKey" placeholder="secretKey" />
            </a-form-item>
        </a-row>

        <a-row justify="space-between" v-if="proxy.operationType.update.type === optType">
            <a-form-item class="w-[49%]" field="endPoint" label="域名" tooltip="MinIO服务器的URL">
                <a-input v-model="form.endPoint" placeholder="endPoint" />
            </a-form-item>
            <a-form-item class="w-[49%]" field="bucketName" label="桶名称">
                <a-input v-model="form.bucketName" placeholder="bucketName" />
            </a-form-item>
        </a-row>

        <!-- 查看时展示 -->
        <a-descriptions :column="2" bordered  v-if="proxy.operationType.detail.type === optType">
            <a-descriptions-item label="访问密钥">{{ form.accessKey }}</a-descriptions-item>
            <a-descriptions-item label="私有密钥">{{ form.secretKey }}</a-descriptions-item>
            <a-descriptions-item label="域名">{{ form.endPoint }}</a-descriptions-item>
            <a-descriptions-item label="桶名称">{{ form.bucketName }}</a-descriptions-item>
        </a-descriptions>

    </div>
</template>
<script setup>
import {getCurrentInstance, reactive, watch} from "vue";
//全局实例
const {proxy} = getCurrentInstance()
//接收父组件参数
const props = defineProps({
    params: {
        type: String,
        default: () => ''
    },
    //操作类型，区分修改(可编辑)和查看(不可编辑)
    optType: {
        type: String,
        default: () => ''
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
    //有值则回显
    if (props.params) {
        let data = JSON.parse(props.params)
        form.accessKey = data.accessKey
        form.secretKey = data.secretKey
        form.endPoint = data.endPoint
        form.bucketName = data.bucketName
    }
}, { deep: true, immediate: true })
</script>
<style scoped>
</style>
