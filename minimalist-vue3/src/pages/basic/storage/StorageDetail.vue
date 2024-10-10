<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-descriptions :column="2" bordered>
            <a-descriptions-item label="存储名称">{{ form.storageName }}</a-descriptions-item>
            <a-descriptions-item label="是否默认使用">
                <dict-convert :dict-data="dicts[proxy.DICT.yesNo]" :dict-key="form.storageDefault" />
            </a-descriptions-item>
            <a-descriptions-item label="存储状态">
                <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="form.status" />
            </a-descriptions-item>
            <a-descriptions-item label="存储类型">
                <dict-convert :dict-data="dicts[proxy.DICT.storageType]" :dict-key="form.storageType" />
            </a-descriptions-item>
            <a-descriptions-item label="存储说明">{{ form.description }}</a-descriptions-item>
        </a-descriptions>

        <a-divider orientation="left" style="margin-top: 2.5em;">存储配置</a-divider>
        <!-- 本地存储 -->
        <local ref="storageConfigRef" :params="form.storageConfig" :optType="proxy.operationType.detail.type" v-if="form.storageType === 'local'" />

        <!-- minio -->
        <minio ref="storageConfigRef" :params="form.storageConfig" :optType="proxy.operationType.detail.type" v-if="form.storageType === 'minio'" />
    </a-spin>
</template>
<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import {getStorageByStorageIdApi} from "~/api/storage.js";
import Local from "~/pages/basic/storage/Local.vue";
import Minio from "~/pages/basic/storage/Minio.vue";

//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.storageType, proxy.DICT.yesNo])
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
    //存储ID
    storageId: null,
    //存储名称
    storageName: null,
    //存储类型
    storageType: null,
    //是否默认使用
    storageDefault: null,
    //说明
    description: null,
    //存储配置
    storageConfig: null,
    //状态
    status: null
})
//加载岗位详细信息
const loadStorageInfo = (storageId) => {
    spinLoading.value = true
    getStorageByStorageIdApi(storageId).then(res => {
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
    //存储ID
    if (props.params.storageId) {
        //加载存储信息
        loadStorageInfo(props.params.storageId)
    }
}, { deep: true, immediate: true })
</script>
<style scoped></style>
