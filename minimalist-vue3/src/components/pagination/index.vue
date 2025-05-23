<template>
    <a-pagination show-total show-jumper show-page-size
            :total="total" :size="size"
            :page-size-options="pageSizeOptions"
            v-model:current="current"
            v-model:page-size="limit"
            @change="handleCurrentChange"
            @page-size-change="handleSizeChange" />
</template>

<script setup>
import { computed } from 'vue'

//接收父组件参数
const props = defineProps({
    //总条数
    total: {
        type: Number,
        default: 0
    },
    //当前页
    pageNum: {
        type: Number,
        default: 1
    },
    //每页条数
    pageSize: {
        type: Number,
        default: 10
    },
    //数据条数选择器的选项列表
    pageSizeOptions: {
        type: Array,
        default: () => [10, 20, 30, 40, 50]
    },
    //分页选择器的大小
    size: {
        type: String,
        default: 'medium'
    }
})
const emits = defineEmits(['update:pageNum', 'update:pageSize', 'pagination'])
//计算属性 -> 当前页
const current = computed({
    get() {
        return props.pageNum;
    },
    set(val) {
        emits("update:pageNum", val);
    },
});
//计算属性 -> 每页条数
const limit = computed({
    get() {
        return props.pageSize;
    },
    set(val) {
        emits("update:pageSize", val);
    },
});

//数据条数改变时触发
function handleSizeChange(val) {
    //强制跳转到第 1 页
    if (current.value * val > props.total) {
        current.value = 1
    }
    //重新加载数据
    emits('pagination', { pageNum: current.value, pageSize: val })
}

//页码改变时触发
function handleCurrentChange(val) {
    //重新加载数据
    emits('pagination', { pageNum: val, pageSize: limit.value })
}

</script>

<style scoped>

</style>
