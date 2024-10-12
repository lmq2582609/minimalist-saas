<template>
    <div>
        <!-- 修改时展示 -->
        <a-form-item field="storagePath" label="本地存储路径" v-if="proxy.operationType.update.type === optType"
            tooltip="指的是要将文件存放在什么位置。Windows系统，如`E:\temp`。Linux系统，如`/opt/temp`">
            <a-input v-model="form.storagePath" placeholder="本地存储路径" />
        </a-form-item>
        <div v-if="proxy.operationType.detail.type !== optType" class="font-bold mb-3">注意：本地存储路径新增或修改后，需重启项目后生效</div>

        <!-- 查看时展示 -->
        <a-descriptions :column="1" bordered v-if="proxy.operationType.detail.type === optType">
            <a-descriptions-item label="本地存储路径">{{ form.storagePath }}</a-descriptions-item>
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
    //有值则回显
    if (props.params) {
        let data = JSON.parse(props.params)
        form.storagePath = data.storagePath
    }
}, { deep: true, immediate: true })
</script>
<style scoped>
</style>
