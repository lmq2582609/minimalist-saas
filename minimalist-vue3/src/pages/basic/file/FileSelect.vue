<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <div class="w-full h-full flex justify-between file-select pr-3">
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

                <a-row class="w-full justify-between mb-3">
                    <a-button type="primary" @click="">
                        <template #icon><icon-upload /></template>
                        <template #default>上传</template>
                    </a-button>
                    <a-input-search v-model="searchForm.fileName" :style="{width:'180px'}" placeholder="请输入图片名称" search-button/>
                </a-row>

                <!-- 文件列表 -->
                <div class="parent-container">
                    <div class="image-container">
                        <div class="image-item" v-for="file in datatable.records">
                            <div class="image-wrapper">
                                <img src="../../../assets/login-pic.png" alt="" @click="selectFileBtnClick(file)">
                                <!-- 选中效果 -->
                                <div class="flex items-center justify-center" v-if="checkSelect(file) >= 0"
                                     :class="checkSelect(file) >= 0 ? 'select-file-active' : ''"
                                     @click="selectFileBtnClick(file)">
                                    <icon-check class="text-7xl" style="color: var(--color-text-4)" />
                                </div>
                            </div>
                            <div class="image-title">这是一张非常长的图片名称需要被截断显示</div>
                        </div>
                    </div>
                </div>

                <!-- 分页 -->
                <a-row class="w-full flex justify-end mt-5">
                    <pagination v-if="datatable.total > 0"
                                v-model:page-num="searchForm.pageNum"
                                v-model:page-size="searchForm.pageSize"
                                :total="datatable.total" @pagination="getPageList()" />
                </a-row>
            </a-row>

        </div>

        <!-- 分割线 -->
        <a-divider />

        <div class="flex justify-end">
            <a-space>
                <a-button @click="cancelBtnClick()">取消</a-button>
                <a-button type="primary" @click="okBtnClick()">确定</a-button>
            </a-space>
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
        //datatable.records = res.records

        datatable.records = []
        for (let i = 0; i < 20; i++) {
            datatable.records.push({
                fileId: i,
            })
        }

        datatable.total = res.total
    }).finally(() => {
        spinLoading.value = false
    })
}


//选中的文件
const selectFile = ref([])
//点击选中
const selectFileBtnClick = (file) => {
    //检查可以选择几个文件 - 默认一个
    let count = props.params?.limit || 1
    //如果是选择 1 个，则将之前选择的剔除
    if (count === 1) {
        selectFile.value = [file]
    } else {
        //大于 1 个，可以多选
        //检查之前是否选中
        let selectIndex = checkSelect(file)
        //之前已选中，再次点击说明要取消掉
        if (selectIndex >= 0) {
            selectFile.value.splice(selectIndex, 1)
        } else {
            //之前未选中，检查是否达到最大个数
            if (selectFile.value.length < count) {
                //未达到最大个数，直接选择
                selectFile.value.push(file)
            } else {
                //提示最多可以选择 count 个
                proxy.$msg.error(`最多可以选择${count}个文件`)
            }
        }
    }
}
//检查是否选中
const checkSelect = (file) => {
    let selectIndex = -1
    for (let i = 0;i < selectFile.value.length;i++) {
        //之前已选中
        if (selectFile.value[i].fileId === file.fileId) {
            selectIndex = i
            break
        }
    }
    return selectIndex
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
/* 父容器设置（示例） */
.parent-container {
    width: 100%;
    min-height: 200px; /* 父容器需要明确高度 */
    max-height: 700px;
}
/* 图片容器 */
.image-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 15px;
    height: 100%; /* 继承父容器高度 */
    overflow-y: auto; /* 纵向滚动条 */
    box-sizing: border-box;
}

/* 自定义滚动条样式 */
.image-container::-webkit-scrollbar {
    width: 8px;
}
.image-container::-webkit-scrollbar-track {
    background: #f1f1f1;
}
.image-container::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 4px;
}

/* 图片项样式保持之前版本 */
.image-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding-bottom: 10px; /* 防止底部被裁切 */
}

.image-wrapper {
    position: relative;
    width: 100%;
}

.image-wrapper::before {
    content: '';
    display: block;
    padding-top: 100%;
}

.image-container img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    transition: transform 0.3s ease;
}

/* 可选悬停动画 */
.image-container img:hover {
    cursor: pointer;
}

.image-title {
    width: 100%;
    font-size: 14px;
    color: #333;
    text-align: center;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    padding: 0 5px;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .image-container {
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    }
    .parent-container {
        height: 400px; /* 移动端调整高度 */
    }
}

/* 响应式调整 */
@media (max-width: 768px) {
    .image-container {
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    }
    .image-title {
        font-size: 12px;
    }
}

/* 点击选中文件 */
.select-file-active {
    width: 100%;
    height: 100%;
    cursor: pointer;
    background: var(--color-text-1);
    opacity: 0.7;
    position: absolute;
    z-index: 99;
    top: 0;
}

/* 文件来源选中 */
.file-select .file-source-active {
    background-color: var(--color-fill-1);color: rgb(var(--arcoblue-6))
}
</style>
