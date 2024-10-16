<template>
    <a-menu accordion
        :collapsed="siderCollapsed"
        @collapse="onCollapse"
        class="w-[100%] h-[100%]"
        :selected-keys="[$route.path]" auto-open-selected
        show-collapse-button :defaultOpenKeys="['0']" :defaultSelectedKeys="['/']" @menu-item-click="menuClick">
        <!-- 菜单处理 - 从store中获取菜单 -->
        <template v-if="user && user.menus">
            <!-- visible=true 菜单可见 -->
            <a-menu-item key="/">
                <template #icon>
                    <icon-bytedance-color/>
                </template>
                控制台
            </a-menu-item>
            <menu-tree :menuTreeData="user.menus" />
        </template>
    </a-menu>
</template>
<script setup>
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useSysStore } from '~/store/module/sys-store.js'
import MenuTree from "~/components/menuTree/index.vue";

//路由
const router = useRouter()
//菜单点击事件
const menuClick = (url) => {
    if (url.includes('http')) {
        //外部链接
        window.open(url);
    } else {
        //跳转页面
        router.push(url)
    }
}

//缓存
const sysStore = useSysStore()
//响应式数据：siderCollapsed: sider是否展开，siderWidth: sider宽度
const { siderCollapsed, siderWidth, user } = storeToRefs(sysStore)
//左侧slider 展开/缩起
const onCollapse = (val, type) => {
    siderCollapsed.value = !siderCollapsed.value
    siderWidth.value = siderCollapsed.value ? sysStore.siderMinWidth : sysStore.siderMaxWidth
}

</script>
<style scoped>
</style>
