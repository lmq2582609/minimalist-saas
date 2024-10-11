<template>
    <a-spin class="w-[100%]" :size="35" :loading="loading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" layout="vertical" :rules="rules" auto-label-width>
            <div class="flex justify-between" style="flex-wrap: wrap;">
                <a-form-item class="w-[100%]" field="parentDeptId" label="上级部门" tooltip="选择全部表示顶级部门">
                    <a-tree-select v-model="form.parentDeptId" :data="deptTree" placeholder="上级部门" allow-clear
                        :fieldNames="{key: 'deptId', title: 'deptName', children: 'children'}" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="deptName" label="部门名称" required>
                    <a-input v-model="form.deptName" placeholder="部门名称" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="deptSort" label="排序值" required tooltip="展示顺序，按照数值升序排序">
                    <a-input-number :min="0" v-model="form.deptSort" placeholder="排序值" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="deptLeader" label="部门负责人" required>
                    <a-select v-model="form.deptLeader" placeholder="部门负责人" allow-clear allow-search>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.userList]" :key="index" :value="String(d.dictKey)" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[49%]" field="phone" label="部门电话" required>
                    <a-input v-model="form.phone" placeholder="部门电话" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="email" label="部门邮箱">
                    <a-input v-model="form.email" placeholder="部门邮箱" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="status" label="部门状态" required v-if="props.params.operationType === proxy.operationType.update.type">
                    <a-select v-model="form.status" placeholder="部门状态" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
            </div>
        </a-form>

        <!-- 分割线 -->
        <a-divider class="mt-0" />

        <div class="flex justify-end">
            <a-space>
                <a-button @click="cancelBtnClick">取消</a-button>
                <a-button type="primary" @click="okBtnClick">确定</a-button>
            </a-space>
        </div>
    </a-spin>
</template>

<script setup>
import {ref, reactive, getCurrentInstance, watch, onMounted} from 'vue'
import { getDeptListApi, getDeptByDeptIdApi, addDeptApi, updateDeptByDeptIdApi } from '~/api/dept'

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.userList])
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
const loading = ref(false)
//表单ref
const formRef = ref(null)
//表单
const form = reactive({
    //部门ID
    deptId: null,
    //上级部门ID
    parentDeptId: "0",
    //部门名称
    deptName: null,
    //排序值
    deptSort: 0,
    //部门负责人
    deptLeader: null,
    //部门电话
    phone: null,
    //部门邮箱
    email: null,
    //部门状态
    status: null,
})
//表单验证规则
const rules = {
    parentDeptId: [{required: true, message: '上级部门不能为空', trigger: 'submit'}],
    deptName: [{required: true, message: '部门名称不能为空', trigger: 'submit'}],
    deptSort: [{required: true, message: '排序值不能为空', trigger: 'submit'}],
    deptLeader: [{required: true, message: '部门负责人不能为空', trigger: 'submit'}],
    phone: [{required: true, message: '部门电话不能为空', trigger: 'submit'}],
    email: [{required: true, message: '部门邮箱不能为空', trigger: 'submit'}],
}
//确定 -> 点击
const okBtnClick = () => {
    //表单验证
    formRef.value.validate((valid) => {
        if (valid) {return false}
        //添加
        if (props.params.operationType === proxy.operationType.add.type) {
            loading.value = true
            addDeptApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                loading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            loading.value = true
            updateDeptByDeptIdApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.update.success)
                emits('ok')
            }).finally(() => {
                loading.value = false
            })
        }
    })
}
//取消 -> 点击
const cancelBtnClick = () => {
    emits('cancel')
}
//部门树数据
const deptTree = ref([])
//获取部门数据列表
const getDeptTree = () => {
    getDeptListApi({}).then(res => {
        //部门树数据处理，将数据挂载到"全部"下边
        deptTree.value = [{deptId: '0', deptName: '全部', children: res}]
    })
}
//加载部门详细信息
const loadDeptInfo = (deptId) => {
    loading.value = true
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
        loading.value = false
    })
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //如果有deptId参数，则是 "修改"
    if (props.params.deptId) {
        loadDeptInfo(props.params.deptId)
    }
    //如果有parentDeptId参数，则是 表格行 "添加"
    if (props.params.parentDeptId) {
        //给上级部门ID赋值
        form.parentDeptId = props.params.parentDeptId
    }
    //加载部门树
    getDeptTree()
}, { deep: true, immediate: true })
</script>
<style scoped></style>
