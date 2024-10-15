<template>
    <a-card :body-style="{height: 'calc(100vh - 125px)'}">
        <div class="w-full h-full flex justify-between">
            <!-- 按文件来源将文件分类 -->
            <a-row class="h-full border-r pr-3">
                <a-list class="w-[240px]" size="small" hoverable>
                    <a-list-item class="cursor-pointer" @click="fileSourceClick(null)"
                                 :class="searchForm.fileSource === null ? 'file-source-active' : ''">全部文件</a-list-item>
                    <a-list-item
                            class="cursor-pointer"
                            @click="fileSourceClick(d.dictKey)"
                            :class="searchForm.fileSource === d.dictKey ? 'file-source-active' : ''"
                            v-for="(d, index) in dicts[proxy.DICT.fileSource]"
                            :key="index">
                        {{d.dictValue}}
                    </a-list-item>
                </a-list>
            </a-row>

            <!-- 文件数据 -->
            <a-row class="w-full flex flex-1 flex-col py-3 pl-3" style="overflow-x: auto;">
                <!-- 查询条件 -->
                <a-row class="w-full" v-if="showSearchRow">
                    <a-form :model="searchForm" layout="inline" label-align="left" size="small">
                        <a-form-item field="fileName" label="文件名称">
                            <a-input v-model="searchForm.fileName" placeholder="文件名称" />
                        </a-form-item>
                        <a-form-item field="status" label="文件状态">
                            <a-select v-model="searchForm.status" placeholder="文件状态" allow-clear>
                                <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                            </a-select>
                        </a-form-item>
                    </a-form>
                    <a-row justify="center" class="w-full mt-2">
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
                    </a-row>
                </a-row>

                <!-- 分割线 -->
                <a-divider v-if="showSearchRow" class="mt-2" />

                <!-- 数据操作区 -->
                <a-row class="w-full flex justify-between">
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
                <a-row class="w-full flex-1 mt-3" style="overflow-y: auto">
                    <a-table class="w-[100%]" :scroll="{ minWidth: 600, y: '100%' }" :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" :pagination="false" table-layout-fixed>
                        <!-- 文件名称 -->
                        <template #fileName="{ record }">
                            <a-tooltip :content="record.fileName">
                                <div class="w-[190px] flex items-center truncate">
                                    <!-- 文件类型图标 -->
                                    <img v-if="record.fileType.includes('image')" style="width: 20px" src="../../../assets/file-icon/image.svg" alt="">
                                    <img v-else style="width: 20px" src="../../../assets/file-icon/unknown.svg" alt="">
                                    <!-- 文件名称 -->
                                    <span class="ml-2">{{ record.fileName }}</span>
                                </div>
                            </a-tooltip>
                        </template>
                        <!-- 文件来源 -->
                        <template #fileSource="{ record }">
                            <dict-convert :dict-data="dicts[proxy.DICT.fileSource]" :dict-key="record.fileSource" />
                        </template>
                        <!-- 存储类型 -->
                        <template #storageType="{ record }">
                            <dict-convert :dict-data="dicts[proxy.DICT.storageType]" :dict-key="record.storageType" />
                        </template>
                        <!-- 文件状态 -->
                        <template #status="{ record }">
                            <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="record.status" />
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
                </a-row>

                <!-- 分页 -->
                <a-row class="w-full flex justify-end mt-2">
                    <pagination v-if="datatable.total > 0"
                        v-model:page-num="searchForm.pageNum"
                        v-model:page-size="searchForm.pageSize"
                        :total="datatable.total" @pagination="getPageList(false)" />
                </a-row>
            </a-row>
        </div>
    </a-card>
</template>

<script setup>
import {ref, reactive, getCurrentInstance} from 'vue'
import { getPageFileListApi, deleteFileApi } from "~/api/file.js";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.fileSource, proxy.DICT.fileSource, proxy.DICT.storageType])
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const searchForm = reactive({
    //文件来源
    fileSource: null,
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
        {title: '文件大小', dataIndex: 'fileSize', align: 'center', width: 120, ellipsis: true, tooltip: true},
        {title: '文件来源', dataIndex: 'fileSource', slotName: 'fileSource', align: 'center', width: 120, ellipsis: true, tooltip: true},
        {title: '存储类型', dataIndex: 'storageType', slotName: 'storageType', align: 'center', width: 120, ellipsis: true, tooltip: true},
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

//文件来源 -> 点击
const fileSourceClick = (fileSource) => {
    //fileSource = null，查询全部
    searchForm.fileSource = fileSource
    //查询数据列表
    getPageList()
}

//查询数据列表
getPageList()
</script>
<style scoped>
/* 文件来源选中 */
.file-source-active {
    background-color: var(--color-fill-1);color: rgb(var(--arcoblue-6))
}
</style>
