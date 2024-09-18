<template>
    <a-layout class="w-[100%] h-[100%]">
        <!-- 头部header -->
        <a-layout-header class="m-header">
            <m-header />
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
                <m-sider />
            </a-layout-sider>
            <a-layout-content class="m-content" :style="{left: siderWidth + 'px'}">
                <!-- 主体内容，通过router动态变换 -->
                <router-view v-slot="{ Component }">
                    <!-- transition动画效果，使用transition 每个页面必须只有1个根节点 -->
                    <transition name="fade">
                        <component :is="Component"></component>
                    </transition>
                </router-view>
            </a-layout-content>
        </a-layout>
    </a-layout>
</template>

<script setup>
import MSider from "./common/MSider.vue";
import MHeader from "./common/MHeader.vue";
import { storeToRefs } from 'pinia'
import { useSysStore } from '~/store/module/sys-store.js'

//缓存
const sysStore = useSysStore()
//响应式数据：siderCollapsed: sider是否展开，siderWidth: sider宽度
const { siderCollapsed, siderWidth } = storeToRefs(sysStore)

</script>
<style scoped>
.m-header {
    @apply flex items-center w-[100%] h-[50px] fixed top-0 left-0 right-0 z-50 transition-all shadow;
    background-color: var(--color-bg-2);
}
.m-sider {
    @apply fixed top-0 left-0 z-40 top-[50px] transition-all;
    height: calc(100% - 50px)
}
.m-content {
    @apply fixed top-[50px] right-0 bottom-0 overflow-y-auto p-3;
    background-color: var(--color-neutral-1);
    height: calc(100% - 50px)
}
</style>
