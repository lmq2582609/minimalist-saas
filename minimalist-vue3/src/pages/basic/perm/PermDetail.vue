<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-descriptions :column="2" bordered>
            <a-descriptions-item label="权限名称">{{ form.permName }}</a-descriptions-item>
            <a-descriptions-item label="权限编码">{{ form.permCode }}</a-descriptions-item>
            <a-descriptions-item label="权限类型">
                <dict-convert :dict-data="dicts[proxy.DICT.permType]" :dict-key="form.permType" />
            </a-descriptions-item>
            <a-descriptions-item label="权限图标">
                <functional-icons :icon="form.permIcon" size="30"></functional-icons>
            </a-descriptions-item>
            <a-descriptions-item label="路由地址">{{ form.permPath }}</a-descriptions-item>
            <a-descriptions-item label="权限状态">
                <dict-convert :dict-data="dicts[proxy.DICT.commonNumberStatus]" :dict-key="form.status" />
            </a-descriptions-item>
            <a-descriptions-item label="是否可见">
                <dict-convert :dict-data="dicts[proxy.DICT.yesNo]" :dict-key="form.visible" />
            </a-descriptions-item>
            <a-descriptions-item label="备注">{{ form.remark }}</a-descriptions-item>
        </a-descriptions>
    </a-spin>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import {getPermByPermIdApi} from "~/api/perm.js";
import FunctionalIcons from "~/components/iconSelect/FunctionalIcons.vue";

//全局实例
const { proxy } = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.yesNo, proxy.DICT.permType])
//接收父组件参数
const props = defineProps({
    params: {
        type: Object,
        default: () => {}
    }
})
//加载中...
const spinLoading = ref(false)
//表单
const form = reactive({
    //权限ID
    permId: null,
    //权限编码
    permCode: null,
    //权限名称
    permName: null,
    //路由地址
    permPath: null,
    //权限图标
    permIcon: null,
    //权限类型
    permType: null,
    //组件路径
    component: null,
    //是否可见
    visible: null,
    //备注
    remark: null,
    //权限状态
    status: null,
})
//加载权限数据
const loadPermInfo = (permId) => {
    spinLoading.value = true
    getPermByPermIdApi(permId).then(res => {
        if (res) {
            //数据赋值
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
    //权限ID
    if (props.params.permId) {
        //查询数据
        loadPermInfo(props.params.permId)
    }
}, { deep: true, immediate: true })
</script>
<style scoped></style>
