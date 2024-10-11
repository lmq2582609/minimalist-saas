<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" layout="vertical" :rules="rules" auto-label-width>
            <div class="flex justify-between" style="flex-wrap: wrap;">
                <a-form-item class="w-[49%]" field="username" label="用户账号" required tooltip="用户登录系统使用的账号">
                    <a-input v-model="form.username" placeholder="用户账号" />
                </a-form-item>
                <!-- 新增时，可以手动输入密码 -->
                <a-form-item class="w-[49%]" field="password" label="用户密码" v-if="props.params.operationType === proxy.operationType.add.type">
                    <a-input-password v-model="form.password" placeholder="用户密码" />
                </a-form-item>
                <!-- 修改时，只询问是否重置密码，选择重置密码后，才能手动输入密码 -->
                <a-form-item class="w-[49%]" row-class="form-label" field="password" v-else-if="props.params.operationType === proxy.operationType.update.type">
                    <template #label>
                        <template class="flex items-center justify-between">
                            <span>
                                用户密码
                                <a-tooltip content="勾选重置密码后可输入密码进行重置，不勾选表示不修改密码">
                                    <icon-question-circle style="color: var(--color-text-4)" />
                                </a-tooltip>
                            </span>
                            <a-checkbox v-model="isResetPassword" :value="true" @change="isResetPasswordChange">重置密码</a-checkbox>
                        </template>
                    </template>
                    <!-- 密码框 -->
                    <a-input-password v-model="form.password" placeholder="用户密码" :disabled="!isResetPassword" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="nickname" label="用户昵称" required tooltip="用户的网名">
                    <a-input v-model="form.nickname" placeholder="用户昵称" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="userRealName" label="用户真实姓名" required>
                    <a-input v-model="form.userRealName" placeholder="用户真实姓名" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="phone" label="手机号" required>
                    <a-input v-model="form.phone" placeholder="手机号" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="email" label="邮箱">
                    <a-input v-model="form.email" placeholder="邮箱" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="userSex" label="用户性别" required>
                    <a-select v-model="form.userSex" placeholder="用户性别" allow-clear allow-search>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.userSex]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[49%]" field="status" label="用户状态" v-if="props.params.operationType === proxy.operationType.update.type">
                    <a-select v-model="form.status" placeholder="用户状态" allow-clear allow-search>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[100%]" field="remark" label="备注">
                    <a-textarea v-model="form.remark" placeholder="备注" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="roleIds" label="用户角色" required tooltip="用户在系统中的角色，用于关联用户的菜单和权限，一个用户可以有多个角色，支持多选">
                    <a-select v-model="form.roleIds" placeholder="用户角色" multiple :max-tag-count="2" allow-clear allow-search>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.roleList]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[49%]" field="postIds" label="用户岗位">
                    <a-select v-model="form.postIds" placeholder="用户岗位" multiple :max-tag-count="2" allow-clear allow-search>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.postList]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[100%]" field="checkedDeptIds" label="所在部门">
                    <a-spin class="w-[100%]" :size="35" :loading="deptTreeSpinLoading" tip="正在处理, 请稍候...">
                        <a-scrollbar class="w-[100%] max-h-[250px] overflow-auto border" :outer-style="{width: '100%'}" type="track">
                            <a-tree v-model:checked-keys="form.checkedDeptIds" :data="deptTreeData" ref="treeRef" v-if="deptTreeData.length > 0"
                                    show-line multiple checkable blockNode action-on-node-click="expand" allow-search
                                    :fieldNames="{
                                        key: 'deptId',
                                        title: 'deptName',
                                        children: 'children'
                                    }" />
                            <a-empty v-else />
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
import { getUserByUserIdApi, addUserApi, updateUserByUserIdApi } from "~/api/user.js";
import { getEnableDeptListApi } from "~/api/dept.js";
import { getAllTreeParentId } from "~/utils/sys.js";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.userSex, proxy.DICT.roleList, proxy.DICT.postList])
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
//是否重置密码
const isResetPassword = ref(false)
//是否重置密码 change事件
const isResetPasswordChange = (val) => {
    isResetPassword.value = val
}
//表单
const form = reactive({
    //用户ID
    userId: null,
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
    userSex: null,
    //用户状态
    status: null,
    //备注
    remark: null,
    //角色ID集合
    roleIds: [],
    //岗位ID集合
    postIds: [],
    //部门ID集合 -> 全勾选+半勾选
    deptIds: [],
    //部门 -> 全勾选
    checkedDeptIds: []
})
//表单校验规则
const rules = {
    username: [{required: true, message: '用户账号不能为空', trigger: 'submit'}],
    nickname: [{required: true, message: '用户昵称不能为空', trigger: 'submit'}],
    userRealName: [{required: true, message: '用户真实姓名不能为空', trigger: 'submit'}],
    phone: [{required: true, message: '用户手机号不能为空', trigger: 'submit'}],
    userSex: [{required: true, message: '用户性别不能为空', trigger: 'submit'}],
    roleIds: [{required: true, message: '用户角色不能为空', trigger: 'submit'}]
}
//确定 -> 点击
const okBtnClick = () => {
    //获取部门 -> 全勾选+半勾选
    form.deptIds = getDeptTreeSelectData(true)
    //表单验证
    formRef.value.validate((valid) => {
        if (valid) {return false}
        //添加
        if (props.params.operationType === proxy.operationType.add.type) {
            spinLoading.value = true
            addUserApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            spinLoading.value = true
            updateUserByUserIdApi(form).then(() => {
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
//获取部门树的节点 isGetHalf：是否获取半勾选节点ID
const getDeptTreeSelectData = (isGetHalf) => {
    //选中的节点
    let checkedKeys = []
    if (treeRef.value) {
        checkedKeys = treeRef.value.getCheckedNodes()
        if (checkedKeys && checkedKeys.length > 0) {
            checkedKeys = [...new Set(checkedKeys.map(node => node.deptId))]
        }
    }
    //半选中的节点
    let halfCheckedKeys = []
    if (isGetHalf && treeRef.value) {
        halfCheckedKeys = treeRef.value.getHalfCheckedNodes()
        if (halfCheckedKeys && halfCheckedKeys.length > 0) {
            halfCheckedKeys = [...new Set(halfCheckedKeys.map(node => node.deptId))]
        }
    }
    //合并两个数组
    return [...halfCheckedKeys, ...checkedKeys]
}
//部门树数据
const deptTreeData = ref([])
//部门树加载
const deptTreeSpinLoading = ref(false)
//部门树所有父节点ID(只要有子集，就视为是父节点)
const allParentDeptId = ref([])
//部门树ref
const treeRef = ref(null)
//获取部门数据列表
const getDeptTree = (loadUser = false) => {
    deptTreeSpinLoading.value = true
    getEnableDeptListApi().then(res => {
        //部门树数据赋值
        deptTreeData.value = res
        if (loadUser) {
            //获取所有父deptId
            allParentDeptId.value = getAllTreeParentId(res, 'deptId')
            //加载用户信息
            loadUserInfo(props.params.userId)
        }
    }).finally(() => {
        deptTreeSpinLoading.value = false
    })
}
//加载用户详细信息
const loadUserInfo = (userId) => {
    spinLoading.value = true
    getUserByUserIdApi(userId).then(res => {
        //数据赋值
        if (res) {
            for (let key in res) {
                if (form.hasOwnProperty(key)) {
                    if (key === 'checkedDeptIds') {
                        let checkedDeptIds = []
                        //处理部门回显
                        res[key].forEach(deptId => {
                            //如果不是父节点，则回显为勾选
                            if (!allParentDeptId.value.includes(deptId)) {
                                checkedDeptIds.push(deptId)
                            }
                        })
                        form.checkedDeptIds = checkedDeptIds
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
    //部门ID
    if (props.params.userId) {
        //加载部门树
        getDeptTree(true)
    } else {
        //加载部门树
        getDeptTree()
    }
}, { deep: true, immediate: true })
</script>
<style scoped>
/* 树形组件点击会高亮，容易引起歧义，此处取消高亮样式 */
:deep(.arco-tree-node-selected .arco-tree-node-title, .arco-tree-node-selected .arco-tree-node-title:hover) {
    color: rgb(var(--color-text-1));
}
.form-label :deep(.arco-form-item-label) {
    @apply w-[100%];
}
</style>
