<template>
    <div class="w-[100%]">
        <editor id="tinymce" v-model="editorHtml" :init="tinymceInit"></editor>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { uploadFileApi } from "~/api/file.js";
import tinymce from "tinymce/tinymce";
import "tinymce/models/dom";
import "tinymce/themes/silver/theme";
import Editor from "@tinymce/tinymce-vue";
//tinymce插件 -> 更多插件参考：https://www.tiny.cloud/docs/plugins/
import 'tinymce/plugins/image' // 插入上传图片插件
import "tinymce/plugins/importcss"; //图片工具
//import 'tinymce/plugins/media' // 插入视频插件
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
    plugins: "link lists image importcss code table wordcount preview searchreplace fullscreen codesample charmap",
    //工具栏
    toolbar: " bold italic underline strikethrough | fontsizeselect | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | outdent indent blockquote | link unlink image importcss wordcount table codesample charmap | removeformat | undo redo | searchreplace code fullscreen preview",
    //是否禁用"Powered by TinyMCE"
    branding: false,
    //顶部菜单栏是否显示
    menubar: false,
    //底部状态栏是否显示
    statusbar: true,
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
    })
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
