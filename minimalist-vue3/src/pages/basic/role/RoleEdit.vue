<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" layout="vertical" :rules="rules" auto-label-width>
            <div class="flex justify-between" style="flex-wrap: wrap;">
                <a-form-item class="w-[49%]" field="roleName" label="角色名称" required>
                    <a-input v-model="form.roleName" placeholder="角色名称" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="roleCode" label="角色编码" required tooltip="控制器中定义的角色编码">
                    <a-input v-model="form.roleCode" placeholder="角色编码" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="roleSort" label="排序值" required>
                    <a-input-number :min="0" v-model="form.roleSort" placeholder="排序值" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="status" label="角色状态" required v-if="props.params.operationType === proxy.operationType.update.type">
                    <a-select v-model="form.status" placeholder="角色状态" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[100%]" field="remark" label="备注">
                    <a-textarea v-model="form.remark" placeholder="备注" />
                </a-form-item>
                <a-form-item class="w-[100%]" field="checkedPermIds" label="角色权限" required>
                    <a-spin class="w-[100%]" :size="35" :loading="permTreeSpinLoading" tip="正在处理, 请稍候...">
                        <a-scrollbar class="w-[100%] max-h-[250px] overflow-auto border" :outer-style="{width: '100%'}" type="track">
                            <a-tree v-model:checked-keys="form.checkedPermIds" :data="permTreeData" ref="treeRef" v-if="permTreeData.length > 0"
                                    show-line multiple checkable blockNode action-on-node-click="expand"
                                    :fieldNames="{
                                        key: 'permId',
                                        title: 'permName',
                                        children: 'children'
                                    }" />
                        </a-scrollbar>
                    </a-spin>
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
import {ref, reactive, getCurrentInstance, watch, nextTick} from 'vue'
import { addRoleApi, updateRoleByRoleIdApi, getRoleByRoleIdApi } from "~/api/role.js";
import {getTenantEnablePermListApi} from "~/api/perm.js";
import { getAllTreeParentId } from "~/utils/sys.js";

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
    //角色ID
    roleId: null,
    //角色名称
    roleName: null,
    //角色编码
    roleCode: null,
    //排序值
    roleSort: null,
    //角色状态
    status: null,
    //备注
    remark: null,
    //角色权限 -> 全勾选+半勾选
    permissionsIds: [],
    //角色权限 -> 全勾选
    checkedPermIds: []
})
//表单校验规则
const rules = {
    roleName: [{required: true, message: '角色名称不能为空', trigger: 'submit'}],
    roleCode: [{required: true, message: '角色编码不能为空', trigger: 'submit'}],
    roleSort: [{required: true, message: '排序值不能为空', trigger: 'submit'}],
    permissionsIds: [{required: true, message: '角色权限不能为空', trigger: 'submit'}],
    checkedPermIds: [{required: true, message: '角色权限不能为空', trigger: 'submit'}]
}
//确定 -> 点击
const okBtnClick = () => {
    //获取权限 -> 全勾选+半勾选
    form.permissionsIds = getPermTreeSelectData(true)
    //表单验证
    formRef.value.validate((valid) => {
        if (valid) {return false}
        //添加
        if (props.params.operationType === proxy.operationType.add.type) {
            spinLoading.value = true
            addRoleApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            spinLoading.value = true
            updateRoleByRoleIdApi(form).then(() => {
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
//获取权限树的节点 isGetHalf：是否获取半勾选节点ID
const getPermTreeSelectData = (isGetHalf) => {
    //选中的节点
    let checkedKeys = treeRef.value.getCheckedNodes()
    if (checkedKeys && checkedKeys.length > 0) {
        checkedKeys = [...new Set(checkedKeys.map(node => node.permId))]
    }
    //半选中的节点
    let halfCheckedKeys = []
    if (isGetHalf) {
        halfCheckedKeys = treeRef.value.getHalfCheckedNodes()
        if (halfCheckedKeys && halfCheckedKeys.length > 0) {
            halfCheckedKeys = [...new Set(halfCheckedKeys.map(node => node.permId))]
        }
    }
    //合并两个数组
    return [...halfCheckedKeys, ...checkedKeys]
}
//权限树数据
const permTreeData = ref([])
//权限树加载
const permTreeSpinLoading = ref(false)
//权限树ref
const treeRef = ref(null)
//权限树所有父节点ID(只要有子集，就视为是父节点)
const allParentPermId = ref([])
//获取权限数据列表
const getPermTree = (loadRole = false) => {
    permTreeSpinLoading.value = true
    getTenantEnablePermListApi().then(res => {
        //权限树数据赋值
        permTreeData.value = res
        if (loadRole) {
            //获取所有父permId
            allParentPermId.value = getAllTreeParentId(res, 'permId')
            //加载角色信息
            loadRoleInfo(props.params.roleId)
        }
    }).finally(() => {
        permTreeSpinLoading.value = false
    })
}
//加载角色详细信息
const loadRoleInfo = (roleId) => {
    spinLoading.value = true
    getRoleByRoleIdApi(roleId).then(res => {
        //数据赋值
        if (res) {
            for (let key in res) {
                if (form.hasOwnProperty(key)) {
                    if (key === 'checkedPermIds') {
                        let checkedPermIds = []
                        //处理权限回显
                        res[key].forEach(permId => {
                            //如果不是父节点，则回显为勾选
                            if (!allParentPermId.value.includes(permId)) {
                                checkedPermIds.push(permId)
                            }
                        })
                        form.checkedPermIds = checkedPermIds
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
    //角色ID
    if (props.params.roleId) {
        //加载权限树，同时加载角色详情
        getPermTree(true)
    } else {
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
