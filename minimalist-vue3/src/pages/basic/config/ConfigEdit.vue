<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" layout="vertical" :rules="rules" auto-label-width>
            <a-form-item class="w-[100%]" field="configName" label="参数名称" required>
                <a-input v-model="form.configName" placeholder="参数名称" />
            </a-form-item>
            <a-form-item class="w-[100%]" field="configKey" label="参数键名" required tooltip="参数的key">
                <a-input v-model="form.configKey" placeholder="参数键名" />
            </a-form-item>
            <a-form-item class="w-[100%]" field="configValue" label="参数键值" required tooltip="参数的value">
                <a-textarea v-model="form.configValue" placeholder="参数键值" />
            </a-form-item>
            <a-form-item class="w-[100%]" field="description" label="说明">
                <a-textarea v-model="form.description" placeholder="说明" />
            </a-form-item>
            <a-form-item class="w-[100%]" field="status" label="参数状态" required v-if="props.params.operationType === proxy.operationType.update.type">
                <a-select v-model="form.status" placeholder="参数状态" allow-clear>
                    <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                </a-select>
            </a-form-item>
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
import {addConfigApi, getConfigByConfigIdApi, updateConfigByConfigIdApi} from "~/api/config.js";
//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus])
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
//表单校验规则
const rules = {
    configName: [{required: true, message: '参数名称不能为空', trigger: 'submit'}],
    configKey: [{required: true, message: '参数key不能为空', trigger: 'submit'}],
    configValue: [{required: true, message: '参数value不能为空', trigger: 'submit'}],
    status: [{required: true, message: '参数状态不能为空', trigger: 'submit'}]
}
//确定 -> 点击
const okBtnClick = () => {
    //表单验证
    formRef.value.validate((valid) => {
        if (valid) {return false}
        //添加
        if (props.params.operationType === proxy.operationType.add.type) {
            spinLoading.value = true
            addConfigApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            spinLoading.value = true
            updateConfigByConfigIdApi(form).then(() => {
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
        //加载参数信息
        loadConfigInfo(props.params.configId)
    }
}, { deep: true, immediate: true })
</script>
<style scoped></style>
