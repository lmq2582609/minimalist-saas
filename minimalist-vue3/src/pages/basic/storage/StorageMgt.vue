<template>
    <a-card :body-style="{height: 'calc(100vh - 125px)'}">

        <!-- 数据列表 -->
        <a-row class="w-full h-full flex flex-col overflow-x-auto overflow-y-hidden">
            <!-- 查询条件 -->
            <a-row class="w-full" v-if="showSearchRow">
                <a-form :model="searchForm" layout="inline" label-align="left" size="small">
                    <a-form-item field="storageName" label="存储名称">
                        <a-input v-model="searchForm.storageName" placeholder="存储名称" />
                    </a-form-item>
                    <a-form-item field="storageType" label="存储类型">
                        <a-select v-model="searchForm.storageType" placeholder="存储类型" allow-clear>
                            <a-option v-for="(d, index) in dicts[proxy.DICT.storageType]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                        </a-select>
                    </a-form-item>
                    <a-form-item field="status" label="存储状态">
                        <a-select v-model="searchForm.status" placeholder="岗位状态" allow-clear>
                            <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                        </a-select>
                    </a-form-item>
                </a-form>
                <a-row justify="center" class="w-full mt-2">
                    <a-space>
                        <a-button @click="getPageList(true)">
                            <template #icon><icon-sync /></template>
                            <template #default>重置</template>
                        </a-button>
                        <a-button type="primary" @click="getPageList(false)">
                            <template #icon><icon-search /></template>
                            <template #default>查询</template>
                        </a-button>
                    </a-space>
                </a-row>
            </a-row>

            <!-- 分割线 -->
            <a-divider v-if="showSearchRow" class="mt-2" />

            <!-- 数据操作区 -->
            <a-row class="w-full flex justify-between">
                <a-space>
                    <!-- 添加 -->
                    <a-button v-perm="['basic:storage:add']" type="primary" size="small" @click="addBtnClick()">
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
            <a-row class="w-full flex-1 mt-3 overflow-y-auto">
                <a-table class="w-[100%]" :scroll="{ minWidth: 600, y: '100%' }" :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" :pagination="false" table-layout-fixed>
                    <!-- 存储名称 -->
                    <template #storageName="{ record }">
                        <a-link @click="detailBtnClick(record.storageId)" icon>{{ record.storageName }}</a-link>
                    </template>
                    <!-- 是否默认 -->
                    <template #storageDefault="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.yesNo]" :dict-key="record.storageDefault" />
                    </template>
                    <!-- 存储类型 -->
                    <template #storageType="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.storageType]" :dict-key="record.storageType" />
                    </template>
                    <!-- 存储状态 -->
                    <template #status="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="record.status" />
                    </template>
                    <!-- 操作 -->
                    <template #operation="{ record }">
                        <a-button v-perm="['basic:storage:update']" type="text" size="mini" @click="updateBtnClick(record.storageId)">
                            <template #icon>
                                <icon-edit />
                            </template>
                            <template #default>修改</template>
                        </a-button>
                        <a-popconfirm content="确认要删除吗?" @ok="deleteBtnOkClick(record.storageId)">
                            <a-button v-perm="['basic:storage:delete']" type="text" status="danger" size="mini">
                                <template #icon>
                                    <icon-delete />
                                </template>
                                <template #default>删除</template>
                            </a-button>
                        </a-popconfirm>
                    </template>
                </a-table>
            </a-row>

            <!-- 分页 -->
            <a-row class="w-full flex justify-end mt-2">
                <pagination v-if="datatable.total > 0"
                            v-model:page-num="searchForm.pageNum"
                            v-model:page-size="searchForm.pageSize"
                            :total="datatable.total" @pagination="getPageList(false)" />
            </a-row>
        </a-row>

        <!-- 添加/修改 -->
        <a-modal v-model:visible="modal.visible" width="700px" :esc-to-close="false" :mask-closable="false" draggable :footer="false">
            <template #title>{{ modal.title }}</template>
            <component :is="modal.component" :params="modal.params" @ok="onOk" @cancel="onCancel" v-if="modal.visible" />
        </a-modal>

    </a-card>
</template>

<script setup>
import {ref, reactive, getCurrentInstance, shallowRef} from 'vue'
import StorageEdit from "~/pages/basic/storage/StorageEdit.vue";
import StorageDetail from "~/pages/basic/storage/StorageDetail.vue";
import {deleteStorageByStorageIdApi, getPageStorageListApi} from "~/api/storage.js";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.storageType, proxy.DICT.yesNo])
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const searchForm = reactive({
    //存储名称
    storageName: null,
    //存储类型
    storageType: null,
    //存储状态
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
        {title: '存储名称', dataIndex: 'storageName', slotName: 'storageName', align: 'center'},
        {title: '存储类型', dataIndex: 'storageType', slotName: 'storageType', align: 'center'},
        {title: '是否默认使用', dataIndex: 'storageDefault', slotName: 'storageDefault', align: 'center'},
        {title: '说明', dataIndex: 'description', width: 200, align: 'center', ellipsis: true, tooltip: true},
        {title: '状态', dataIndex: 'status', slotName: 'status', align: 'center'},
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
        searchForm.postName = null
        searchForm.postCode = null
        searchForm.status = null
        searchForm.pageNum = 1
        searchForm.pageSize = 10
    }
    datatable.loading = true
    getPageStorageListApi(searchForm).then(res => {
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
    title: '存储管理',
    //传递参数
    params: {},
    //组件名称
    component: null
});
//添加按钮 -> 点击事件
const addBtnClick = () => {
    modal.visible = true
    modal.title = '添加存储'
    modal.params = { operationType: proxy.operationType.add.type }
    modal.component = shallowRef(StorageEdit)
}
//表格行数据 "修改" -> 点击
const updateBtnClick = (storageId) => {
    modal.visible = true
    modal.title = '修改存储'
    modal.params = { operationType: proxy.operationType.update.type, storageId: storageId }
    modal.component = shallowRef(StorageEdit)
}
//表格行数据 "查看" -> 点击
const detailBtnClick = (storageId) => {
    modal.visible = true
    modal.title = '存储详细信息'
    modal.params = { operationType: proxy.operationType.detail.type, storageId: storageId }
    modal.component = shallowRef(StorageDetail)
}
//表格行数据 "删除" -> 确认
const deleteBtnOkClick = (postId) => {
    deleteStorageByStorageIdApi(postId).then(() => {
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
