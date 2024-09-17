<template>
    <div>
        <a-card class="w-[100%] text-2xl py-1">
            您好，{{sysStore.user?.username}}，祝您生活愉快!
        </a-card>
        <div class="w-[100%] flex justify-between mt-3">
            <a-card title="更新日志" class="w-[50%] mr-2">
                <a-collapse :bordered="false">
                    <a-collapse-item header="V 1.0.0" key="1.0.0">
                        <template #extra>
                            2023-07-26
                        </template>
                        <div>架构上的重大改变或者其他重大改变，修改第一位版本号</div>
                        <div>版本迭代，修改第二个版本号</div>
                        <div>bug修复、小修改，修改第三个版本号</div>
                    </a-collapse-item>
                </a-collapse>
            </a-card>
            <a-card title="系统公告" class="w-[50%] ml-2">
                <a-table class="w-[100%]" :show-header="false" :columns="datatable.columns" :data="datatable.records" :loading="datatable.loading" :bordered="false" :pagination="false" table-layout-fixed>
                    <!-- 公告标题 -->
                    <template #noticeTitle="{ record }">
                        <a-link @click="detailBtnClick(record.noticeId)" icon>{{ record.noticeTitle }}</a-link>
                    </template>
                </a-table>
                <pagination class="mt-5" v-if="datatable.total > 0"
                            v-model:page-num="searchForm.pageNum"
                            v-model:page-size="searchForm.pageSize"
                            :total="datatable.total" @pagination="getPageNoticeList()" />
            </a-card>

            <!-- 公告详情 -->
            <a-modal v-model:visible="noticeModal.visible" width="1080px" simple draggable :footer="false">
                <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
                    <div class="flex justify-center text-2xl">{{noticeModal.noticeData.noticeTitle}}</div>
                    <div class="flex justify-center mt-5">
                        发布时间：{{ noticeModal.noticeData.publishTime }}
                    </div>
                    <div class="flex mt-5">
                        <div v-html="noticeModal.noticeData.noticeContent"></div>
                    </div>
                </a-spin>
            </a-modal>
        </div>
    </div>
</template>
<script setup>
import {getNoticeByNoticeIdApi, getPageHomeNoticeListApi} from "~/api/notice.js";
import {reactive, ref} from "vue";
import {useSysStore} from "~/store/module/sys-store.js";

//缓存
const sysStore = useSysStore()
//搜索参数表单
const searchForm = reactive({
    //页码
    pageNum: 1,
    //条数
    pageSize: 10
})
//数据列表
const datatable = reactive({
    //列配置
    columns: [
        {title: '公告标题', dataIndex: 'noticeTitle', slotName: 'noticeTitle', align: 'left', ellipsis: true, tooltip: true},
        {title: '发布时间', dataIndex: 'publishTime', align: 'center', width: 170}
    ],
    //加载
    loading: false,
    //数据列表
    records: [],
    //总条数
    total: 0
})
//查询数据列表
const getPageNoticeList = () => {
    datatable.loading = true
    getPageHomeNoticeListApi(searchForm).then(res => {
        datatable.records = res.records
        datatable.total = res.total
    }).finally(() => {
        datatable.loading = false
    })
}
//公共模态框
const noticeModal = reactive({
    //显示/隐藏
    visible: false,
    //公告数据
    noticeData: {}
});
//加载中...
const spinLoading = ref(false)
//加载公告详情
const detailBtnClick = (noticeId) => {
    //加载公告数据
    spinLoading.value = true
    getNoticeByNoticeIdApi(noticeId).then(res => {
        //数据赋值
        if (res) {
            //如果是外链，打开新页面跳转
            if (res.noticeOutChain && res.noticeLink) {
                window.open(res.noticeLink, '_blank')
            } else {
                //显示模态框
                noticeModal.visible = true
                //数据赋值
                noticeModal.noticeData = res
            }
        }
    }).finally(() => {
        spinLoading.value = false
    })
}
//查询数据列表
getPageNoticeList()
</script>
