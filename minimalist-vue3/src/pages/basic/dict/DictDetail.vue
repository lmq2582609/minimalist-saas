<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-descriptions :column="2" bordered>
            <a-descriptions-item label="字典名称">{{ form.dictName }}</a-descriptions-item>
            <a-descriptions-item label="字典类型">{{ form.dictType }}</a-descriptions-item>
            <a-descriptions-item label="字典描述">{{ form.dictDesc }}</a-descriptions-item>
        </a-descriptions>

        <!-- 分割线 -->
        <a-divider orientation="center">字典数据</a-divider>

        <a-descriptions :column="5" bordered>
            <template v-for="(item, index) in form.dictDataList" :key="index">
                <a-descriptions-item label="字典Key">{{ item.dictKey }}</a-descriptions-item>
                <a-descriptions-item label="字典Value">{{ item.dictValue }}</a-descriptions-item>
                <a-descriptions-item label="排序值">{{ item.dictOrder }}</a-descriptions-item>
                <a-descriptions-item label="字典样式">
                    <template v-if="item.dictClass">
                        <dict-convert :dict-data="dicts[proxy.DICT.dictClass]" :dict-key="item.dictClass" />
                    </template>
                    <template v-else>
                        无样式
                    </template>
                </a-descriptions-item>
                <a-descriptions-item label="字典状态">
                    <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="item.status" />
                </a-descriptions-item>
            </template>
        </a-descriptions>
    </a-spin>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import { getDictByDictTypeApi } from '~/api/dict'

//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.dictClass])
//接收父组件参数
const props = defineProps({
    params: {
        type: Object,
        default: () => {}
    }
})
//表单
const form = reactive({
    //字典名称
    dictName: null,
    //字典类型
    dictType: null,
    //字典描述
    dictDesc: null,
    //字典数据
    dictDataList: []
})
//加载中...
const spinLoading = ref(false)
//根据字典类型加载字典数据
const loadDictInfo = (dictType) => {
    //根据类型查询字典
    spinLoading.value = true
    getDictByDictTypeApi(dictType).then(res => {
        if (res && res.dictDataList) {
            //数据赋值
            form.dictName = res.dictName
            form.dictType = res.dictType
            form.dictDesc = res.dictDesc
            form.dictDataList = res.dictDataList
        }
    }).finally(() => {
        spinLoading.value = false
    })
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //字典类型
    if (props.params.dictType) {
        //查询数据
        loadDictInfo(props.params.dictType)
    }
}, { deep: true, immediate: true })
</script>

<style scoped>

</style>
