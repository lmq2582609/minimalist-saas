<template>
    <div class="w-[100%]">
        <editor id="tinymce" v-model="editorHtml" :init="tinymceInit"></editor>

        <!-- 文件选择组件演示 -->
        <a-modal v-model:visible="modal.visible" width="60%" :esc-to-close="false" :mask-closable="false" draggable :footer="false" style="z-index: 99999">
            <template #title>{{ modal.title }}</template>
            <file-select :file-type="modal.fileType" :limit="modal.limit" @ok="onOk" @cancel="onCancel" v-if="modal.visible" />
        </a-modal>
    </div>
</template>

<script setup>
import {reactive, ref} from 'vue'
import { uploadFileApi } from "~/api/file.js";
import tinymce from "tinymce/tinymce";
import "tinymce/models/dom";
import "tinymce/themes/silver/theme";
import Editor from "@tinymce/tinymce-vue";
//tinymce插件 -> 更多插件参考：https://www.tiny.cloud/docs/plugins/
import 'tinymce/plugins/image' // 插入上传图片插件
import "tinymce/plugins/importcss"; //图片工具
import 'tinymce/plugins/media' // 插入视频插件
import 'tinymce/plugins/table' // 插入表格插件
import 'tinymce/plugins/lists' // 列表插件
import "tinymce/plugins/charmap"; // 特殊字符
import 'tinymce/plugins/wordcount' // 字数统计插件
import "tinymce/plugins/codesample"; // 插入代码
import "tinymce/plugins/code"; // 查看源码
import "tinymce/plugins/fullscreen"; //全屏
import 'tinymce/plugins/link' //超链接
import 'tinymce/plugins/preview' // 预览
import "tinymce/plugins/searchreplace"; //查询替换
//字体图标
import 'tinymce/icons/default/icons.js'
import FileSelect from "~/pages/basic/file/FileSelect.vue";
import {fileType} from "~/utils/sys.js";


/********************** 文件选择组件开始 **********************/
//接收父组件参数
const props = defineProps({
    //编辑器高度
    editorHeight: {
        type: Number,
        default: 450
    },
    //文件来源 -> 若上传文件，需传入此参数，用于标识文件的作用
    fileSource: {
        type: Number,
        default: 0
    },
    //存储信息ID -> 标识用哪个存储，不穿则使用默认的存储
    storageId: {
        type: String,
        default: null
    }
})
//文件选择组件
const modal = reactive({
    //是否显示
    visible: false,
    //模态框标题
    title: '文件选择',
    //文件类型
    fileType: '',
    //可以选择几个文件
    limit: 1,
    //将文件放置到富文本编辑器中的回调
    callback: null,
});
//模态框 -> 确认
const onOk = (selectedFiles) => {
    let file = null
    if (selectedFiles && selectedFiles.length > 0) {
        file = selectedFiles[0]
    } else {
        //未选择文件，关闭
        onCancel()
        return
    }
    if (modal.fileType === fileType.image.key) {
        //图片
        modal.callback(file.fileUrl, {alt: file.fileName})
    } else if (modal.fileType === fileType.video.key) {
        //视频
        modal.callback(file.fileUrl)
    } else {
        //文件
        modal.callback(file.fileUrl, {text: file.fileName, title: file.fileName})
    }
    onCancel()
}
//模态框 -> 取消
const onCancel = () => {
    //隐藏文件选择器
    modal.visible = false
    //清空回调函数
    modal.callback = null
}
/********************** 文件选择组件结束 **********************/


//富文本内容
const editorHtml = ref("请输入内容");
//富文本参数
const tinymceInit = {
    //引入语言包 -> public目录下静态资源
    language_url: "/tinymce/skins/langs/zh-Hans.js",
    //语言类型 -> 中文
    language: "zh-Hans",
    //样式 -> public目录下静态资源
    skin_url: "/tinymce/skins/ui/oxide",
    //以css文件方式自定义可编辑区域的css样式，css文件需自己创建并引入 -> public目录下静态资源
    content_css: "/tinymce/skins/content/default/content.css",
    //富文本插件
    plugins: "link lists image media importcss code table wordcount preview searchreplace fullscreen codesample charmap",
    //工具栏
    toolbar: "undo redo outdent indent removeformat | bold italic underline strikethrough charmap | fontsizeselect | forecolor backcolor | alignment bullist numlist link blockquote codesample table | image media file | searchreplace fullscreen preview",
    //是否禁用"Powered by TinyMCE"
    branding: false,
    //顶部菜单栏是否显示
    menubar: false,
    //底部状态栏是否显示
    statusbar: true,
    //文件、图片、视频增加文件选择器处理
    file_picker_types: 'file image media',
    //文件选择器处理
    file_picker_callback: function(callback, value, meta) {
        //给定回调函数
        modal.callback = callback
        //图片
        if (meta.filetype === 'image') {
            modal.fileType = fileType.image.key
        }
        //视频
        if (meta.filetype === 'media') {
            modal.fileType = fileType.video.key
        }
        //文件
        if (meta.filetype === 'file') {
            modal.fileType = null
        }
        //显示文件选择器
        modal.visible = true
    },
    //初始化完成触发
    init_instance_callback: (editor) => {
        //console.log("富文本编辑器初始化完成：", editor)
    },
    //限制高度
    height: props.editorHeight,
    //插入word文档需要该属性
    //paste_convert_word_fake_lists: false,
    //自定义图片上传处理
    images_upload_handler: (blobInfo, progress) => new Promise((resolve, reject) => {
        //上传进度监控
        const onUploadProgress = (e) => {
            progress(e.progress * 100)
        }
        //单文件上传
        const formData = new FormData();
        formData.append("file", blobInfo.blob())
        formData.append("fileSource", props.fileSource)
        if (props.storageId) {
            formData.append("storageId", props.storageId)
        }
        uploadFileApi(formData, onUploadProgress).then(res => {
            //将上传图片后的返回的url放入resolve
            resolve(res.fileUrl);
        }).catch(e => {
            reject('上传失败')
        })
    }),
    setup: (editor) => {
        //自定义对齐按钮 - 下拉形式
        editor.ui.registry.addMenuButton('alignment', {
            icon: 'align-left',
            tooltip: '对齐方式',
            fetch: (callback) => {
                const items = [
                    {
                        type: 'menuitem',
                        text: '左对齐',
                        icon: 'align-left',
                        onAction: () => editor.execCommand('JustifyLeft')
                    },
                    {
                        type: 'menuitem',
                        text: '居中对齐',
                        icon: 'align-center',
                        onAction: () => editor.execCommand('JustifyCenter')
                    },
                    {
                        type: 'menuitem',
                        text: '右对齐',
                        icon: 'align-right',
                        onAction: () => editor.execCommand('JustifyRight')
                    },
                    {
                        type: 'menuitem',
                        text: '两端对齐',
                        icon: 'align-justify',
                        onAction: () => editor.execCommand('JustifyFull')
                    }
                ];
                callback(items);
            },
            //图标变换
            onSetup: (api) => {
                const iconMap = {
                    'JustifyLeft': 'align-left',
                    'JustifyCenter': 'align-center',
                    'JustifyRight': 'align-right',
                    'JustifyFull': 'align-justify'
                };
                const updateState = () => {
                    const activeAlignment = Object.keys(iconMap).find(cmd =>
                            editor.queryCommandState(cmd)
                    ) || 'JustifyLeft';
                    api.setIcon(iconMap[activeAlignment])
                }
                editor.on('NodeChange', updateState)
                return () => editor.off('NodeChange', updateState)
            }
        })
    },
}
//获取富文本数据 -> 返回html内容和内容中所使用的的图片URL
const getEditorContent = () => {
    return editorHtml.value
}
//设置富文本数据
const setEditorContent = (content) => {
    if (content) {
        editorHtml.value = content
    } else {
        editorHtml.value = '请输入内容'
    }
}
//初始化富文本
tinymce.init({});
//暴露子组件
defineExpose({getEditorContent, setEditorContent})
</script>
<style scoped>
:deep(.tox-promotion) {
    display: none;
}
</style>
