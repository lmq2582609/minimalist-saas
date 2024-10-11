<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" layout="vertical" ref="formRef" :rules="rules" auto-label-width>
            <a-row :gutter="12">
                <a-col :span="12">
                    <a-form-item field="dictName" label="字典名称" required>
                        <a-input v-model="form.dictName" placeholder="字典名称" />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item field="dictType" label="字典类型" required>
                        <a-input v-model="form.dictType" placeholder="字典类型" />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row :gutter="2">
                <a-form-item field="dictDesc" label="字典描述">
                    <a-textarea v-model="form.dictDesc" placeholder="字典描述" />
                </a-form-item>
            </a-row>

            <!-- 分割线 -->
            <div class="w-[calc(100%-100px)] flex justify-between items-center">
                <a-divider orientation="center">字典数据</a-divider>
                <a-button class="ml-5 w-[100px]" type="primary" @click="addDictDataBtnClick()" size="mini">
                    <template #icon><icon-plus /></template>
                    <span>添加</span>
                </a-button>
            </div>

            <a-scrollbar class="w-[100%] max-h-[250px] overflow-auto" type="track">
                <div class="w-[100%]">
                    <a-row class="w-[100%]" :gutter="12" v-for="(dict, index) in form.dictDataList" :key="index" justify="space-between">
                        <a-col :span="props.params.operationType === proxy.operationType.update.type ? 6 : 8">
                            <a-form-item field="dictKey" label="字典key">
                                <a-input v-model="dict.dictKey" placeholder="字典key" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="props.params.operationType === proxy.operationType.update.type ? 6 : 8">
                            <a-form-item field="dictValue" label="字典Value">
                                <a-input v-model="dict.dictValue" placeholder="字典Value" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="4">
                            <a-form-item field="dictClass" label="字典样式">
                                <a-select v-model="dict.dictClass" placeholder="字典样式" allow-clear allow-search>
                                    <a-option v-for="(item, index) in dicts[proxy.DICT.dictClass]" :key="index" :value="item.dictKey" :label="item.dictValue"  />
                                </a-select>
                            </a-form-item>
                        </a-col>
                        <a-col :span="2">
                            <a-form-item field="dictOrder" label="排序">
                                <a-input-number v-model="dict.dictOrder" placeholder="排序" :min="0" />
                            </a-form-item>
                        </a-col>
                        <a-col :span="3" v-if="props.params.operationType === proxy.operationType.update.type">
                            <a-form-item field="status" label="状态">
                                <a-select v-model="dict.status" placeholder="状态" allow-clear>
                                    <a-option v-for="(item, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="item.dictKey" :label="item.dictValue" />
                                </a-select>
                            </a-form-item>
                        </a-col>
                        <a-col :span="2">
                            <a-popconfirm content="确认要删除吗?" @ok="delDictDataBtnClick(dict)">
                                <a-button class="mt-[1.9rem] flex justify-center" type="primary" status="danger">
                                    <template #icon><icon-minus /></template>
                                </a-button>
                            </a-popconfirm>
                        </a-col>
                    </a-row>
                </div>
            </a-scrollbar>
        </a-form>

        <!-- 分割线 -->
        <a-divider class="mt-0" />

        <div class="flex justify-end">
            <a-space>
                <a-button @click="cancelBtnClick">取消</a-button>
                <a-button type="primary" @click="okBtnClick">确定</a-button>
            </a-space>
        </div>
    </a-spin>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import { addDictApi, updateDictApi, getDictByDictTypeApi, deleteDictByDictIdApi } from '~/api/dict'
import {status} from "~/utils/sys.js";

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
//父组件函数
const emits = defineEmits(['ok', 'cancel'])
//字典空数据
const emptyDictData = {rowKey: proxy.randomCode(18), dictId: null, dictKey: null, dictValue: null, dictOrder: 0, dictClass: null, status: status.status_1.key}
//表单ref
const formRef = ref(null)
//表单
const form = reactive({
    //字典名称
    dictName: null,
    //字典类型
    dictType: null,
    //字典描述
    dictDesc: null,
    //字典数据
    dictDataList: [JSON.parse(JSON.stringify(emptyDictData))]
})
//表单验证规则
const rules = {
    dictName: [{required: true, message: '字典名称不能为空', trigger: 'submit'}],
    dictType: [{required: true, message: '字典类型不能为空', trigger: 'submit'}]
}
//添加字典数据按钮 -> 点击事件
const addDictDataBtnClick = () => {
    //添加时，dictId为null，新增rowKey用于标识新增的数据
    let dictData = JSON.parse(JSON.stringify(emptyDictData))
    dictData['rowKey'] = proxy.randomCode(18)
    form.dictDataList.push(dictData)
}
//删除字典数据按钮 -> 点击事件
const delDictDataBtnClick = (dict) => {
    if (dict.dictId) {
        //有字典ID，已存在的数据，删除数据
        deleteDictByDictIdApi(dict.dictId).then(() => {
            proxy.$msg.success(proxy.operationType.delete.success)
            //从数组中，根据dictId剔除该条数据
            for (let i = 0;i < form.dictDataList.length;i++) {
                if (dict.dictId === form.dictDataList[i].dictId) {
                    form.dictDataList.splice(i, 1)
                    break
                }
            }
        })
    } else if (dict.rowKey) {
        //无字典ID，有rowKey，从数组中，根据rowKey剔除该条数据
        for (let i = 0;i < form.dictDataList.length;i++) {
            if (dict.rowKey === form.dictDataList[i].rowKey) {
                form.dictDataList.splice(i, 1)
                break
            }
        }
        proxy.$msg.success(proxy.operationType.delete.success)
    }
}
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
//确定 -> 点击
const okBtnClick = () => {
    //添加
    if (props.params.operationType === proxy.operationType.add.type) {
        spinLoading.value = true
        addDictApi(form).then(() => {
            proxy.$msg.success(proxy.operationType.add.success)
            emits('ok')
        }).finally(() => {
            spinLoading.value = false
        })
    }
    //修改
    if (props.params.operationType === proxy.operationType.update.type) {
        spinLoading.value = true
        updateDictApi(form).then(() => {
            proxy.$msg.success(proxy.operationType.update.success)
            emits('ok')
        }).finally(() => {
            spinLoading.value = false
        })
    }
}
//取消 -> 点击
const cancelBtnClick = () => {
    emits('cancel')
}
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //字典类型
    if (props.params.dictType) {
        loadDictInfo(props.params.dictType)
    }
}, { deep: true, immediate: true })
</script>

<style scoped></style>
