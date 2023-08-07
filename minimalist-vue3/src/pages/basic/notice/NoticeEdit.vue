<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" :rules="rules" auto-label-width>
            <div class="flex justify-between" style="flex-wrap: wrap;">
                <a-form-item class="w-[100%]" field="noticeTitle" label="公告标题" required>
                    <a-input v-model="form.noticeTitle" placeholder="公告标题" />
                </a-form-item>
                <a-form-item class="w-[33%]" field="noticeType" label="公告类型" required>
                    <a-select v-model="form.noticeType" placeholder="公告类型" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.noticeType]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[33%]" field="publishAuthorId" label="发布人" required>
                    <a-select v-model="form.publishAuthorId" placeholder="发布人" allow-clear allow-search>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.userList]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[33%]" field="publishDeptId" label="发布部门" tooltip="表示发布此公告的部门，可为空">
                    <a-tree-select v-model="form.publishDeptId" :data="deptTree" placeholder="发布部门" allow-clear
                                   :fieldNames="{key: 'deptId', title: 'deptName', children: 'children'}" />
                </a-form-item>
                <a-form-item class="w-[33%]" field="noticeTop" label="是否置顶" tooltip="置顶会让此公告展示在最前面">
                    <a-radio-group v-model="form.noticeTop" type="button">
                        <a-radio v-for="(item, index) in dicts[proxy.DICT.yesNo]" :key="index" :value="item.dictKey">{{ item.dictValue }}</a-radio>
                    </a-radio-group>
                </a-form-item>
                <a-form-item class="w-[33%]" field="noticeOutChain" label="是否外链" tooltip="表示此公告是一个外部链接，展示时点击会打开新页面跳转">
                    <a-radio-group v-model="form.noticeOutChain" type="button">
                        <a-radio v-for="(item, index) in dicts[proxy.DICT.yesNo]" :key="index" :value="item.dictKey">{{ item.dictValue }}</a-radio>
                    </a-radio-group>
                </a-form-item>
                <a-form-item class="w-[33%]" field="noticeLink" label="外链URL" tooltip="`是否外链`选择`是`，跳转的URL">
                    <a-input v-model="form.noticeLink" placeholder="外部链接URL" />
                </a-form-item>
                <a-form-item class="w-[33%]" field="noticeSort" label="排序值" tooltip="公告的展示顺序，数值越小排序越靠前">
                    <a-input-number :min="0" v-model="form.noticeSort" placeholder="排序值" />
                </a-form-item>
                <a-form-item class="w-[33%]" field="noticeTimeInterval" label="延期发布" tooltip="表示想让此公告在何时发布，比如选择明天10点，则此公告将在明天10点发布。不选择将立即发布">
                    <a-date-picker class="w-[100%]" v-model="form.noticeTimeInterval" show-time format="YYYY-MM-DD HH:mm:ss" disabled-input placeholder="延期发布时间" />
                </a-form-item>
                <a-form-item class="w-[33%]" field="status" label="公告状态" required>
                    <a-select v-model="form.status" placeholder="公告状态" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.noticeStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[100%]" field="noticePic" label="公告封面图">
                    <upload-file :file-list="noticePicList" :file-source="fileSource.notice_cover_img.key" :accept="fileAccept.img" :multiple="true" :list-type="fileListType.pictureCard" :limit="5" ref="uploadRef" />
                </a-form-item>
                <a-form-item class="w-[100%]" field="noticeContent" label="公告内容" required>
                    <tinymce-editor :editorHeight="450" :file-source="fileSource.notice_content_img.key" ref="editorRef" />
                </a-form-item>
            </div>
        </a-form>

        <!-- 分割线 -->
        <a-divider class="mt-0" />

        <div class="flex justify-end">
            <a-space>
                <a-button @click="cancelBtnClick()">取消</a-button>
                <a-button type="primary" @click="okBtnClick()">确定</a-button>
            </a-space>
        </div>
    </a-spin>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import { addNoticeApi, updateNoticeByNoticeIdApi, getNoticeByNoticeIdApi } from "~/api/notice.js";
import { getDeptListApi } from "~/api/dept.js";
import TinymceEditor from '~/components/tinymceEditor/index.vue'
import UploadFile from '~/components/uploadFile/index.vue'
import { fileSource, fileAccept, fileListType } from "~/utils/sys.js";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.postStatus, proxy.DICT.noticeType, proxy.DICT.yesNo, proxy.DICT.userList, proxy.DICT.noticeStatus])
//接收父组件参数
const props = defineProps({
    params: {
        type: Object,
        default: () => {}
    }
})
//父组件函数
const emits = defineEmits(['ok', 'cancel'])
//加载中...
const spinLoading = ref(false)
//表单ref
const formRef = ref(null)
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
    noticePic: null,
    //是否置顶 -> 默认否
    noticeTop: proxy.yesNo.no.key,
    //延期发布时间
    noticeTimeInterval: null,
    //排序值
    noticeSort: null,
    //是否外链 -> 默认否
    noticeOutChain: proxy.yesNo.no.key,
    //外链URL
    noticeLink: null,
    //发布部门
    publishDeptId: null,
    //发布人
    publishAuthorId: null,
    //系统公告状态
    status: null
})
//表单校验规则
const rules = {
    noticeTitle: [{required: true, message: '系统公告标题不能为空', trigger: 'submit'}],
    noticeType: [{required: true, message: '系统公告类型不能为空', trigger: 'submit'}],
    noticeContent: [{required: true, message: '系统公告内容不能为空', trigger: 'submit'}],
    publishAuthorId: [{required: true, message: '发布人不能为空', trigger: 'submit'}],
    status: [{required: true, message: '系统公告状态不能为空', trigger: 'submit'}]
}
//编辑器ref
const editorRef = ref(null)
//封面上传uploadRef
const uploadRef = ref(null)
//确定 -> 点击
const okBtnClick = () => {
    //公告封面图处理，多张图片URL逗号分割
    let noticePic = uploadRef.value.getUploadFileUrl()
    if (noticePic && noticePic.length > 0) {
        form.noticePic = noticePic.join("|")
    }
    //获取富文本数据
    form.noticeContent = editorRef.value.getEditorContent()
    //表单验证
    formRef.value.validate((valid) => {
        if (valid) {return false}
        //添加
        if (props.params.operationType === proxy.operationType.add.type) {
            spinLoading.value = true
            addNoticeApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            spinLoading.value = true
            updateNoticeByNoticeIdApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.update.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
    })
}
//取消 -> 点击
const cancelBtnClick = () => {
    emits('cancel')
}
//封面图列表
const noticePicList = ref([])
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
            //富文本内容
            editorRef.value.setEditorContent(res.noticeContent)
            //封面图回显
            if (res.noticePic) {
                let noticeArr = res.noticePic.split('|')
                for (let i = 0; i < noticeArr.length; i++) {
                    noticePicList.value.push({
                        url: noticeArr[i]
                    })
                }
            }
        }
    }).finally(() => {
        spinLoading.value = false
    })
}
//部门树数据
const deptTree = ref([])
//获取部门数据列表
const getDeptTree = () => {
    getDeptListApi({}).then(res => {
        deptTree.value = res
    })
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //公告ID
    if (props.params.noticeId) {
        //加载公告信息
        loadNoticeInfo(props.params.noticeId)
    }
    //加载部门树
    getDeptTree()
}, { deep: true, immediate: true })
</script>
<style scoped></style>
