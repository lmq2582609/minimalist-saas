<template>
    <a-card class="p-0" :body-style="{height: 'calc(100vh - 125px)'}">

        <!-- 数据列表 -->
        <a-row class="w-full h-full flex flex-col overflow-x-auto overflow-y-hidden">
            <!-- 查询条件 -->
            <a-row class="w-full" v-if="showSearchRow">
                <a-form :model="searchForm" layout="inline" label-align="left" size="small">
                    <a-form-item field="noticeTitle" label="公告标题">
                        <a-input v-model="searchForm.noticeTitle" placeholder="公告标题" />
                    </a-form-item>
                    <a-form-item field="noticeType" label="公告类型">
                        <a-select v-model="searchForm.noticeType" placeholder="公告类型" allow-clear>
                            <a-option v-for="(d, index) in dicts[proxy.DICT.noticeType]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                        </a-select>
                    </a-form-item>
                    <a-form-item field="status" label="公告状态">
                        <a-select v-model="searchForm.status" placeholder="公告状态" allow-clear>
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
            <a-row class="w-full flex-1 mt-3 overflow-y-auto">
                <a-table class="w-[100%]" :scroll="{ minWidth: 600, y: '100%' }" :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" :pagination="false" table-layout-fixed>
                    <!-- 公告标题 -->
                    <template #noticeTitle="{ record }">
                        <a-link @click="detailBtnClick(record.noticeId)" icon>{{ record.noticeTitle }}</a-link>
                    </template>
                    <!-- 公告类型 -->
                    <template #noticeType="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.noticeType]" :dict-key="record.noticeType" />
                    </template>
                    <!-- 发布人 -->
                    <template #publishAuthorId="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.userList]" :dict-key="record.publishAuthorId" />
                    </template>
                    <!-- 发布部门 -->
                    <template #publishDeptId="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.deptList]" :dict-key="record.publishDeptId" />
                    </template>
                    <!-- 封面图 -->
                    <template #noticePicFile="{ record }">
                        <template v-if="record.noticePicFile && record.noticePicFile.length > 0">
                            <a-carousel :style="{width: '100%', height: '100px'}" :indicator-type="'never'">
                                <a-carousel-item v-for="picFile in record.noticePicFile">
                                    <a-image class="cursor-pointer" style="transform: translateY(-20%)" :src="picFile.fileThUrl" />
                                </a-carousel-item>
                            </a-carousel>
                        </template>
                    </template>
                    <!-- 是否置顶 -->
                    <template #noticeTop="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.yesNo]" :dict-key="record.noticeTop" />
                    </template>
                    <!-- 是否外链 -->
                    <template #noticeOutChain="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.yesNo]" :dict-key="record.noticeOutChain" />
                    </template>
                    <!-- 公告状态 -->
                    <template #status="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="record.status" />
                    </template>
                    <!-- 创建人 -->
                    <template #createId="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.userList]" :dict-key="record.createId" />
                    </template>
                    <!-- 操作 -->
                    <template #operation="{ record }">
                        <a-button type="text" size="mini" @click="updateBtnClick(record.noticeId)">
                            <template #icon>
                                <icon-edit />
                            </template>
                            <template #default>修改</template>
                        </a-button>
                        <a-popconfirm content="确认要删除吗?" @ok="deleteBtnOkClick(record.noticeId)">
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

        <!-- 添加/修改 -->
        <a-modal v-model:visible="modal.visible" fullscreen :esc-to-close="false" :mask-closable="false" draggable :footer="false">
            <template #title>{{ modal.title }}</template>
            <component :is="modal.component" :params="modal.params" @ok="onOk" @cancel="onCancel" v-if="modal.visible" />
        </a-modal>

    </a-card>
</template>

<script setup>
import {ref, reactive, getCurrentInstance, shallowRef} from 'vue'
import { getPageNoticeListApi, deleteNoticeByNoticeIdApi } from "~/api/notice.js";
import NoticeEdit from "~/pages/basic/notice/NoticeEdit.vue";
import NoticeDetail from "~/pages/basic/notice/NoticeDetail.vue";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.noticeType, proxy.DICT.userList, proxy.DICT.deptList, proxy.DICT.yesNo])
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const searchForm = reactive({
    //公告标题
    noticeTitle: null,
    //公告类型
    noticeType: null,
    //公告状态
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
        {title: '公告标题', dataIndex: 'noticeTitle', slotName: 'noticeTitle', align: 'center', width: 220, ellipsis: true, tooltip: true},
        {title: '公告类型', dataIndex: 'noticeType', slotName: 'noticeType', align: 'center', width: 100},
        {title: '发布人', dataIndex: 'publishAuthorId', slotName: 'publishAuthorId', align: 'center', width: 100},
        {title: '发布部门', dataIndex: 'publishDeptId', slotName: 'publishDeptId', align: 'center', width: 150, ellipsis: true, tooltip: true},
        {title: '封面图', dataIndex: 'noticePicFile', slotName: 'noticePicFile', align: 'center', width: 200},
        {title: '是否置顶', dataIndex: 'noticeTop', slotName: 'noticeTop', align: 'center', width: 90},
        {title: '排序', dataIndex: 'noticeSort', align: 'center', width: 90},
        {title: '是否外链', dataIndex: 'noticeOutChain', slotName: 'noticeOutChain', align: 'center', width: 90},
        {title: '公告状态', dataIndex: 'status', slotName: 'status', align: 'center', width: 90},
        {title: '创建人', dataIndex: 'createId', slotName: 'createId', align: 'center', width: 100},
        {title: '创建时间', dataIndex: 'createTime', align: 'center', width: 170},
        {title: '发布时间', dataIndex: 'publishTime', align: 'center', width: 170},
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
        searchForm.noticeTitle = null
        searchForm.noticeType = null
        searchForm.status = null
        searchForm.pageNum = 1
        searchForm.pageSize = 10
    }
    datatable.loading = true
    getPageNoticeListApi(searchForm).then(res => {
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
    title: '系统公告',
    //传递参数
    params: {},
    //组件名称
    component: null
});
//添加按钮 -> 点击事件
const addBtnClick = () => {
    modal.visible = true
    modal.title = '添加系统公告'
    modal.params = { operationType: proxy.operationType.add.type }
    modal.component = shallowRef(NoticeEdit)
}
//表格行数据 "修改" -> 点击
const updateBtnClick = (noticeId) => {
    modal.visible = true
    modal.title = '修改系统公告'
    modal.params = { operationType: proxy.operationType.update.type, noticeId: noticeId }
    modal.component = shallowRef(NoticeEdit)
}
//表格行数据 "查看" -> 点击
const detailBtnClick = (noticeId) => {
    modal.visible = true
    modal.title = '系统公告详细信息'
    modal.params = { operationType: proxy.operationType.detail.type, noticeId: noticeId }
    modal.component = shallowRef(NoticeDetail)
}
//表格行数据 "删除" -> 确认
const deleteBtnOkClick = (noticeId) => {
    deleteNoticeByNoticeIdApi(noticeId).then(() => {
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
