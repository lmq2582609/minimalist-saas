<template>
    <div>
        <a-card title="用户管理" :body-style="{height: 'calc(100vh - 155px)'}">
            <div class="w-full h-full flex justify-between">
                <!-- 部门数据 -->
                <a-row class="w-[250px] h-full">
                    <a-spin class="w-[100%] h-full" :size="35" :loading="loadDeptListLoading" tip="正在处理, 请稍候...">
                        <a-scrollbar class="w-[100%] h-full overflow-auto" :outer-style="{width: '100%'}" type="track">
                            <a-tree :data="deptTree" v-if="deptTree.length > 0" class="h-full"
                                show-line  blockNode
                                :fieldNames="{
                                    key: 'deptId',
                                    title: 'deptName',
                                    children: 'children'
                                }" />
                        </a-scrollbar>
                    </a-spin>
                </a-row>

                <!-- 用户数据 -->
                <a-row class="flex-1" style="overflow-x: auto;overflow-y: hidden;">
                    <!-- 查询条件 -->
                    <a-row v-if="showSearchRow">
                        <a-form :model="searchForm" layout="inline" label-align="left" size="small">
                            <a-form-item field="userRealName" label="用户姓名">
                                <a-input v-model="searchForm.userRealName" placeholder="用户姓名" />
                            </a-form-item>
                            <a-form-item field="phone" label="用户手机">
                                <a-input v-model="searchForm.phone" placeholder="用户手机" />
                            </a-form-item>
                            <a-form-item field="status" label="用户状态">
                                <a-select v-model="searchForm.status" placeholder="用户状态" allow-clear allow-search>
                                    <a-option v-for="(d, index) in dicts[proxy.DICT.userStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                                </a-select>
                            </a-form-item>
                            <a-form-item>
                                <a-space>
                                    <a-button type="primary" @click="getPageList(false)">
                                        <template #icon><icon-search /></template>
                                        <template #default>查询</template>
                                    </a-button>
                                    <a-button @click="getPageList(true)">
                                        <template #icon><icon-sync /></template>
                                        <template #default>重置</template>
                                    </a-button>
                                </a-space>
                            </a-form-item>
                        </a-form>
                    </a-row>

                    <!-- 分割线 -->
                    <a-divider v-if="showSearchRow" class="mt-2" />

                    <!-- 数据操作区 -->
                    <a-row class="w-full flex justify-between">
                        <a-space>
                            <!-- 添加 -->
                            <a-button type="primary" size="small" @click="addBtnClick()">
                                <template #icon><icon-plus /></template>
                                <template #default>添加</template>
                            </a-button>
                        </a-space>
                        <a-space>
                            <!-- 刷新 -->
                            <a-button shape="circle" size="small" @click="getPageList(false)">
                                <template #icon><icon-refresh /></template>
                            </a-button>
                            <!-- 收缩/展开 -->
                            <a-button shape="circle" size="small" @click="showSearchRow = !showSearchRow">
                                <template #icon>
                                    <icon-caret-up v-if="showSearchRow" />
                                    <icon-caret-down v-else />
                                </template>
                            </a-button>
                        </a-space>
                    </a-row>

                    <!-- 数据展示区 -->
                    <a-row class="mt-3 w-full">
                        <a-table class="w-[100%]" :scroll="{ minWidth: 600 }" :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" :pagination="false" table-layout-fixed>
                            <!-- 用户名称 -->
                            <template #username="{ record }">
                                <a-link @click="detailBtnClick(record.userId)" icon>{{ record.username }}</a-link>
                            </template>
                            <!-- 用户头像 -->
                            <template #userAvatar="{ record }">
                                <a-avatar :size="28">
                                    <img alt="头像" :src="record.userAvatar" v-if="record.userAvatar" />
                                    <img alt="头像" src="../../../assets/default-avatar.jpg" v-else />
                                </a-avatar>
                            </template>
                            <!-- 用户性别 -->
                            <template #userSex="{ record }">
                                <dict-convert :dict-data="dicts[proxy.DICT.userSex]" :dict-key="record.userSex" />
                            </template>
                            <!-- 用户状态 -->
                            <template #status="{ record }">
                                <dict-convert :dict-data="dicts[proxy.DICT.userStatus]" :dict-key="record.status" />
                            </template>
                            <!-- 操作 -->
                            <template #operation="{ record }">
                                <a-button type="text" size="mini" @click="updateBtnClick(record.userId)">
                                    <template #icon>
                                        <icon-edit />
                                    </template>
                                    <template #default>修改</template>
                                </a-button>
                                <a-popconfirm content="确认要删除吗?" @ok="deleteBtnOkClick(record.userId)">
                                    <a-button type="text" status="danger" size="mini" v-if="record.allowDelete">
                                        <template #icon>
                                            <icon-delete />
                                        </template>
                                        <template #default>删除</template>
                                    </a-button>
                                </a-popconfirm>
                            </template>
                        </a-table>
                        <pagination class="mt-5" v-if="datatable.total > 0"
                                    v-model:page-num="searchForm.pageNum"
                                    v-model:page-size="searchForm.pageSize"
                                    :total="datatable.total" @pagination="getPageList(false)" />
                    </a-row>
                </a-row>
            </div>
        </a-card>

        <!-- 添加/修改 -->
        <a-modal v-model:visible="modal.visible" width="50%" :esc-to-close="false" :mask-closable="false" draggable :footer="false">
            <template #title>{{ modal.title }}</template>
            <component :is="modal.component" :params="modal.params" @ok="onOk" @cancel="onCancel" v-if="modal.visible" />
        </a-modal>
    </div>
</template>

<script setup>
import {ref, reactive, getCurrentInstance, shallowRef} from 'vue'
import { getPageUserListApi, deleteUserByUserIdApi } from "~/api/user.js";
import UserEdit from "~/pages/basic/user/UserEdit.vue";
import UserDetail from "~/pages/basic/user/UserDetail.vue";
import {getDeptListApi} from "~/api/dept.js";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.userStatus, proxy.DICT.deptList, proxy.DICT.userSex])
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const searchForm = reactive({
    //用户真实姓名
    userRealName: null,
    //用户手机
    phone: null,
    //用户状态
    status: null,
    //页码
    pageNum: 1,
    //条数
    pageSize: 10
})
//数据列表
const datatable = reactive({
    //列配置
    columns: [
        {title: '用户账号', dataIndex: 'username', slotName: 'username', align: 'center', width: 150, ellipsis: true, tooltip: true},
        {title: '用户昵称', dataIndex: 'nickname', align: 'center', width: 150, ellipsis: true, tooltip: true},
        {title: '用户头像', dataIndex: 'userAvatar', slotName: 'userAvatar', align: 'center', width: 100},
        {title: '用户姓名', dataIndex: 'userRealName', align: 'center', width: 100},
        {title: '用户性别', dataIndex: 'userSex', slotName: 'userSex', align: 'center', width: 90},
        {title: '手机号', dataIndex: 'phone', align: 'center', width: 125},
        {title: '邮箱', dataIndex: 'email', align: 'center', width: 200, ellipsis: true, tooltip: true},
        {title: '用户状态', dataIndex: 'status', slotName: 'status', align: 'center', width: 90},
        {title: '操作', slotName: 'operation', align: 'center', width: 160, fixed: 'right'}
    ],
    //加载
    loading: false,
    //数据列表
    records: [],
    //总条数
    total: 0
})
//查询数据列表
const getPageList = (isReset = false) => {
    if (isReset) {
        searchForm.userRealName = null
        searchForm.phone = null
        searchForm.status = null
        searchForm.pageNum = 1
        searchForm.pageSize = 10
    }
    datatable.loading = true
    getPageUserListApi(searchForm).then(res => {
        datatable.records = res.records
        datatable.total = res.total
    }).finally(() => {
        datatable.loading = false
    })
}
//公共模态框
const modal = reactive({
    //是否显示
    visible: false,
    //模态框标题
    title: '用户管理',
    //传递参数
    params: {},
    //组件名称
    component: null
});
//添加按钮 -> 点击事件
const addBtnClick = () => {
    modal.visible = true
    modal.title = '添加用户'
    modal.params = { operationType: proxy.operationType.add.type }
    modal.component = shallowRef(UserEdit)
}
//表格行数据 "修改" -> 点击
const updateBtnClick = (userId) => {
    modal.visible = true
    modal.title = '修改用户'
    modal.params = { operationType: proxy.operationType.update.type, userId: userId }
    modal.component = shallowRef(UserEdit)
}
//表格行数据 "查看" -> 点击
const detailBtnClick = (userId) => {
    modal.visible = true
    modal.title = '用户详细信息'
    modal.params = { operationType: proxy.operationType.detail.type, userId: userId }
    modal.component = shallowRef(UserDetail)
}
//表格行数据 "删除" -> 确认
const deleteBtnOkClick = (userId) => {
    deleteUserByUserIdApi(userId).then(() => {
        proxy.$msg.success(proxy.operationType.delete.success)
        //刷新列表
        getPageList()
    })
}
//模态框 -> 确认
const onOk = () => {
    modal.visible = false
    //查询数据列表
    getPageList()
}
//模态框 -> 取消
const onCancel = () => {
    modal.visible = false
}

//查询部门数据列表
const deptTree = ref([])
const loadDeptListLoading = ref(false)
const getDeptList = () => {
    loadDeptListLoading.value = true
    getDeptListApi({status: 1}).then(res => {
        deptTree.value = res
    }).finally(() => {
        loadDeptListLoading.value = false
    })
}





//查询数据列表
getPageList()
//查询部门数据列表
getDeptList()
</script>
<style scoped></style>
