<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-descriptions :column="2" bordered>
            <a-descriptions-item label="部门名称">{{ form.deptName }}</a-descriptions-item>
            <a-descriptions-item label="上级部门">
                <template v-if="form.parentDeptId === '0'">
                    顶级
                </template>
                <template v-else>
                    <dict-convert :dict-data="dicts[proxy.DICT.deptList]" :dict-key="form.parentDeptId" />
                </template>
            </a-descriptions-item>
            <a-descriptions-item label="排序">{{ form.deptSort }}</a-descriptions-item>
            <a-descriptions-item label="部门负责人">
                <dict-convert :dict-data="dicts[proxy.DICT.userList]" :dict-key="form.deptLeader" />
            </a-descriptions-item>
            <a-descriptions-item label="部门电话">{{ form.phone }}</a-descriptions-item>
            <a-descriptions-item label="部门邮箱">{{ form.email }}</a-descriptions-item>
            <a-descriptions-item label="部门状态">
                <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="form.status" />
            </a-descriptions-item>
        </a-descriptions>
    </a-spin>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import {getDeptByDeptIdApi} from "~/api/dept.js";

//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.userList, proxy.DICT.deptList])
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
    //部门ID
    deptId: null,
    //上级部门ID
    parentDeptId: null,
    //部门名称
    deptName: null,
    //排序值
    deptSort: null,
    //部门负责人
    deptLeader: null,
    //部门电话
    phone: null,
    //部门邮箱
    email: null,
    //部门状态
    status: null,
})
//加载部门详细信息
const loadDeptInfo = (deptId) => {
    spinLoading.value = true
    getDeptByDeptIdApi(deptId).then(res => {
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
    //部门ID
    if (props.params.deptId) {
        //查询数据
        loadDeptInfo(props.params.deptId)
    }
}, { deep: true, immediate: true })
</script>

<style scoped>

</style>
