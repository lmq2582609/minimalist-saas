<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-descriptions :column="2" bordered>
            <a-descriptions-item label="参数名称">{{ form.configName }}</a-descriptions-item>
            <a-descriptions-item label="参数key">{{ form.configKey }}</a-descriptions-item>
            <a-descriptions-item label="参数value">{{ form.configValue }}</a-descriptions-item>
            <a-descriptions-item label="说明">{{ form.description }}</a-descriptions-item>
            <a-descriptions-item label="参数状态">
                <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="form.status" />
            </a-descriptions-item>
        </a-descriptions>
    </a-spin>
</template>
<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import {getConfigByConfigIdApi} from "~/api/config.js";
//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus])
//加载中...
const spinLoading = ref(false)
//接收父组件参数
const props = defineProps({
    params: {
        type: Object,
        default: () => {}
    }
})
//表单
const form = reactive({
    //参数ID
    configId: null,
    //参数名称
    configName: null,
    //参数key
    configKey: null,
    //参数value
    configValue: null,
    //参数状态
    status: null,
    //说明
    description: null
})
//加载参数详细信息
const loadConfigInfo = (configId) => {
    spinLoading.value = true
    getConfigByConfigIdApi(configId).then(res => {
        //数据赋值
        if (res) {
            for (let key in res) {
                if (form.hasOwnProperty(key)) {
                    form[key] = res[key]
                }
            }
        }
    }).finally(() => {
        spinLoading.value = false
    })
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //参数ID
    if (props.params.configId) {
        //查询数据
        loadConfigInfo(props.params.configId)
    }
}, { deep: true, immediate: true })
</script>
<style scoped></style>
