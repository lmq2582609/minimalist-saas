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

                <!-- 文件名称搜索 -->
                <a-space class="w-full flex justify-between mb-3 file-search-container">
                    <a-upload :action="uploadFileUrl" multiple with-credentials
                              :accept="accept"
                              :custom-request="customUploadFile" :show-file-list="false" />
                    <a-input-search v-model="searchForm.fileName" :style="{width:'180px'}" placeholder="请输入文件名称" search-button @search="getPageList"/>
                </a-space>

                <!-- 文件列表 -->
                <div class="parent-container">
                    <div class="image-container" v-if="datatable.records.length > 0">
                        <div class="image-item" v-for="file in datatable.records">
                            <div class="image-wrapper">
                                <!-- 图片 -->
                                <img :src="file.fileThUrl" alt="" @click="selectFileBtnClick(file)" v-if="fileAccept.img.includes(file.fileTypeSuffix)">
                                <!-- 视频 -->
                                <div class="w-[100%] h-[100%] file-video" :id="file.fileId" @click.stop="selectFileBtnClick(file)" v-else-if="fileAccept.video.includes(file.fileTypeSuffix)"></div>
                                <!-- 其他文件 -->
                                <img class="p-8" src="../../../assets/default-file-icon.png" @click="selectFileBtnClick(file)" alt="" v-else>

                                <!-- 选中效果 -->
                                <div class="flex items-center justify-center" v-if="checkSelect(file) >= 0"
                                     :class="checkSelect(file) >= 0 ? 'select-file-active' : ''"
                                     @click="selectFileBtnClick(file)">
                                    <icon-check class="text-6xl" style="color: var(--color-text-4)" />
                                </div>
                            </div>
                            <a-tooltip :content="file.fileName">
                                <div class="image-title">{{file.fileName}}</div>
                            </a-tooltip>
                        </div>
                    </div>
                    <a-empty class="mt-5" v-else />
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
import {getCurrentInstance, nextTick, reactive, ref, watch} from "vue";
import {getPageFileListApi, uploadFileApi} from "~/api/file.js";
import {status, fileType, fileAccept, videoTypeHandler} from "~/utils/sys.js";
import Player from 'xgplayer';
import 'xgplayer/dist/index.min.css';

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.fileSource])
//允许上传的文件类型
const accept = ref('')
//接收父组件参数
const props = defineProps({
    //可以选择几个文件
    limit: {
        type: Number,
        default: () => 1
    },
    //文件类型
    fileType: {
        type: String,
        default: () => null
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
    status: null,
    //页码
    pageNum: 1,
    //条数
    pageSize: 10
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
        //渲染视频 - 需在dom加载后渲染
        nextTick(() => {
            renderVideo(datatable.records)
        })
    }).finally(() => {
        spinLoading.value = false
    })
}

//选中的文件
const selectFile = ref([])
//点击选中
const selectFileBtnClick = (file) => {
    //检查可以选择几个文件 - 默认一个
    let count = props.limit
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
            //达到最大个数，校验
            if (count === 1) {
                //最大个数=1，直接替换
                selectFile.value = [file]
            } else {
                //最大个数 > 1，提示最多可以选择 count 个
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
//父组件函数
const emits = defineEmits(['ok', 'cancel'])
//取消 -> 点击
const cancelBtnClick = () => {
    emits('cancel')
}
//确定 -> 点击
const okBtnClick = () => {
    emits("ok", selectFile.value)
}

//上传文件地址
const uploadFileUrl = import.meta.env.VITE_UPLOAD_FILE_URL
//自定义上传
const customUploadFile = (option) => {
    //上传进度监控
    const onUploadProgress = (e) => {
        option.onProgress(e.progress * 100)
    }
    //上传参数
    const formData = new FormData();
    formData.append("file", option.fileItem.file);
    formData.append("fileSource", -1);
    if (props.storageId) {
        formData.append("storageId", props.storageId)
    }
    spinLoading.value = true
    uploadFileApi(formData, onUploadProgress).then(res => {
        //调用onSuccess方法将响应数据附加到fileItem中的response字段上
        option.onSuccess(res)
        proxy.$msg.success(proxy.operationType.upload.success)
        //刷新文件列表
        getPageList()
    }).catch(e => {
        //上传失败
        option.onError(e)
    }).finally(() => {
        spinLoading.value = false
    })
}

//渲染视频
const renderVideoCache = ref([])
const renderVideo = (data) => {
    //渲染前销毁
    for (const player of renderVideoCache.value) {
        player.destroy()
    }
    renderVideoCache.value = []
    //渲染视频
    for (const file of data) {
        if (fileAccept.video.includes(file.fileTypeSuffix)) {
            let player = new Player({
                id: file.fileId,
                url: file.fileUrl,
                height: '100%',
                width: '100%',
                //关闭双击播放器进入全屏
                closeVideoDblclick: true,
                //关闭单击播放器区域切换播放/暂停
                closeVideoClick: true
            })
            renderVideoCache.value.push(player)
        }
    }
}

//监听参数变化
watch(() => props.fileType, (newVal, oldVal) => {
    //文件类型
    if (props.fileType) {
        searchForm.fileType = props.fileType

        //允许上传的文件类型
        //图片文件
        if (props.fileType === fileType.image.key) {
            accept.value = fileAccept.img
        }
        //视频文件
        else if (props.fileType === fileType.video.key) {
            accept.value = fileAccept.video
        }
        //所有文件
        else {
            accept.value = ''
        }
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
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
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
.image-wrapper img,.file-video {
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
.image-wrapper img:hover {
    cursor: pointer;
}
/* 可选悬停动画 */
.image-wrapper .file-video:hover {
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
    .image-title {
        font-size: 12px;
    }
    .file-search-container {
        display: none;
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
