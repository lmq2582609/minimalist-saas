<template>
    <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
        <a-form :model="form" ref="formRef" layout="vertical" :rules="rules" auto-label-width>
            <div class="flex justify-between" style="flex-wrap: wrap;">
                <a-form-item class="w-[100%]" field="permType" label="权限类型" required tooltip="菜单表示系统中左侧展示的菜单；按钮表示访问后端接口的权限。">
                    <a-radio-group v-model="form.permType" type="button">
                        <a-radio v-for="(item, index) in dicts[proxy.DICT.permType]" :key="index" :value="item.dictKey">{{ item.dictValue }}</a-radio>
                    </a-radio-group>
                </a-form-item>
                <a-form-item class="w-[49%]" field="parentPermId" :label="'上级' + formName" tooltip="可为空，为空表示顶级">
                    <a-tree-select v-model="form.parentPermId" :data="permTree" :placeholder="'上级' + formName" allow-clear
                                   :fieldNames="{key: 'permId', title: 'permName', children: 'children'}" />
                </a-form-item>
                <a-form-item class="w-[49%]" v-if="form.permType !== permType.B.key" field="permIcon" label="菜单图标">
                    <icon-select v-model="form.permIcon" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="permName" :label="formName + '名称'" required>
                    <a-input v-model="form.permName" :placeholder="formName + '名称'" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="permCode" label="权限编码" tooltip="Controller接口中定义的权限字符，如：`system:user:list`)">
                    <a-input v-model="form.permCode" placeholder="权限编码" />
                </a-form-item>
                <a-form-item class="w-[49%]" field="permSort" label="排序值" required tooltip="展示顺序，按照数值升序排序">
                    <a-input-number :min="0" v-model="form.permSort" placeholder="排序值" />
                </a-form-item>
                <a-form-item class="w-[49%]" v-if="form.permType !== permType.B.key" field="permPath" label="路由地址" tooltip="访问的url，会出现在浏览器的地址栏中，如：`/basic/user/mgt`；若访问外部链接需以`http或https`开头，如：`https://www.baidu.com`">
                    <a-input v-model="form.permPath" placeholder="路由地址" />
                </a-form-item>
                <a-form-item class="w-[49%]" v-if="form.permType !== permType.B.key" field="visible" label="是否可见" tooltip="选择否(隐藏)，将不会出现在侧边栏，但仍然可以访问">
                    <a-radio-group v-model="form.visible" type="button">
                        <a-radio v-for="(item, index) in dicts[proxy.DICT.yesNo]" :key="index" :value="item.dictKey">{{ item.dictValue }}</a-radio>
                    </a-radio-group>
                </a-form-item>
                <a-form-item class="w-[49%]" field="status" :label="formName + '状态'" required
                    tooltip="选择禁用，将不会出现在侧边栏，也不能被访问" v-if="props.params.operationType === proxy.operationType.update.type">
                    <a-select v-model="form.status" :placeholder="formName + '状态'" allow-clear>
                        <a-option v-for="(d, index) in dicts[proxy.DICT.commonNumberStatus]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                    </a-select>
                </a-form-item>
                <a-form-item class="w-[49%]" v-if="form.permType === permType.M.key" field="component" label="组件路径" tooltip="vue文件的路径，文件需要放到pages目录下，如：`/basic/user/UserMgt.vue`">
                    <a-input v-model="form.component" placeholder="组件路径" />
                </a-form-item>
                <a-form-item class="w-[100%]" field="remark" label="备注">
                    <a-textarea v-model="form.remark" placeholder="备注" />
                </a-form-item>
            </div>
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
import {addPermApi, permType, updatePermByPermIdApi, getPermListApi, getPermByPermIdApi} from '~/api/perm'
import {status} from "~/utils/sys.js";
import IconSelect from "~/components/iconSelect/index.vue";

//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.commonNumberStatus, proxy.DICT.permType, proxy.DICT.yesNo])
//接收父组件参数
const props = defineProps({
    params: {
        type: Object,
        default: () => {}
    }
})
//加载中...
const spinLoading = ref(false)
//表单ref
const formRef = ref(null)
//表单
const form = reactive({
    //权限ID
    permId: null,
    //权限编码
    permCode: null,
    //权限名称
    permName: null,
    //上级权限ID
    parentPermId: "0",
    //排序值
    permSort: 0,
    //路由地址
    permPath: null,
    //权限图标
    permIcon: null,
    //权限类型
    permType: permType.M.key,
    //组件路径
    component: null,
    //是否可见
    visible: proxy.yesNo.yes.key,
    //备注
    remark: null,
    //权限状态
    status: status.status_1.key,
})
//表单验证规则
const rules = {
    parentPermId: [{required: true, message: '上级权限不能为空', trigger: 'submit'}],
    permType: [{required: true, message: '权限类型不能为空', trigger: 'submit'}],
    permName: [{required: true, message: '权限名称不能为空', trigger: 'submit'}],
    permSort: [{required: true, message: '排序值不能为空', trigger: 'submit'}],
}
//表单项名称
const formName = ref('')
//权限树数据
const permTree = ref([])
//获取权限数据列表
const getPermTree = () => {
    getPermListApi({}).then(res => {
        //权限树数据处理，将数据挂载到"全部"下边
        permTree.value = [{permId: '0', permName: '全部', children: res}]
    })
}
//加载权限详细信息
const loadPermInfo = (permId) => {
    spinLoading.value = true
    getPermByPermIdApi(permId).then(res => {
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
//父组件函数
const emits = defineEmits(['ok', 'cancel'])
//确定 -> 点击
const okBtnClick = () => {
    //表单验证
    formRef.value.validate((valid) => {
        if (valid) {return false}
        //添加
        if (props.params.operationType === proxy.operationType.add.type) {
            spinLoading.value = true
            addPermApi(form).then(() => {
                proxy.$msg.success(proxy.operationType.add.success)
                emits('ok')
            }).finally(() => {
                spinLoading.value = false
            })
        }
        //修改
        if (props.params.operationType === proxy.operationType.update.type) {
            spinLoading.value = true
            updatePermByPermIdApi(form).then(() => {
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
//监听参数变化
watch(() => form.permType, (newVal, oldVal) => {
    formName.value = form.permType === permType.M.key ? permType.M.value : '权限'
}, { deep: true, immediate: true })
//监听参数变化
watch(() => props.params, (newVal, oldVal) => {
    //如果有permId参数，则是 "修改"
    if (props.params.permId) {
        loadPermInfo(props.params.permId)
    }
    //如果有parentPermId参数，则是 表格行 "添加"
    if (props.params.parentPermId) {
        //给上级权限ID赋值
        form.parentPermId = props.params.parentPermId
    }
    //加载权限树
    getPermTree()
}, { deep: true, immediate: true })
</script>
<style scoped></style>
