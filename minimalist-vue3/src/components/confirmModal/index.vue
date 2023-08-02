<template>
    <a-modal :visible="visible" :messageType="status" unmount-on-close :footer="false" simple :modal-style="{padding: '20px 15px 15px 20px'}">
        <template #title>{{title}}</template>
        <div>{{content}}</div>
        <div class="flex justify-end mt-3">
            <a-space>
                <a-button @click="handleCancel()">取消</a-button>
                <a-button type="primary" @click="handleOk()">确定</a-button>
            </a-space>
        </div>
    </a-modal>
</template>

<script setup>
import { computed } from 'vue'
//接收父组件参数
const props = defineProps({
    //模态框标题
    title: {
        type: String,
        default: '提示'
    },
    //状态，影响样式 info success warning danger
    status: {
        type: String,
        default: 'info'
    },
    //模态框内容
    content: {
        type: String,
        default: null
    },
    //是否显示
    visible: {
        type: Boolean,
        default: false
    }
})

//父组件选择事件
const emits = defineEmits(['ok', 'cancel'])
//点击确认
const handleOk = () => {
    emits('ok')
}
const handleCancel = () => {
    emits('cancel')
}
</script>