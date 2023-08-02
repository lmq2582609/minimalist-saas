<!-- 指定组件名称，递归使用 -->
<script>export default { name: "menuTree"}</script>
<template>
    <div>
        <template v-for="(item, index) in menuTreeData" :key="index">
            <!-- 如果有子菜单，则是折叠的sub-menu -->
            <a-sub-menu :key="item.permId" v-if="item.children && item.children.length > 0">
                <!-- 图标 -->
                <template #icon v-if="item.permIcon">
                    <functional-icons :icon="item.permIcon" size="30"></functional-icons>
                </template>
                <!-- 折叠菜单名称 -->
                <template #title>
                    {{item.permName}}
                </template>
                <!-- 组件递归 -->
                <menu-tree :menuTreeData="item.children"></menu-tree>
            </a-sub-menu>
            <!-- 菜单 :key 给定路由，需要跳转 -->
            <template v-else>
                <!-- visible=true 菜单可见 -->
                <a-menu-item :key="item.permPath" v-if="item.visible">
                    <!-- 图标 -->
                    <template #icon v-if="item.permIcon">
                        <functional-icons :icon="item.permIcon" size="30"></functional-icons>
                    </template>
                    {{ item.permName }}
                </a-menu-item>
            </template>
        </template>
    </div>
</template>

<script setup>
import FunctionalIcons from "~/components/iconSelect/FunctionalIcons.vue";

defineProps({
    menuTreeData: Array
})
</script>