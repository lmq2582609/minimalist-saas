<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-descriptions :column="2" bordered>
            <a-descriptions-item label="租户名称">{{ form.tenantName }}</a-descriptions-item>
            <a-descriptions-item label="租户套餐">
                <dict-convert :dict-data="dicts[proxy.DICT.tenantPackageList]" :dict-key="form.packageId" />
            </a-descriptions-item>
            <a-descriptions-item label="联系人">{{ form.contactName }}</a-descriptions-item>
            <a-descriptions-item label="联系人手机">{{ form.phone }}</a-descriptions-item>
            <a-descriptions-item label="联系人邮箱">{{ form.email }}</a-descriptions-item>
            <a-descriptions-item label="账号额度">{{ form.accountCount }}</a-descriptions-item>
            <a-descriptions-item label="过期时间">{{ form.expireTime }}</a-descriptions-item>
            <a-descriptions-item label="数据隔离方式">
                <dict-convert :dict-data="dicts[proxy.DICT.tenantDataIsolation]" :dict-key="form.dataIsolation" />
            </a-descriptions-item>
            <a-descriptions-item label="数据源名称">{{ form.datasource }}</a-descriptions-item>
            <a-descriptions-item label="文件存储方式">
                <dict-convert :dict-data="dicts[proxy.DICT.storageList]" :dict-key="form.storageId" />
            </a-descriptions-item>
            <a-descriptions-item label="租户状态">
                <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="form.status" />
            </a-descriptions-item>
            <a-descriptions-item label="备注">{{ form.remark }}</a-descriptions-item>
        </a-descriptions>
    </a-spin>
</template>
<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import { getTenantByTenantIdApi } from "~/api/tenant.js";

//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.tenantPackageList, proxy.DICT.tenantDataIsolation, proxy.DICT.storageList])
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
    //租户ID
    tenantId: null,
    //租户名称
    tenantName: null,
    //租户套餐
    packageId: null,
    //联系人
    contactName: null,
    //联系人手机
    phone: null,
    //联系人邮箱
    email: null,
    //账号额度
    accountCount: null,
    //过期时间
    expireTime: null,
    // 数据隔离方式
    dataIsolation: null,
    //数据源名称
    datasource: null,
    //存储ID
    storageId: null,
    //租户状态
    status: null,
    //备注
    remark: null
})
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
        }
    }).finally(() => {
        spinLoading.value = false
    })
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //租户ID
    if (props.params.tenantId) {
        //查询数据
        loadTenantInfo(props.params.tenantId)
    }
}, { deep: true, immediate: true })
</script>
<style scoped></style>
