<template>
    <div class="fixed top-[50px] right-0 h-[45px] flex items-center px-3 shadow"
         :style="{left: sysStore.siderWidth + 'px'}" style="background-color: var(--color-bg-white);z-index: 100">
        <a-tabs type="card-gutter" v-model:active-key="activeTab" :editable="true" @delete="deleteTab" auto-switch>
            <a-tab-pane v-for="(item, index) of tabList" :key="item.path" :title="item.title" :closable="item.path !== '/'"></a-tab-pane>
        </a-tabs>

        <span class="rounded ml-auto flex items-center justify-center">
            <a-dropdown @select="tabDropdownSelect" position="br">
                <a-button type="primary">
                    <template #icon>
                        <icon-down/>
                    </template>
                </a-button>
                <template #content>
                    <a-doption value="clearOther">关闭其他</a-doption>
                    <a-doption value="clearAll">关闭全部</a-doption>
                </template>
            </a-dropdown>
        </span>
    </div>

    <!-- 高度占位 -->
    <div class="h-[47px]"></div>
</template>
<script setup>
import {onMounted, ref, watch} from "vue";
import {useSysStore} from "~/store/module/sys-store.js";
import {useRoute, useRouter, onBeforeRouteUpdate} from "vue-router";
import { useCookies } from '@vueuse/integrations/useCookies'
import {PAGE_TAB_LIST} from "~/utils/cookie.js";
//cookie
const cookie = useCookies()
//缓存
const sysStore = useSysStore()
//路由
const route = useRoute()
const router = useRouter()
//当前选中的tab
const activeTab = ref(route.fullPath)
//首页控制台tab
const indexTab = {title: '控制台', path: '/'}
//全部tab
const tabList = ref([ indexTab ]);
//添加tab页
const addTab = (tab) => {
    let index = tabList.value.findIndex(t => t.path === tab.path)
    //如果tabList中没有这个tab，添加
    if (index === -1) {
        tabList.value.push(tab)
        //同时添加到cookie，保证页面刷新后tab还存在
        cookie.set(PAGE_TAB_LIST, tabList.value)
    }
    //激活这个tab页
    activeTab.value = tab.path
}
//删除tab页
const deleteTab = (path) => {
    let tabs = tabList.value
    let active = activeTab.value
    //如果关闭的是当前tab
    if (active === path) {
        tabs.forEach((tab, index) => {
            //找到当前tab的下一个tab，如果没有下一个tab，就取上一个tab
            if (tab.path === path) {
                const nextTab = tabs[index+1] || tabs[index-1]
                if (nextTab) {
                    active = nextTab.path
                }
            }
        })
    }
    //修改当前tab
    activeTab.value = active
    //在tabList中删除关闭的tab
    tabList.value = tabList.value.filter(tab => tab.path !== path)
    //同时添加到cookie，保证页面刷新后tab还存在
    cookie.set(PAGE_TAB_LIST, tabList.value)
}
//to   -> 跳转到哪个页面去(路径)
//from -> 从哪个页面跳转过来的(路径)
onBeforeRouteUpdate((to, from) => {
    //跳转页面，添加到tabList
    addTab({
        title: to.meta.title,
        path: to.path
    })
})
//初始化标签页
const initTabList = () => {
    let tabs = cookie.get(PAGE_TAB_LIST)
    if (tabs && tabs.length >= 0) {
        tabList.value = tabs
    }
}
//初始化
onMounted(() => {
    //初始化标签页
    initTabList()
})

//关闭tab下拉菜单钮
const tabDropdownSelect = (key) => {
    //关闭其他
    if (key === 'clearOther') {
        //除首页 和 当前tab，其他全部关闭
        tabList.value = tabList.value.filter(tab => tab.path === '/' || tab.path === activeTab.value)
    }
    //关闭全部
    if (key === 'clearAll') {
        //回首页
        activeTab.value = '/'
        tabList.value = [ indexTab ]
    }
    //同时添加到cookie，保证页面刷新后tab还存在
    cookie.set(PAGE_TAB_LIST, tabList.value)
}


//监听参数变化
watch(() =>  activeTab.value, (newVal, oldVal) => {
    //tab变更，跳转至这个页面
    router.push( activeTab.value)
}, { deep: true, immediate: true })
</script>
<style scoped>
:deep(.arco-tabs-content) {
    display: none;
    border: 0;
}
:deep(.arco-tabs-tab-active) {
    border-bottom-color: transparent;
}
:deep(.arco-tabs-tab-active:hover) {
    border-bottom-color: transparent;
}
</style>
