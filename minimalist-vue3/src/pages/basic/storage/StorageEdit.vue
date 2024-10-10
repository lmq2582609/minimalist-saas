<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" layout="vertical" :rules="rules" auto-label-width>
            <a-row justify="space-between">
                <a-form-item class="w-[49%]" field="storageName" label="存储名称" required>
                    <a-input v-model="form.storageName" placeholder="存储名称" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="storageDefault" label="是否默认使用" required tooltip="只能有一个默认的存储。如果之前已有默认的存储，选择`是`会将之前的数据置为`否`，新增的本条数据置为`是`">
                    <a-select v-model="form.storageDefault" placeholder="是否默认使用" allow-clear>
                        <!-- 目前a-option还不支持使用布尔值，所以dictKey需要转成字符串 -->
                        <!-- 所以，storageDefault回显时也要转成字符串 -->
                        <a-option v-for="(d, index) in dicts[proxy.DICT.yesNo]" :key="index" :value="String(d.dictKey)" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
            </a-row>
            <a-row justify="space-between">
                <a-form-item class="w-[49%]" field="status" label="存储状态" required v-if="props.params.operationType === proxy.operationType.update.type">
                    <a-select v-model="form.status" placeholder="存储状态" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
            </a-row>

            <a-form-item field="description" label="说明">
                <a-textarea v-model="form.description" placeholder="说明" />
            </a-form-item>

            <a-divider orientation="left">存储配置</a-divider>
            <a-row justify="space-between">
                <a-form-item class="w-[49%]" field="storageType" label="存储类型" required>
                    <a-select v-model="form.storageType" placeholder="存储类型" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.storageType]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
            </a-row>

            <!-- 本地存储 -->
            <local ref="storageConfigRef" :params="form.storageConfig" :optType="proxy.operationType.update.type" v-if="form.storageType === 'local'" />

            <!-- minio -->
            <minio ref="storageConfigRef" :params="form.storageConfig" :optType="proxy.operationType.update.type" v-if="form.storageType === 'minio'" />

        </a-form>

        <!-- 分割线 -->
        <a-divider class="mt-0" />

        <div class="flex justify-end">
            <a-space>
                <a-button @click="cancelBtnClick()">取消</a-button>
                <a-button type="primary" @click="okBtnClick()">确定</a-button>
            </a-space>
        </div>
    </a-spin>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import {addStorageApi, updateStorageByStorageIdApi, getStorageByStorageIdApi} from "~/api/storage.js";
import Local from "~/pages/basic/storage/Local.vue";
import Minio from "~/pages/basic/storage/Minio.vue";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.storageType, proxy.DICT.yesNo])
//接收父组件参数
const props = defineProps({
    params: {
        type: Object,
        default: () => {}
    }
})
//父组件函数
const emits = defineEmits(['ok', 'cancel'])
//加载中...
const spinLoading = ref(false)
//表单ref
const formRef = ref(null)
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
//表单校验规则
const rules = {
    storageName: [{required: true, message: '存储名称不能为空', trigger: 'submit'}],
    storageType: [{required: true, message: '存储类型不能为空', trigger: 'submit'}],
    storageDefault: [{required: true, message: '是否默认使用不能为空', trigger: 'submit'}],
}
//存储配置
const storageConfigRef = ref()
//确定 -> 点击
const okBtnClick = () => {
    //表单验证
    formRef.value.validate((valid) => {
        if (valid) {return false}
        if (!storageConfigRef.value) {
            return false
        }
        form.storageConfig = storageConfigRef.value.getStorageConfig()
        //添加
        if (props.params.operationType === proxy.operationType.add.type) {
            spinLoading.value = true
            addStorageApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            spinLoading.value = true
            updateStorageByStorageIdApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.update.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
    })
}
//取消 -> 点击
const cancelBtnClick = () => {
    emits('cancel')
}
//加载存储详细信息
const loadStorageInfo = (storageId) => {
    spinLoading.value = true
    getStorageByStorageIdApi(storageId).then(res => {
        //数据赋值
        if (res) {
            for (let key in res) {
                if (form.hasOwnProperty(key)) {
                    //true和false的下拉框回显，需要转成string类型
                    if (key === 'storageDefault') {
                        form[key] = String(res[key])
                    } else {
                        form[key] = res[key]
                    }
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
