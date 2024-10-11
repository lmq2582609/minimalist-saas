<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-space size="medium" direction="vertical" fill>
            <div class="flex items-center justify-center text-3xl">{{ form.noticeTitle }}</div>
            <div class="flex items-center justify-center">
                <a-space size="medium">
                    <template #split>
                        <a-divider direction="vertical" />
                    </template>
                    <span>创建人：<dict-convert :dict-data="dicts[proxy.DICT.userList]" :dict-key="form.createId" /></span>
                    <span>创建时间：{{ form.createTime }}</span>
                    <span>发布人：<dict-convert :dict-data="dicts[proxy.DICT.userList]" :dict-key="form.publishAuthorId" /></span>
                    <span>发布部门：<dict-convert :dict-data="dicts[proxy.DICT.deptList]" :dict-key="form.publishDeptId" /></span>
                    <span>发布时间：{{ form.publishTime }}</span>
                </a-space>
            </div>
            <div class="flex items-center justify-center">
                <a-space size="medium">
                    <template #split>
                        <a-divider direction="vertical" />
                    </template>
                    <span>公告类型：<dict-convert :dict-data="dicts[proxy.DICT.noticeType]" :dict-key="form.noticeType" /></span>
                    <span>排序：{{ form.noticeSort }}</span>
                    <span>是否置顶：<dict-convert :dict-data="dicts[proxy.DICT.yesNo]" :dict-key="form.noticeTop" /></span>
                    <span>公告状态：<dict-convert :dict-data="dicts[proxy.DICT.status]" :dict-key="form.commonNumberStatus" /></span>
                    <span>是否外链：<dict-convert :dict-data="dicts[proxy.DICT.yesNo]" :dict-key="form.noticeOutChain" /></span>
                    <span>外链URL：
                        <template v-if="form.noticeLink">
                            <a-tooltip :content="form.noticeLink">
                                <a-link :href="form.noticeLink">点击跳转</a-link>
                            </a-tooltip>
                        </template>
                        <template v-else>无</template>
                    </span>
                </a-space>
            </div>
            <div class="flex items-center justify-center">
                封面图：
                <template v-if="form.noticePicFile">
                    <a-image-preview-group infinite>
                        <a-space>
                            <a-image v-for="(image, index) in form.noticePicFile" :key="index" :src="image.fileUrl" width="200" class="cursor-pointer" />
                        </a-space>
                    </a-image-preview-group>
                </template>
                <template v-else>无</template>
            </div>
            <div class="flex justify-center">
                <div class="w-[70%]" v-html="form.noticeContent"></div>
            </div>
        </a-space>
    </a-spin>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import {getNoticeByNoticeIdApi} from "~/api/notice.js";
import 'tinymce/skins/ui/oxide/content.css'
import 'tinymce/skins/ui/oxide/skin.css'

//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.noticeType, proxy.DICT.userList, proxy.DICT.deptList, proxy.DICT.yesNo])
//加载中...
const spinLoading = ref(false)
//接收父组件参数
const props = defineProps({
    params: {
        type: Object,
        default: () => {}
    }
})
//表单
const form = reactive({
    //系统公告ID
    noticeId: null,
    //系统公告标题
    noticeTitle: null,
    //系统公告类型
    noticeType: null,
    //系统公告内容
    noticeContent: null,
    //公告封面图
    noticePicFile: null,
    //是否置顶
    noticeTop: null,
    //排序值
    noticeSort: null,
    //是否外链
    noticeOutChain: null,
    //外链URL
    noticeLink: null,
    //发布部门
    publishDeptId: null,
    //发布人
    publishAuthorId: null,
    //发布时间
    publishTime: null,
    //系统公告状态
    status: null,
    //创建人
    createId: null,
    //创建时间
    createTime: null
})
//加载公告详细信息
const loadNoticeInfo = (noticeId) => {
    spinLoading.value = true
    getNoticeByNoticeIdApi(noticeId).then(res => {
        //数据赋值
        if (res) {
            for (let key in res) {
                if (form.hasOwnProperty(key)) {
                    form[key] = res[key]
                }
            }
        }
    }).finally(() => {
        spinLoading.value = false
    })
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //公告ID
    if (props.params.noticeId) {
        //查询数据
        loadNoticeInfo(props.params.noticeId)
    }
}, { deep: true, immediate: true })
</script>
<style scoped></style>
