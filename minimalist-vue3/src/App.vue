<template>
    <router-view v-if="isRouterAlive"></router-view>
</template>
<script setup>
import {nextTick, provide, ref} from "vue";
import {useSysStore} from "~/store/module/sys-store.js";
//缓存
const sysStore = useSysStore()
//router-view是否展示
const isRouterAlive = ref(true)
//重新加载
const reload = () => {
    isRouterAlive.value = false
    nextTick(() => {
        isRouterAlive.value = true
        //保留首页缓存
        sysStore.includePage = ['index']
    })
}
//暴露 reload 给子组件使用
provide('reload', reload)
</script>
<style scoped>

</style>
