<template>
    <div>
        <a-card title="权限管理">
            <!-- 查询条件 -->
            <a-row v-if="showSearchRow">
                <a-form :model="searchForm" layout="inline" label-align="left" size="small">
                    <a-form-item field="permName" label="权限名称">
                        <a-input v-model="searchForm.permName" placeholder="权限名称" />
                    </a-form-item>
                    <a-form-item field="status" label="权限状态">
                        <a-select v-model="searchForm.status" placeholder="权限状态" allow-clear>
                            <a-option v-for="(d, index) in dicts[proxy.DICT.permStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
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
                <a-table :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" row-key="permId" :pagination="false" table-layout-fixed>
                    <!-- 权限名称 -->
                    <template #permName="{ record }">
                        <a-link @click="detailBtnClick(record.permId)" icon>{{ record.permName }}</a-link>
                    </template>
                    <!-- 图标展示 -->
                    <template #permIcon="{ record }">
                        <functional-icons :icon="record.permIcon" size="30"></functional-icons>
                    </template>
                    <!-- 权限类型 -->
                    <template #permType="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.permType]" :dict-key="record.permType" />
                    </template>
                    <!-- 状态数据转换 -->
                    <template #status="{ record }">
                        <dict-convert :dict-data="dicts[proxy.DICT.permStatus]" :dict-key="record.status" />
                    </template>
                    <!-- 操作 -->
                    <template #operation="{ record }">
                        <a-button type="text" size="mini" @click="addRowBtnClick(record)" style="padding: 0 5px">
                            <template #icon>
                                <icon-plus />
                            </template>
                            <template #default>添加</template>
                        </a-button>
                        <a-button type="text" size="mini" @click="updateBtnClick(record.permId)" style="padding: 0 5px">
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
import FunctionalIcons from "~/components/iconSelect/FunctionalIcons.vue";
import {ref, reactive, getCurrentInstance, shallowRef} from 'vue'
import PermEdit from "~/pages/basic/perm/PermEdit.vue";
import PermDetail from "~/pages/basic/perm/PermDetail.vue";
import {getPermListApi, deletePermByPermIdApi} from '~/api/perm'

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.permStatus, proxy.DICT.permType])
//是否展示搜索区域
const showSearchRow = ref(true)
//搜索参数表单
const searchForm = reactive({
    //权限名称
    permName: null,
    //权限状态
    status: null
})
//数据列表
const datatable = reactive({
    //列配置
    columns: [
        {title: '权限名称', dataIndex: 'permName', slotName: 'permName', align: 'left', width: 320, headerCellClass: 'w-[100%] flex justify-center'},
        {title: '权限图标', dataIndex: 'permIcon', slotName: 'permIcon', align: 'center', width: 100},
        {title: '权限类型', dataIndex: 'permType', slotName: 'permType', align: 'center', width: 100},
        {title: '排序', dataIndex: 'permSort', align: 'center', width: 80},
        {title: '权限编码', dataIndex: 'permCode', align: 'center', ellipsis: true, tooltip: true},
        {title: '路由地址', dataIndex: 'permPath', align: 'center', ellipsis: true, tooltip: true},
        {title: '组件路径', dataIndex: 'component', align: 'center', ellipsis: true, tooltip: true},
        {title: '权限状态', dataIndex: 'status', slotName: 'status', align: 'center', width: 100},
        {title: '操作', slotName: 'operation', align: 'center', width: 200}
    ],
    //加载
    loading: false,
    //数据列表
    records: []
})
//公共模态框
const modal = reactive({
    //是否显示
    visible: false,
    //模态框标题
    title: '权限管理',
    //传递参数
    params: {},
    //组件名称
    component: null
});
//添加按钮 -> 点击事件
const addBtnClick = () => {
    modal.visible = true
    modal.title = '添加权限'
    modal.params = { operationType: proxy.operationType.add.type }
    modal.component = shallowRef(PermEdit)
}
//表格行数据 "添加" -> 点击
const addRowBtnClick = (record) => {
    modal.visible = true
    modal.title = '添加权限'
    modal.params = { operationType: proxy.operationType.add.type, parentPermId: record.permId }
    modal.component = shallowRef(PermEdit)
}
//表格行数据 "修改" -> 点击
const updateBtnClick = (permId) => {
    modal.visible = true
    modal.title = '修改权限'
    modal.params = { operationType: proxy.operationType.update.type, permId: permId }
    modal.component = shallowRef(PermEdit)
}
//表格行数据 "查看" -> 点击
const detailBtnClick = (permId) => {
    modal.visible = true
    modal.title = '权限详细信息'
    modal.params = { operationType: proxy.operationType.detail.type, permId: permId }
    modal.component = shallowRef(PermDetail)
}
//表格行数据 "删除" -> 确认
const deleteBtnOkClick = (record) => {
    if (record.children && record.children.length > 0) {
        proxy.$msg.error('该权限下包含下级权限，请先删除下级权限')
        return
    }
    deletePermByPermIdApi(record.permId).then(() => {
        proxy.$msg.success(proxy.operationType.delete.success)
        //刷新列表
        getList()
    })
}
//查询数据列表
const getList = (isReset = false) => {
    //重置查询条件
    if (isReset) {
        searchForm.permName = null
        searchForm.status = null
    }
    datatable.loading = true
    getPermListApi(searchForm).then(res => {
        //table数据赋值
        datatable.records = res
    }).finally(() => {
        datatable.loading = false
    })
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

<style scoped>

</style>