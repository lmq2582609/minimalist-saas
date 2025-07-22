<template>
    <a-layout class="w-[100%] h-[100%]">
        <!-- 头部header -->
        <a-layout-header class="m-header">
            <m-header @tenant-change="tenantChange" />
        </a-layout-header>
        <a-layout>
            <!-- 左侧菜单 -->
            <a-layout-sider
                class="m-sider"
                breakpoint="lg"
                collapsible
                hide-trigger
                :width="siderWidth"
                :collapsed="siderCollapsed">
                <div class="sider-div">
                    <m-sider />
                </div>
            </a-layout-sider>
            <a-layout-content class="m-content" :style="{left: siderWidth + 'px'}">
                <!-- tab页 -->
                <PageTabList ref="pageTabListRef" />
                <!-- 主体内容，通过router动态变换 -->
                <router-view v-slot="{ Component }">
                    <!-- transition动画效果，使用transition 每个页面必须只有1个根节点 -->
                    <transition name="fade">
                        <keep-alive :include="sysStore.includePage">
                            <component :is="Component"></component>
                        </keep-alive>
                    </transition>
                </router-view>
            </a-layout-content>
        </a-layout>
    </a-layout>
</template>

<script setup>
import MSider from "../components/MSider.vue";
import MHeader from "../components/MHeader.vue";
import PageTabList from "../components/PageTabList.vue"
import { storeToRefs } from 'pinia'
import { useSysStore } from '~/store/module/sys-store.js'
import {getCurrentInstance, inject, ref} from "vue";

//全局实例
const {proxy} = getCurrentInstance()
//缓存
const sysStore = useSysStore()
//响应式数据：siderCollapsed: sider是否展开，siderWidth: sider宽度
const { siderCollapsed, siderWidth } = storeToRefs(sysStore)

//tabListRef
const pageTabListRef = ref()
//App.vue提供的reload方法
const reload = inject('reload')
//刷新倒计时
const reloadCountDown = ref(5)
//租户切换
const tenantChange = () => {
    //消息提示
    reloadCountDown.value = 5
    proxy.$msg.error(`注意：正在为您切换到该租户的管理员身份，${reloadCountDown.value} 秒后将刷新页面`)
    reloadCountDown.value--
    //关闭所有tab页
    pageTabListRef.value.tabDropdownSelect('clearAll')
    //清除页面缓存
    sysStore.includePage = []

    //倒计时刷新页面
    const timer = setInterval(() => {
        proxy.$msg.error(`注意：正在为您切换到该租户的管理员身份，${reloadCountDown.value} 秒后将刷新页面`)
        reloadCountDown.value--
        //倒计时 = 0，刷新页面
        if (reloadCountDown.value <= 0) {
            clearInterval(timer)
            // 延迟1秒后执行
            setTimeout(() => {
                //重新加载
                //reload()
                //重新刷新页面
                location.reload()
            }, 1000)
        }
    }, 1000)
}
</script>
<style scoped>
.m-header {
    @apply flex items-center w-[100%] h-[50px] fixed top-0 left-0 right-0 z-50 transition-all shadow;
    background-color: var(--color-bg-2);
}
.m-sider {
    @apply fixed top-0 left-0 z-40 top-[50px] transition-all;
    height: calc(100% - 50px);
    overflow-y: hidden;
}
.m-content {
    @apply fixed top-[50px] right-0 bottom-0 overflow-y-auto p-3;
    background-color: var(--color-neutral-1);
    width: calc(100% - 200px);
    height: calc(100% - 50px)
}
/* 隐藏侧边栏的滚动条 */
:deep(.arco-layout-sider-children) {
    overflow-y: hidden;
}
/* 侧边栏div */
.sider-div {
    height: 100%;
    overflow: auto;
    overflow-x: hidden;
}
/* 侧边栏滚动条 */
:deep(.arco-menu ::-webkit-scrollbar) {
    width: 16px;
    height: 4px;
}
:deep(.arco-menu ::-webkit-scrollbar-thumb) {
    border: 4px solid transparent;
    background-clip: padding-box;
    border-radius: 7px;
    background-color: var(--color-text-4);
}
:deep(.arco-menu ::-webkit-scrollbar-thumb:hover) {
    background-color: var(--color-text-3);
}
</style>
