<template>
    <div>
        <a-card title="文件管理">
            <!-- 查询条件 -->
            <a-row v-if="showSearchRow">
                <a-form :model="searchForm" layout="inline" label-align="left" size="small">
                    <a-form-item field="fileName" label="文件名称">
                        <a-input v-model="searchForm.fileName" placeholder="文件名称" />
                    </a-form-item>
                    <a-form-item field="status" label="文件状态">
                        <a-select v-model="searchForm.status" placeholder="文件状态" allow-clear>
                            <a-option v-for="(d, index) in dicts[proxy.DICT.fileStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
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
                <a-space></a-space>
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
                <a-table class="w-[100%]" :scroll="{ minWidth: 600 }" :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" :pagination="false" table-layout-fixed>
                    <!-- 文件来源 -->
                    <template #fileSource="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.fileSource]" :dict-key="record.fileSource" />
                    </template>
                    <!-- 文件来源 -->
                    <template #filePlatform="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.filePlatform]" :dict-key="record.filePlatform" />
                    </template>
                    <!-- 文件状态 -->
                    <template #status="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.fileStatus]" :dict-key="record.status" />
                    </template>
                    <!-- 操作 -->
                    <template #operation="{ record }">
                        <a-button type="text" size="mini" @click="downloadBtnClick(record)">
                            <template #icon>
                                <icon-download />
                            </template>
                            <template #default>下载</template>
                        </a-button>
                        <a-popconfirm content="确认要删除吗?" @ok="deleteBtnOkClick(record.fileId)">
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
    </div>
</template>

<script setup>
import {ref, reactive, getCurrentInstance} from 'vue'
import { getPageFileListApi, deleteFileApi } from "~/api/file.js";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.fileStatus, proxy.DICT.fileSource, proxy.DICT.filePlatform])
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const searchForm = reactive({
    //文件名称
    fileName: null,
    //文件状态
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
        {title: '文件名称', dataIndex: 'fileName', align: 'center', width: 250, ellipsis: true, tooltip: true},
        {title: '文件类型', dataIndex: 'fileType', align: 'center', width: 150, ellipsis: true, tooltip: true},
        {title: '文件大小', dataIndex: 'fileSize', align: 'center', width: 150, ellipsis: true, tooltip: true},
        {title: '文件来源', dataIndex: 'fileSource', slotName: 'fileSource', align: 'center', width: 150, ellipsis: true, tooltip: true},
        {title: '文件存储平台', dataIndex: 'filePlatform', slotName: 'filePlatform', align: 'center', width: 150, ellipsis: true, tooltip: true},
        {title: '文件状态', dataIndex: 'status', slotName: 'status', align: 'center', width: 100, ellipsis: true, tooltip: true},
        {title: '备注', dataIndex: 'remark', align: 'center', width: 200, ellipsis: true, tooltip: true},
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
        searchForm.fileName = null
        searchForm.status = null
        searchForm.pageNum = 1
        searchForm.pageSize = 10
    }
    datatable.loading = true
    getPageFileListApi(searchForm).then(res => {
        datatable.records = res.records
        datatable.total = res.total
    }).finally(() => {
        datatable.loading = false
    })
}
//表格行数据 "下载" -> 点击
const downloadBtnClick = (record) => {
    window.open(record.fileUrl, '_blank')
}
//表格行数据 "删除" -> 确认
const deleteBtnOkClick = (fileId) => {
    deleteFileApi(fileId).then(() => {
        proxy.$msg.success(proxy.operationType.delete.success)
        //刷新列表
        getPageList()
    })
}

//查询数据列表
getPageList()
</script>
<style scoped></style>