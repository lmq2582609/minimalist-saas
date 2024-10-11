<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-descriptions :column="2" bordered>
            <a-descriptions-item label="岗位名称">{{ form.postName }}</a-descriptions-item>
            <a-descriptions-item label="岗位编码">{{ form.postCode }}</a-descriptions-item>
            <a-descriptions-item label="排序">{{ form.postSort }}</a-descriptions-item>
            <a-descriptions-item label="岗位状态">
                <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="form.status" />
            </a-descriptions-item>
            <a-descriptions-item label="备注">{{ form.remark }}</a-descriptions-item>
        </a-descriptions>
    </a-spin>
</template>
<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import { getPostByPostIdApi } from "~/api/post.js";

//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus])
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
    //岗位ID
    postId: null,
    //岗位名称
    postName: null,
    //岗位编码
    postCode: null,
    //排序值
    postSort: null,
    //岗位状态
    status: null,
    //备注
    remark: null
})
//加载岗位详细信息
const loadPostInfo = (postId) => {
    spinLoading.value = true
    getPostByPostIdApi(postId).then(res => {
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
    //岗位ID
    if (props.params.postId) {
        //查询数据
        loadPostInfo(props.params.postId)
    }
}, { deep: true, immediate: true })
</script>
<style scoped></style>
