<template>
    <a-select :modelValue="modelValue" @change="selectChangeHandler" placeholder="图标" allow-search allow-clear>
        <template #prefix v-if="modelValue">
            <functional-icons :icon="modelValue" size="30"></functional-icons>
        </template>
        <a-option v-for="item in icons" :key="item" :label="item" :value="item">
            <div class="flex items-center justify-between">
                <functional-icons :icon="item" size="30"></functional-icons>
                <span class="text-gray-500 ml-3">{{ item }}</span>
            </div>
        </a-option>
    </a-select>
</template>
<script setup>
import { ref } from 'vue'
import * as iconList from '@arco-design/web-vue/es/icon';
import FunctionalIcons from "~/components/iconSelect/FunctionalIcons.vue";

//接收父组件参数
defineProps({
    //v-model
    modelValue: String
})
//更新选中的值
const emit = defineEmits(['update:modelValue'])
//选择事件
const selectChangeHandler = (val) => {
    emit('update:modelValue', val);
}
//所有图标
const icons = ref([])
//去除图标数组中的default
const iconHanlder = () => {
    let allIconName = Object.keys(iconList)
    for (let i = 0;i < allIconName.length;i++) {
        if (allIconName[i] === 'default') {
            allIconName.splice(i, 1)
        }
    }
    icons.value = allIconName
}

iconHanlder()
</script>