<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" layout="vertical" :rules="rules" auto-label-width>
            <div class="flex justify-between" style="flex-wrap: wrap;">
                <a-form-item class="w-[49%]" field="tenantName" label="租户名称" required>
                    <a-input v-model="form.tenantName" placeholder="租户名称" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="packageId" label="租户套餐" required>
                    <a-select v-model="form.packageId" placeholder="租户套餐" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.tenantPackageList]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[49%]" field="userId" label="联系人" required>
                    <a-select v-model="form.userId" placeholder="联系人" allow-clear allow-search>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.userAllList]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[49%]" field="expireTime" label="过期时间" required>
                    <a-date-picker class="w-[100%]" v-model="form.expireTime" show-time format="YYYY-MM-DD HH:mm:ss" disabled-input placeholder="过期时间" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="accountCount" label="账号额度" required tooltip="表示该租户下可以创建多少个用户账号">
                    <a-input-number v-model="form.accountCount" :min="0" placeholder="账号额度" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="storeCount" label="门店额度" tooltip="默认1家门店，表示这个租户下可以创建多少家门店或者店铺，若系统中没有门店或店铺的概念，可忽略此字段">
                    <a-input-number v-model="form.storeCount" :min="1" placeholder="门店额度" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="status" label="租户状态" required>
                    <a-select v-model="form.status" placeholder="租户状态" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.tenantStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[100%]" field="remark" label="备注">
                    <a-textarea v-model="form.remark" placeholder="备注" />
                </a-form-item>
            </div>
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
import {ref, reactive, getCurrentInstance, watch} from 'vue'
import { addTenantApi, updateTenantByTenantIdApi, getTenantByTenantIdApi } from "~/api/tenant.js";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.tenantStatus, proxy.DICT.userAllList, proxy.DICT.tenantPackageList])
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
    //租户ID
    tenantId: null,
    //租户名称
    tenantName: null,
    //租户套餐
    packageId: null,
    //联系人
    userId: null,
    //账号额度
    accountCount: null,
    //门店额度
    storeCount: null,
    //过期时间
    expireTime: null,
    //租户状态
    status: null,
    //备注
    remark: null
})
//表单校验规则
const rules = {
    tenantName: [{required: true, message: '租户名称不能为空', trigger: 'submit'}],
    packageId: [{required: true, message: '租户套餐不能为空', trigger: 'submit'}],
    userId: [{required: true, message: '联系人不能为空', trigger: 'submit'}],
    accountCount: [{required: true, message: '账号额度不能为空', trigger: 'submit'}],
    expireTime: [{required: true, message: '过期时间不能为空', trigger: 'submit'}],
    status: [{required: true, message: '租户状态不能为空', trigger: 'submit'}]
}
//确定 -> 点击
const okBtnClick = () => {
    //表单验证
    formRef.value.validate((valid) => {
        if (valid) {return false}
        //添加
        if (props.params.operationType === proxy.operationType.add.type) {
            spinLoading.value = true
            addTenantApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            spinLoading.value = true
            updateTenantByTenantIdApi(form).then(() => {
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
//加载租户详细信息
const loadTenantInfo = (tenantId) => {
    spinLoading.value = true
    getTenantByTenantIdApi(tenantId).then(res => {
        //数据赋值
        if (res) {
            for (let key in res) {
                if (form.hasOwnProperty(key)) {
                    form[key] = res[key]
                }
            }
            form.packageId = form.packageId === '0' ? Number(form.packageId) : String(form.packageId)
        }
    }).finally(() => {
        spinLoading.value = false
    })
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //租户ID
    if (props.params.tenantId) {
        //加载租户信息
        loadTenantInfo(props.params.tenantId)
    }
}, { deep: true, immediate: true })
</script>
<style scoped></style>
