<template>
    <div>
        <a-card class="p-0" :body-style="{height: 'calc(100vh - 125px)'}">
            <!-- 查询条件 -->
            <a-row v-if="showSearchRow">
                <a-form :model="searchForm" layout="inline" label-align="left" size="small">
                    <a-form-item field="deptName" label="部门名称">
                        <a-input v-model="searchForm.deptName" placeholder="部门名称" allow-clear />
                    </a-form-item>
                    <a-form-item field="status" label="部门状态">
                        <a-select v-model="searchForm.status" placeholder="部门状态" allow-clear>
                            <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                        </a-select>
                    </a-form-item>
                    <a-form-item>
                        <a-space>
                            <a-button type="primary" @click="getList(false)">
                                <template #icon><icon-search /></template>
                                <template #default>查询</template>
                            </a-button>
                            <a-button @click="getList(true)">
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
                    <!-- 展开/折叠 -->
                    <a-button size="small" @click="treeExpand = !treeExpand">
                        <template #icon><icon-swap /></template>
                        <template #default>展开/折叠</template>
                    </a-button>
                </a-space>
                <a-space>
                    <!-- 刷新 -->
                    <a-button shape="circle" size="small" @click="getList(false)">
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
                <a-table ref="tableRef" :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" row-key="deptId" :pagination="false" table-layout-fixed>
                    <!-- 部门名称 -->
                    <template #deptName="{ record }">
                        <a-link @click="detailBtnClick(record.deptId)" icon>{{ record.deptName }}</a-link>
                    </template>
                    <!-- 部门负责人 -->
                    <template #deptLeader="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.userList]" :dict-key="record.deptLeader" />
                    </template>
                    <!-- 部门状态 -->
                    <template #status="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="record.status" />
                    </template>
                    <!-- 操作 -->
                    <template #operation="{ record }">
                        <a-button type="text" size="mini" @click="addRowBtnClick(record)" style="padding: 0 5px">
                            <template #icon>
                                <icon-plus />
                            </template>
                            <template #default>添加</template>
                        </a-button>
                        <a-button type="text" size="mini" @click="updateBtnClick(record.deptId)" style="padding: 0 5px">
                            <template #icon>
                                <icon-edit />
                            </template>
                            <template #default>修改</template>
                        </a-button>
                        <a-popconfirm content="确认要删除吗?" @ok="deleteBtnOkClick(record)">
                            <a-button type="text" status="danger" size="mini" style="padding: 0 5px">
                                <template #icon>
                                    <icon-delete />
                                </template>
                                <template #default>删除</template>
                            </a-button>
                        </a-popconfirm>
                    </template>
                </a-table>
            </a-row>
        </a-card>

        <!-- 添加/修改 -->
        <a-modal v-model:visible="modal.visible" width="50%" :esc-to-close="false" :mask-closable="false" draggable :footer="false">
            <template #title>{{ modal.title }}</template>
            <component :is="modal.component" :params="modal.params" @ok="onOk" @cancel="onCancel" v-if="modal.visible" />
        </a-modal>
    </div>
</template>

<script setup>
import {ref, reactive, getCurrentInstance, shallowRef, watch} from 'vue'
import DeptEdit from "~/pages/basic/dept/DeptEdit.vue";
import DeptDetail from "~/pages/basic/dept/DeptDetail.vue";
import {getDeptListApi, deleteDeptByDeptIdApi} from '~/api/dept'

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.userList])
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const searchForm = reactive({
    //部门名称
    deptName: null,
    //部门状态
    status: null
})
//表格
const tableRef = ref()
//数据列表
const datatable = reactive({
    //列配置
    columns: [
        {title: '部门名称', dataIndex: 'deptName', slotName: 'deptName', align: 'left', width: 250, headerCellClass: 'w-[100%] flex justify-center'},
        {title: '负责人', dataIndex: 'deptLeader', slotName: 'deptLeader', align: 'center'},
        {title: '排序', dataIndex: 'deptSort', align: 'center', width: 80},
        {title: '电话', dataIndex: 'phone', align: 'center', ellipsis: true, tooltip: true},
        {title: '邮箱', dataIndex: 'email', align: 'center', ellipsis: true, tooltip: true},
        {title: '部门状态', dataIndex: 'status', slotName: 'status', align: 'center', width: 100},
        {title: '操作', slotName: 'operation', align: 'center', width: 200}
    ],
    //加载
    loading: false,
    //数据列表
    records: []
})
//查询数据列表
const getList = (isReset = false) => {
    //重置查询条件
    if (isReset) {
        searchForm.deptName = null
        searchForm.status = null
    }
    datatable.loading = true
    getDeptListApi(searchForm).then(res => {
        //table数据赋值
        datatable.records = res
    }).finally(() => {
        datatable.loading = false
    })
}
//公共模态框
const modal = reactive({
    //是否显示
    visible: false,
    //模态框标题
    title: '部门管理',
    //传递参数
    params: {},
    //组件名称
    component: null
});
//添加按钮 -> 点击事件
const addBtnClick = () => {
    modal.visible = true
    modal.title = '添加部门'
    modal.params = { operationType: proxy.operationType.add.type }
    modal.component = shallowRef(DeptEdit)
}
//展开/折叠 - 默认折叠
const treeExpand = ref(false)
//监听展开/折叠
watch(() => treeExpand.value, (newVal, oldVal) => {
    if (tableRef.value) {
        tableRef.value.expandAll(treeExpand.value)
    }
}, { deep: true, immediate: true })

//表格行数据 "添加" -> 点击
const addRowBtnClick = (record) => {
    modal.visible = true
    modal.title = '添加部门'
    modal.params = { operationType: proxy.operationType.add.type, parentDeptId: record.deptId }
    modal.component = shallowRef(DeptEdit)
}
//表格行数据 "删除" -> 确认
const deleteBtnOkClick = (record) => {
    if (record.children && record.children.length > 0) {
        proxy.$msg.error('该部门下包含下级部门，请先删除下级部门')
        return
    }
    deleteDeptByDeptIdApi(record.deptId).then(() => {
        proxy.$msg.success(proxy.operationType.delete.success)
        //刷新列表
        getList()
    })
}
//表格行数据 "修改" -> 点击
const updateBtnClick = (deptId) => {
    modal.visible = true
    modal.title = '修改部门'
    modal.params = { operationType: proxy.operationType.update.type, deptId: deptId }
    modal.component = shallowRef(DeptEdit)
}
//表格行数据 "查看" -> 点击
const detailBtnClick = (deptId) => {
    modal.visible = true
    modal.title = '部门详细信息'
    modal.params = { operationType: proxy.operationType.detail.type, deptId: deptId }
    modal.component = shallowRef(DeptDetail)
}
//模态框 -> 确认
const onOk = () => {
    modal.visible = false
    //查询数据列表
    getList()
}
//模态框 -> 取消
const onCancel = () => {
    modal.visible = false
}

//查询数据列表
getList()
</script>
<style scoped></style>
