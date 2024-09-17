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
                <a-form-item class="w-[49%]" field="expireTime" label="过期时间" required>
                    <a-date-picker class="w-[100%]" v-model="form.expireTime" show-time format="YYYY-MM-DD HH:mm:ss" disabled-input placeholder="过期时间" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="accountCount" label="账号额度" required tooltip="表示该租户下可以创建多少个用户账号">
                    <a-input-number v-model="form.accountCount" :min="0" placeholder="账号额度" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="status" label="租户状态" v-if="props.params.operationType === proxy.operationType.update.type">
                    <a-select v-model="form.status" placeholder="租户状态" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.tenantStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[100%]" field="remark" label="备注">
                    <a-textarea v-model="form.remark" placeholder="备注" />
                </a-form-item>

                <!-- 租户与用户绑定 -->
                <a-divider orientation="left" v-if="props.params.operationType === proxy.operationType.add.type">
                    租户的用户信息
                </a-divider>

                <!-- 新增时，添加用户信息 -->
                <a-form-item class="w-[49%]" field="username" label="用户账号" tooltip="用户登录系统使用的账号" v-if="props.params.operationType === proxy.operationType.add.type">
                    <a-input v-model="form.user.username" placeholder="用户账号" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="password" label="用户密码" v-if="props.params.operationType === proxy.operationType.add.type">
                    <a-input-password v-model="form.user.password" placeholder="用户密码" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="nickname" label="用户昵称" tooltip="用户的网名" v-if="props.params.operationType === proxy.operationType.add.type">
                    <a-input v-model="form.user.nickname" placeholder="用户昵称" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="userRealName" label="用户真实姓名" v-if="props.params.operationType === proxy.operationType.add.type">
                    <a-input v-model="form.user.userRealName" placeholder="用户真实姓名" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="phone" label="手机号" v-if="props.params.operationType === proxy.operationType.add.type">
                    <a-input v-model="form.user.phone" placeholder="手机号" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="email" label="邮箱" v-if="props.params.operationType === proxy.operationType.add.type">
                    <a-input v-model="form.user.email" placeholder="邮箱" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="userSex" label="用户性别" v-if="props.params.operationType === proxy.operationType.add.type">
                    <a-select v-model="form.user.userSex" placeholder="用户性别" allow-clear allow-search>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.userSex]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
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
const dicts = proxy.LoadDicts([proxy.DICT.tenantStatus, proxy.DICT.tenantPackageList, proxy.DICT.userSex])
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
    //过期时间
    expireTime: null,
    //租户状态
    status: null,
    //备注
    remark: null,
    //用户信息，新增租户时填充数据
    user: {
        //用户账号
        username: null,
        //用户密码
        password: null,
        //用户昵称
        nickname: null,
        //用户真实姓名
        userRealName: null,
        //手机号
        phone: null,
        //邮箱
        email: null,
        //用户性别
        userSex: null
    }
})
//表单校验规则
const rules = {
    tenantName: [{required: true, message: '租户名称不能为空', trigger: 'submit'}],
    packageId: [{required: true, message: '租户套餐不能为空', trigger: 'submit'}],
    accountCount: [{required: true, message: '账号额度不能为空', trigger: 'submit'}],
    expireTime: [{required: true, message: '过期时间不能为空', trigger: 'submit'}],
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
