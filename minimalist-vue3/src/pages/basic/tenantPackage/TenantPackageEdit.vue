<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" layout="vertical" :rules="rules" auto-label-width>
            <a-form-item field="packageName" label="套餐名称" required>
                <a-input v-model="form.packageName" placeholder="套餐名称" />
            </a-form-item>
            <a-form-item field="status" label="套餐状态" required v-if="props.params.operationType === proxy.operationType.update.type">
                <a-select v-model="form.status" placeholder="套餐状态" allow-clear>
                    <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                </a-select>
            </a-form-item>
            <a-form-item field="remark" label="备注">
                <a-textarea v-model="form.remark" placeholder="备注" />
            </a-form-item>
            <a-form-item field="checkedPermIds" label="套餐权限" required>
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
import { addTenantPackageApi, updateTenantPackageByTenantPackageIdApi, getTenantPackageByTenantPackageIdApi } from "~/api/tenantPackage.js";
import { getEnablePermListApi } from "~/api/perm.js";
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
    //套餐ID
    packageId: null,
    //套餐名称
    packageName: null,
    //套餐状态
    status: null,
    //备注
    remark: null,
    //套餐权限 -> 全勾选+半勾选
    permissionsIds: [],
    //套餐权限 -> 全勾选
    checkedPermIds: []
})
//表单校验规则
const rules = {
    packageName: [{required: true, message: '套餐名称不能为空', trigger: 'submit'}],
    permissionsIds: [{required: true, message: '套餐权限不能为空', trigger: 'submit'}],
    checkedPermIds: [{required: true, message: '套餐权限不能为空', trigger: 'submit'}]
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
            addTenantPackageApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            spinLoading.value = true
            updateTenantPackageByTenantPackageIdApi(form).then(() => {
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
const getPermTree = (loadTenantPackage = false) => {
    permTreeSpinLoading.value = true
    getEnablePermListApi().then(res => {
        //权限树数据赋值
        permTreeData.value = res
        if (loadTenantPackage) {
            //获取所有父permId
            allParentPermId.value = getAllTreeParentId(res, 'permId')
            //加载套餐信息
            loadTenantPackageInfo(props.params.packageId)
        }
    }).finally(() => {
        permTreeSpinLoading.value = false
    })
}
//加载套餐详细信息
const loadTenantPackageInfo = (packageId) => {
    spinLoading.value = true
    getTenantPackageByTenantPackageIdApi(packageId).then(res => {
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
    //租户套餐ID
    if (props.params.packageId) {
        //加载权限树
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
