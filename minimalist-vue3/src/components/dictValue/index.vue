<template>
    <template v-if="dict.dictClass && dict.dictClass !== 'default'">
        <a-tag :color="dict.dictClass" bordered>{{dict.dictValue}}</a-tag>
    </template>
    <template v-else-if="dict.dictClass && dict.dictClass === 'default'">
        <a-tag bordered>{{dict.dictValue}}</a-tag>
    </template>
    <template v-else>
        {{dict.dictValue}}
    </template>
</template>
<script setup>
import { reactive, watch } from 'vue'

//接收父组件参数
const props = defineProps({
    //字典数据，通过父组件传入
    dictData: {
        type: Array,
        default: null
    },
    //字典key
    dictKey: {
        type: [Number, String, Boolean],
        default: null
    }
})

//展示的数据
const dict = reactive({})
//查找字典数据
const findDictByDictKey = () => {
    let dictList = props.dictData
    if (dictList && dictList.length > 0) {
        for (let i = 0;i < dictList.length;i++) {
            if (dictList[i].dictKey == props.dictKey) {
                return dictList[i]
            }
        }
    }
    return ''
}
//监听参数变化
watch(() => [props.dictKey, props.dictData], (newVal, oldVal) => {
    if (newVal) {
        Object.assign(dict, findDictByDictKey())
    }
}, { deep: true, immediate: true })
</script>