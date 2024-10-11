<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" layout="vertical" :rules="rules" auto-label-width>
            <a-form-item field="postName" label="岗位名称" required>
                <a-input v-model="form.postName" placeholder="岗位名称" />
            </a-form-item>
            <a-form-item field="postCode" label="岗位编码" required tooltip="岗位的唯一英文标识">
                <a-input v-model="form.postCode" placeholder="岗位编码" />
            </a-form-item>
            <a-form-item field="postSort" label="排序值" required>
                <a-input-number :min="0" v-model="form.postSort" placeholder="排序值" />
            </a-form-item>
            <a-form-item field="status" label="岗位状态" required v-if="props.params.operationType === proxy.operationType.update.type">
                <a-select v-model="form.status" placeholder="岗位状态" allow-clear>
                    <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                </a-select>
            </a-form-item>
            <a-form-item field="remark" label="备注">
                <a-textarea v-model="form.remark" placeholder="备注" />
            </a-form-item>
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
import { addPostApi, updatePostByPostIdApi, getPostByPostIdApi } from "~/api/post.js";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus])
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
//表单校验规则
const rules = {
    postName: [{required: true, message: '岗位名称不能为空', trigger: 'submit'}],
    postCode: [{required: true, message: '岗位编码不能为空', trigger: 'submit'}],
    postSort: [{required: true, message: '排序值不能为空', trigger: 'submit'}],
}
//确定 -> 点击
const okBtnClick = () => {
    //表单验证
    formRef.value.validate((valid) => {
        if (valid) {return false}
        //添加
        if (props.params.operationType === proxy.operationType.add.type) {
            spinLoading.value = true
            addPostApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            spinLoading.value = true
            updatePostByPostIdApi(form).then(() => {
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
        //加载岗位信息
        loadPostInfo(props.params.postId)
    }
}, { deep: true, immediate: true })
</script>
<style scoped></style>
