<template>
    <div>
        <a-form-item field="storagePath" label="本地存储路径" required tooltip="指的是要将文件存放在什么位置。Windows系统，如`E:\temp`。Linux系统，如`/opt/temp`">
            <a-input v-model="form.storagePath" placeholder="本地存储路径" />
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
    //本地存储路径
    storagePath: null
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
        form.storagePath = props.params.storagePath
    }
}, { deep: true, immediate: true })
</script>
<style scoped>
</style>
