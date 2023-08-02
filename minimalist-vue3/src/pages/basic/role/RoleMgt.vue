<template>
    <div>
        <a-card title="角色管理">
            <!-- 查询条件 -->
            <a-row v-if="showSearchRow">
                <a-form :model="searchForm" layout="inline" label-align="left" size="small">
                    <a-form-item field="roleName" label="角色名称">
                        <a-input v-model="searchForm.roleName" placeholder="角色名称" />
                    </a-form-item>
                    <a-form-item field="roleCode" label="角色编码">
                        <a-input v-model="searchForm.roleCode" placeholder="角色编码" />
                    </a-form-item>
                    <a-form-item field="status" label="角色状态">
                        <a-select v-model="searchForm.status" placeholder="角色状态" allow-clear>
                            <a-option v-for="(d, index) in dicts[proxy.DICT.roleStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
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
            <a-row class="flex justify-between">
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
            <a-row class="mt-3">
                <a-table :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" :pagination="false" table-layout-fixed>
                    <!-- 角色名称 -->
                    <template #roleName="{ record }">
                        <a-link @click="detailBtnClick(record.roleId)" icon>{{ record.roleName }}</a-link>
                    </template>
                    <!-- 角色状态 -->
                    <template #status="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.roleStatus]" :dict-key="record.status" />
                    </template>
                    <!-- 操作 -->
                    <template #operation="{ record }">
                        <a-button type="text" size="mini" @click="updateBtnClick(record.roleId)">
                            <template #icon>
                                <icon-edit />
                            </template>
                            <template #default>修改</template>
                        </a-button>
                        <a-popconfirm content="确认要删除吗?" @ok="deleteBtnOkClick(record.roleId)">
                            <a-button type="text" status="danger" size="mini">
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
        </a-card>

        <!-- 添加/修改 -->
        <a-modal v-model:visible="modal.visible" width="50%" :esc-to-close="false" :mask-closable="false" draggable :footer="false">
            <template #title>{{ modal.title }}</template>
            <component :is="modal.component" :params="modal.params" @ok="onOk" @cancel="onCancel"/>
        </a-modal>
    </div>
</template>

<script setup>
import {ref, reactive, getCurrentInstance, shallowRef} from 'vue'
import { getPageRoleListApi, deleteRoleByRoleIdApi } from '~/api/role.js'
import RoleEdit from "~/pages/basic/role/RoleEdit.vue";
import RoleDetail from "~/pages/basic/role/RoleDetail.vue";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.roleStatus])
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const searchForm = reactive({
    //角色名称
    roleName: null,
    //角色编码
    roleCode: null,
    //角色状态
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
        {title: '角色名称', dataIndex: 'roleName', slotName: 'roleName', align: 'center'},
        {title: '角色编码', dataIndex: 'roleCode', align: 'center'},
        {title: '排序', dataIndex: 'roleSort', align: 'center'},
        {title: '角色状态', dataIndex: 'status', slotName: 'status', align: 'center'},
        {title: '操作', slotName: 'operation', align: 'center', width: 160}
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
        searchForm.roleName = null
        searchForm.roleCode = null
        searchForm.status = null
        searchForm.pageNum = 1
        searchForm.pageSize = 10
    }
    datatable.loading = true
    getPageRoleListApi(searchForm).then(res => {
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
    title: '角色管理',
    //传递参数
    params: {},
    //组件名称
    component: null
});
//添加按钮 -> 点击事件
const addBtnClick = () => {
    modal.visible = true
    modal.title = '添加角色'
    modal.params = { operationType: proxy.operationType.add.type }
    modal.component = shallowRef(RoleEdit)
}
//表格行数据 "修改" -> 点击
const updateBtnClick = (roleId) => {
    modal.visible = true
    modal.title = '修改角色'
    modal.params = { operationType: proxy.operationType.update.type, roleId: roleId }
    modal.component = shallowRef(RoleEdit)
}
//表格行数据 "查看" -> 点击
const detailBtnClick = (roleId) => {
    modal.visible = true
    modal.title = '角色详细信息'
    modal.params = { operationType: proxy.operationType.detail.type, roleId: roleId }
    modal.component = shallowRef(RoleDetail)
}
//表格行数据 "删除" -> 确认
const deleteBtnOkClick = (roleId) => {
    deleteRoleByRoleIdApi(roleId).then(() => {
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

//查询数据列表
getPageList()
</script>
<style scoped></style>