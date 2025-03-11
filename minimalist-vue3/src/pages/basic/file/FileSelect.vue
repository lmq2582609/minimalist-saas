<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
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
            <a-row class="w-full flex flex-1 flex-col pl-3 overflow-x-auto overflow-y-hidden">

                <!-- 文件列表 -->
                <a-list :gridProps="{ gutter: 0, span: 6 }" :bordered="false">
                    <a-list-item v-for="item in datatable.records">
                        <div class="h-[150px]">
                            iOS
                        </div>
                    </a-list-item>
                </a-list>

                <!-- 分页 -->
                <a-row class="w-full flex justify-end mt-2">
                    <pagination v-if="datatable.total > 0"
                                v-model:page-num="searchForm.pageNum"
                                v-model:page-size="searchForm.pageSize"
                                :total="datatable.total" @pagination="getPageList()" />
                </a-row>
            </a-row>

        </div>
    </a-spin>
</template>
<script setup>
import {getCurrentInstance, reactive, ref, watch} from "vue";
import {getPageFileListApi} from "~/api/file.js";
import {status} from "~/utils/sys.js";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.fileSource])
//接收父组件参数
const props = defineProps({
    params: {
        type: Object,
        default: () => {}
    }
})
//加载中...
const spinLoading = ref(false)
//搜索参数表单
const searchForm = reactive({
    //文件来源
    fileSource: null,
    //文件类型
    fileType: null,
    //文件名称
    fileName: null,
    //文件状态 = 正常
    status: status.status_1.key,
    //页码
    pageNum: 1,
    //条数
    pageSize: 20
})
//数据列表
const datatable = reactive({
    //数据列表
    records: [],
    //总条数
    total: 0
})
//文件来源 -> 点击
const fileSourceClick = (fileSource) => {
    //fileSource = null，查询全部
    searchForm.fileSource = fileSource
    //查询数据列表
    getPageList()
}
//查询数据列表
const getPageList = () => {
    spinLoading.value = true
    getPageFileListApi(searchForm).then(res => {
        datatable.records = res.records
        datatable.total = res.total
    }).finally(() => {
        spinLoading.value = false
    })
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //文件类型
    if (props.params.fileType) {
        searchForm.fileType = props.params.fileType
    }
    //加载文件列表
    getPageList()
}, { deep: true, immediate: true })
</script>
<style scoped>
</style>
