<template>
    <a-card :body-style="{height: 'calc(100vh - 125px)'}">
        <a-row class="w-full h-full flex flex-col">
            <a-row class="w-full mb-6">
                <a-space size="large" direction="vertical" :style="{width: '400px'}">
                    <a-row justify="space-between" align="center" :style="{width: '100%'}">
                        <a-space>
                            <icon-settings :style="{fontSize: '20px'}" />
                            <span :style="{fontSize: '16px', fontWeight: 'bold'}">功能配置</span>
                        </a-space>
                    </a-row>
                    <a-divider :style="{margin: '8px 0'}" />
                    <a-row v-for="(item, index) in funcConfigList" :key="index" justify="space-between" align="center" :style="{width: '100%', padding: '8px 0'}">
                        <a-space direction="vertical" :size="0">
                            <span>{{ item.configName }}</span>
                            <a-typography-text type="secondary" :style="{fontSize: '12px'}">{{ item.description }}</a-typography-text>
                        </a-space>
                        <a-switch
                            v-model="item.switchValue"
                            @change="(val) => onSwitchChange(item, val)"
                            :loading="item.loading"
                        />
                    </a-row>
                </a-space>
            </a-row>
        </a-row>
    </a-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Message } from '@arco-design/web-vue'
import { getFuncConfigListApi, updateFuncConfigApi } from '~/api/funcConfig'

const funcConfigList = ref([])

//只展示功能开关（feature.*的配置），不展示菜单参数
const loadData = async () => {
    const res = await getFuncConfigListApi()
    funcConfigList.value = res
        .filter(item => item.configKey.startsWith('feature.'))
        .map(item => ({
            ...item,
            switchValue: item.configValue === 'true',
            loading: false
        }))
}

const onSwitchChange = async (item, val) => {
    item.loading = true
    try {
        await updateFuncConfigApi({
            configId: item.configId,
            configKey: item.configKey,
            configValue: val ? 'true' : 'false'
        })
        Message.success('配置已更新')
    } catch (e) {
        //恢复开关状态
        item.switchValue = !val
    } finally {
        item.loading = false
    }
}

onMounted(() => {
    loadData()
})
</script>
