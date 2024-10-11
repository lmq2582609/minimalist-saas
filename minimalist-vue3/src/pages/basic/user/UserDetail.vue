<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-descriptions :column="2" bordered>
            <a-descriptions-item label="用户账号">{{ form.username }}</a-descriptions-item>
            <a-descriptions-item label="用户昵称">{{ form.nickname }}</a-descriptions-item>
            <a-descriptions-item label="用户姓名">{{ form.userRealName }}</a-descriptions-item>
            <a-descriptions-item label="用户手机">{{ form.phone }}</a-descriptions-item>
            <a-descriptions-item label="用户邮箱">{{ form.email }}</a-descriptions-item>
            <a-descriptions-item label="用户性别">
                <dict-convert :dict-data="dicts[proxy.DICT.userSex]" :dict-key="form.userSex" />
            </a-descriptions-item>
            <a-descriptions-item label="用户状态">
                <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="form.status" />
            </a-descriptions-item>
            <a-descriptions-item label="用户角色">
                <template v-for="(roleId, index) in form.roleIds" :key="index">
                    <dict-convert :dict-data="dicts[proxy.DICT.roleList]" :dict-key="roleId" />
                    {{ index < form.roleIds.length - 1 ? ', ' : '' }}
                </template>
            </a-descriptions-item>
            <a-descriptions-item label="用户岗位">
                <template v-for="(postId, index) in form.postIds" :key="index">
                    <dict-convert :dict-data="dicts[proxy.DICT.postList]" :dict-key="postId" />
                    {{index < form.postIds.length - 1 ? '   ' : ''}}
                </template>
            </a-descriptions-item>
        </a-descriptions>

        <a-descriptions :column="2" bordered class="mt-3">
            <a-descriptions-item :span="2" label="所属部门">
                <a-spin class="w-[100%]" :size="35" :loading="deptTreeSpinLoading" tip="正在处理, 请稍候...">
                    <a-scrollbar class="w-[100%] max-h-[250px] overflow-auto" :outer-style="{width: '100%'}" type="track">
                        <a-tree v-model:checked-keys="form.checkedDeptIds" :data="deptTreeData" v-if="deptTreeData.length > 0"
                                show-line checkable multiple blockNode :selectable="false"
                                :fieldNames="{
                                    key: 'deptId',
                                    title: 'deptName',
                                    children: 'children'
                                }" />
                    </a-scrollbar>
                </a-spin>
            </a-descriptions-item>
        </a-descriptions>
    </a-spin>
</template>
<script setup>
import {ref, reactive, getCurrentInstance, watch, nextTick} from 'vue'
import { getUserByUserIdApi } from "~/api/user.js";
import { getEnableDeptListApi } from "~/api/dept.js";
import {getAllTreeParentId} from "~/utils/sys.js";

//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.userSex, proxy.DICT.roleList, proxy.DICT.postList])
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
    //用户ID
    userId: null,
    //用户账号
    username: null,
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
    //部门 -> 全勾选
    checkedDeptIds: []
})
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
//部门树加载
const deptTreeSpinLoading = ref(false)
//部门树数据
const deptTreeData = ref([])
//部门树所有父节点ID(只要有子集，就视为是父节点)
const allParentDeptId = ref([])
//获取部门数据列表
const getDeptTree = (loadUser = false) => {
    deptTreeSpinLoading.value = true
    getEnableDeptListApi().then(res => {
        //权限树数据赋值
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
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //角色ID
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
</style>
