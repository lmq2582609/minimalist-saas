<template>
    <a-card class="p-0" :body-style="{height: 'calc(100vh - 125px)'}">

        <!-- 数据列表 -->
        <a-row class="w-full h-full flex flex-col overflow-x-auto overflow-y-hidden">
            <!-- 查询条件 -->
            <a-row class="w-full" v-if="showSearchRow">
                <a-form :model="form" layout="inline" label-align="left" size="small">
                    <a-form-item field="permName" label="字典名称">
                        <a-input v-model="form.dictName" placeholder="字典名称" allow-clear />
                    </a-form-item>
                    <a-form-item field="permType" label="字典类型">
                        <a-input v-model="form.dictType" placeholder="字典类型" allow-clear />
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
                    <a-button v-perm="['basic:dict:add']" type="primary" size="small" @click="addBtnClick()">
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
                    <!-- 字典名称 -->
                    <template #dictName="{ record }">
                        <a-link @click="detailBtnClick(record.dictType)" icon>{{ record.dictName }}</a-link>
                    </template>
                    <!-- 操作 -->
                    <template #operation="{ record }">
                        <a-button v-perm="['basic:dict:update']" type="text" size="mini" @click="updateBtnClick(record.dictType)">
                            <template #icon>
                                <icon-edit />
                            </template>
                            <template #default>修改</template>
                        </a-button>
                        <a-popconfirm content="确认要删除吗?" @ok="deleteBtnOkClick(record.dictType)">
                            <a-button v-perm="['basic:dict:delete']" type="text" status="danger" size="mini">
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
                            v-model:page-num="form.pageNum"
                            v-model:page-size="form.pageSize"
                            :total="datatable.total" @pagination="getPageList(false)" />
            </a-row>
        </a-row>

        <!-- 添加/修改 -->
        <a-modal v-model:visible="modal.visible" width="60%" :esc-to-close="false" :mask-closable="false" draggable :footer="false">
            <template #title>{{ modal.title }}</template>
            <component :is="modal.component" :params="modal.params" @ok="onOk" @cancel="onCancel" v-if="modal.visible" />
        </a-modal>

    </a-card>
</template>

<script setup>
import { ref, reactive, shallowRef, getCurrentInstance } from 'vue'
import { getPageDictListApi, deleteDictByDictTypeApi } from '~/api/dict.js'
import DictEdit from '~/pages/basic/dict/DictEdit.vue'
import DictDetail from '~/pages/basic/dict/DictDetail.vue'

//全局实例
const {proxy} = getCurrentInstance()
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const form = reactive({
    //权限名称
    dictName: null,
    //字典类型
    dictType: null,
    //页码
    pageNum: 1,
    //条数
    pageSize: 10
})
//数据列表
const datatable = reactive({
    //列配置
    columns: [
        {title: '字典名称', dataIndex: 'dictName', slotName: 'dictName', align: 'center'},
        {title: '字典类型', dataIndex: 'dictType', align: 'center'},
        {title: '字典描述', dataIndex: 'dictDesc', align: 'center', ellipsis: true, tooltip: true},
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
    //重置查询条件
    if (isReset) {
        form.dictName = null
        form.dictType = null
        form.pageNum = 1
        form.pageSize = 10
    }
    datatable.loading = true
    getPageDictListApi(form).then(res => {
        datatable.records = res.records
        datatable.total = res.total
    }).finally(() => {
        datatable.loading = false
    })
}
//表格行数据 "删除" -> 确认
const deleteBtnOkClick = (dictType) => {
    deleteDictByDictTypeApi(dictType).then(() => {
        proxy.$msg.success(proxy.operationType.delete.success)
        //刷新列表
        getPageList()
    })
}
//公共模态框
const modal = reactive({
    //是否显示
    visible: false,
    //模态框标题
    title: '字典管理',
    //传递参数
    params: {},
    //组件名称
    component: null
})
//表格行数据 "修改" -> 点击
const updateBtnClick = (dictType) => {
    modal.visible = true
    modal.title = '修改字典'
    modal.params = { operationType: proxy.operationType.update.type, dictType: dictType }
    modal.component = shallowRef(DictEdit)
}
//添加按钮 -> 点击
const addBtnClick = () => {
    modal.visible = true
    modal.title = '添加字典'
    modal.params = { operationType: proxy.operationType.add.type }
    modal.component = shallowRef(DictEdit)
}
//表格行数据 "查看" -> 点击
const detailBtnClick = (dictType) => {
    modal.visible = true
    modal.title = '字典详细信息'
    modal.params = { operationType: proxy.operationType.detail.type, dictType: dictType }
    modal.component = shallowRef(DictDetail)
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
