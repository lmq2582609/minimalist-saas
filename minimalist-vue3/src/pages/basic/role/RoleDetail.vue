<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-descriptions :column="2" bordered>
            <a-descriptions-item label="角色名称">{{ form.roleName }}</a-descriptions-item>
            <a-descriptions-item label="角色编码">{{ form.roleCode }}</a-descriptions-item>
            <a-descriptions-item label="排序">{{ form.roleSort }}</a-descriptions-item>
            <a-descriptions-item label="角色状态">
                <dict-convert :dict-data="dicts[proxy.DICT.roleStatus]" :dict-key="form.status" />
            </a-descriptions-item>
            <a-descriptions-item label="备注">{{ form.remark }}</a-descriptions-item>
        </a-descriptions>

        <a-descriptions :column="2" bordered class="mt-3">
            <a-descriptions-item :span="2" label="角色权限">
                <a-spin class="w-[100%]" :size="35" :loading="permTreeSpinLoading" tip="正在处理, 请稍候...">
                    <a-scrollbar class="w-[100%] max-h-[250px] overflow-auto" :outer-style="{width: '100%'}" type="track">
                        <a-tree v-model:checked-keys="form.checkedPermIds" :data="permTreeData" v-if="permTreeData.length > 0"
                                show-line checkable multiple blockNode :selectable="false"
                                :fieldNames="{
                                    key: 'permId',
                                    title: 'permName',
                                    children: 'children'
                                }" />
                    </a-scrollbar>
                </a-spin>
            </a-descriptions-item>
        </a-descriptions>
    </a-spin>
</template>
<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import { getRoleByRoleIdApi } from "~/api/role.js";
import { getEnablePermListApi } from "~/api/perm.js";

//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.roleStatus])
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
    //角色ID
    roleId: null,
    //角色名称
    roleName: null,
    //角色编码
    roleCode: null,
    //排序值
    roleSort: 0,
    //角色状态
    status: null,
    //备注
    remark: null,
    //角色权限 -> 全勾选
    checkedPermIds: []
})
//加载角色详细信息
const loadRoleInfo = (roleId) => {
    spinLoading.value = true
    getRoleByRoleIdApi(roleId).then(res => {
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
//权限树数据
const permTreeData = ref([])
//权限树加载
const permTreeSpinLoading = ref(false)
//获取权限数据列表
const getPermTree = () => {
    permTreeSpinLoading.value = true
    getEnablePermListApi().then(res => {
        //权限树数据赋值
        permTreeData.value = res
    }).finally(() => {
        permTreeSpinLoading.value = false
    })
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //角色ID
    if (props.params.roleId) {
        //查询数据
        loadRoleInfo(props.params.roleId)
        //加载权限树
        getPermTree()
    }
}, { deep: true, immediate: true })
</script>
<style scoped>
/* 树形组件点击会高亮，容易引起歧义，此处取消高亮样式 */
:deep(.arco-tree-node-selected .arco-tree-node-title, .arco-tree-node-selected .arco-tree-node-title:hover) {
    color: rgb(var(--color-text-1));
}
</style>