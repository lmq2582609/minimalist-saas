<template>
    <a-breadcrumb>
        <a-breadcrumb-item>
            <a-link href="/" icon>控制台</a-link>
        </a-breadcrumb-item>
        <a-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="index">
            {{ item.permName }}
        </a-breadcrumb-item>
    </a-breadcrumb>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import {ref, watch} from "vue";
import { useSysStore } from '~/store/module/sys-store.js'
//路由
const route = useRoute();
const router = useRouter();
//缓存
const sysStore = useSysStore()
//面包屑导航数据
const breadcrumbList = ref([])
//生成面包屑导航
function getBreadcrumbs(data, path) {
    const breadcrumbs = [];
    const findNode = (node, path) => {
        //找到目标节点，将其加入面包屑数组
        if (node.permPath === path) {
            breadcrumbs.push(node);
            return true;
        }
        if (node.children) {
            //该节点有子集，继续处理
            for (let i = 0; i < node.children.length; i++) {
                //递归
                if (findNode(node.children[i], path)) {
                    //在子节点中找到目标节点，将当前节点加入面包屑数组
                    breadcrumbs.push(node);
                    return true;
                }
            }
        }
        return false;
    };
    for (let i = 0; i < data.length; i++) {
        if (findNode(data[i], path)) {
            break;
        }
    }
    //反转数组并返回
    return breadcrumbs.reverse()
}
//监听路由变化
watch(route, () => {
    breadcrumbList.value = getBreadcrumbs(sysStore.user.menus, route.path)
    //考虑是否是静态路由
    if (breadcrumbList.value.length === 0) {
        const r = router.getRoutes()
        for (let i = 0; i < r.length; i++) {
            if (r[i].path === '/') { continue }
            if (r[i].path === route.path) {
                breadcrumbList.value.push({
                    permPath: r[i].path,
                    permName: r[i].meta.title
                })
                break
            }
        }
    }
},{immediate: true, deep: true});
</script>
<style scoped></style>