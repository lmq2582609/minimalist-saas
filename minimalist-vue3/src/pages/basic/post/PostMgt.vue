<template>
    <a-card :body-style="{height: 'calc(100vh - 125px)'}">

        <!-- 数据列表 -->
        <a-row class="w-full h-full flex flex-col overflow-x-auto overflow-y-hidden">
            <!-- 查询条件 -->
            <a-row class="w-full" v-if="showSearchRow">
                <a-form :model="searchForm" layout="inline" label-align="left" size="small">
                    <a-form-item field="postName" label="岗位名称">
                        <a-input v-model="searchForm.postName" placeholder="岗位名称" />
                    </a-form-item>
                    <a-form-item field="postCode" label="岗位编码">
                        <a-input v-model="searchForm.postCode" placeholder="岗位编码" />
                    </a-form-item>
                    <a-form-item field="status" label="岗位状态">
                        <a-select v-model="searchForm.status" placeholder="岗位状态" allow-clear>
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
                    <a-button v-perm="['basic:post:add']" type="primary" size="small" @click="addBtnClick()">
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
                <a-table class="w-[100%]" :scroll="{ y: '100%' }" :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" :pagination="false" table-layout-fixed>
                    <!-- 岗位名称 -->
                    <template #postName="{ record }">
                        <a-link @click="detailBtnClick(record.postId)" icon>{{ record.postName }}</a-link>
                    </template>
                    <!-- 岗位状态 -->
                    <template #status="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="record.status" />
                    </template>
                    <!-- 操作 -->
                    <template #operation="{ record }">
                        <a-button v-perm="['basic:post:update']" type="text" size="mini" @click="updateBtnClick(record.postId)">
                            <template #icon>
                                <icon-edit />
                            </template>
                            <template #default>修改</template>
                        </a-button>
                        <a-popconfirm content="确认要删除吗?" @ok="deleteBtnOkClick(record.postId)">
                            <a-button v-perm="['basic:post:delete']" type="text" status="danger" size="mini">
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
        <a-modal v-model:visible="modal.visible" width="400px" :esc-to-close="false" :mask-closable="false" draggable :footer="false">
            <template #title>{{ modal.title }}</template>
            <component :is="modal.component" :params="modal.params" @ok="onOk" @cancel="onCancel" v-if="modal.visible" />
        </a-modal>

    </a-card>
</template>

<script setup>
import {ref, reactive, getCurrentInstance, shallowRef} from 'vue'
import { getPagePostListApi, deletePostByPostIdApi } from '~/api/post.js'
import PostEdit from "~/pages/basic/post/PostEdit.vue";
import PostDetail from "~/pages/basic/post/PostDetail.vue";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus])
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const searchForm = reactive({
    //岗位名称
    postName: null,
    //岗位编码
    postCode: null,
    //岗位状态
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
        {title: '岗位名称', dataIndex: 'postName', slotName: 'postName', align: 'center'},
        {title: '岗位编码', dataIndex: 'postCode', align: 'center'},
        {title: '排序', dataIndex: 'postSort', align: 'center'},
        {title: '岗位状态', dataIndex: 'status', slotName: 'status', align: 'center'},
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
    getPagePostListApi(searchForm).then(res => {
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
    title: '岗位管理',
    //传递参数
    params: {},
    //组件名称
    component: null
});
//添加按钮 -> 点击事件
const addBtnClick = () => {
    modal.visible = true
    modal.title = '添加岗位'
    modal.params = { operationType: proxy.operationType.add.type }
    modal.component = shallowRef(PostEdit)
}
//表格行数据 "修改" -> 点击
const updateBtnClick = (postId) => {
    modal.visible = true
    modal.title = '修改岗位'
    modal.params = { operationType: proxy.operationType.update.type, postId: postId }
    modal.component = shallowRef(PostEdit)
}
//表格行数据 "查看" -> 点击
const detailBtnClick = (postId) => {
    modal.visible = true
    modal.title = '岗位详细信息'
    modal.params = { operationType: proxy.operationType.detail.type, postId: postId }
    modal.component = shallowRef(PostDetail)
}
//表格行数据 "删除" -> 确认
const deleteBtnOkClick = (postId) => {
    deletePostByPostIdApi(postId).then(() => {
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
