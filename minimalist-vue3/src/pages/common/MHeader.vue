<template>
    <div class="w-[100%] flex justify-between items-center">
        <div class="flex items-center ml-4 cursor-pointer" @click="logoClick()">
            <img class="w-[35px] h-[35px]" src="../../assets/logo.png" />
            <span class="logo-text">极简多租户管理系统</span>
        </div>
        <div class="flex items-center">
            <a-space>
                <!-- 全屏 -->
                <a-button shape="circle" size="small" @click="toggle">
                    <template #icon>
                        <icon-fullscreen-exit v-if="isFullscreen" />
                        <icon-fullscreen v-else />
                    </template>
                </a-button>
                <!-- 切换主题 -->
                <a-button shape="circle" size="small" @click="themeChange">
                    <template #icon>
                        <icon-sun-fill v-if="theme" />
                        <icon-moon-fill v-else />
                    </template>
                </a-button>
                <!-- gitee -->
                <a-button shape="circle" size="small" @click="skipLink('https://gitee.com/marlife/minimalist-saas')">
                    <template #icon>
                        <svg t="1690378638998" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1580" width="16" height="16"><path d="M512 1024C229.222 1024 0 794.778 0 512S229.222 0 512 0s512 229.222 512 512-229.222 512-512 512z m259.149-568.883h-290.74a25.293 25.293 0 0 0-25.292 25.293l-0.026 63.206c0 13.952 11.315 25.293 25.267 25.293h177.024c13.978 0 25.293 11.315 25.293 25.267v12.646a75.853 75.853 0 0 1-75.853 75.853h-240.23a25.293 25.293 0 0 1-25.267-25.293V417.203a75.853 75.853 0 0 1 75.827-75.853h353.946a25.293 25.293 0 0 0 25.267-25.292l0.077-63.207a25.293 25.293 0 0 0-25.268-25.293H417.152a189.62 189.62 0 0 0-189.62 189.645V771.15c0 13.977 11.316 25.293 25.294 25.293h372.94a170.65 170.65 0 0 0 170.65-170.65V480.384a25.293 25.293 0 0 0-25.293-25.267z" fill="#C71D23" p-id="1581"></path></svg>                    </template>
                </a-button>
                <!-- github -->
                <a-button shape="circle" size="small" @click="skipLink('https://github.com/lmq2582609/minimalist-saas')">
                    <template #icon>
                        <icon-github />
                    </template>
                </a-button>
                <!-- 用户头像 下拉菜单 -->
                <div class="mr-3 cursor-pointer">
                    <a-dropdown @select="dropdownSelect">
                        <a-avatar :size="40" class="bg-blue-300">
                            <template v-if="sysStore.user.userAvatar">
                                <img :src="sysStore.user.userAvatar" alt="头像" />
                            </template>
                            <template v-else>
                                <img src="../../assets/default-avatar.jpg" alt="头像" />
                            </template>
                        </a-avatar>
                        <template #content>
                            <a-doption value="userSetting">
                                <template #icon>
                                    <icon-settings />
                                </template>
                                <template #default>用户设置</template>
                            </a-doption>
                            <a-divider class="mt-0 mb-0" />
                            <a-doption value="logout">
                                <template #icon>
                                    <icon-poweroff />
                                </template>
                                <template #default>退出系统</template>
                            </a-doption>
                        </template>
                    </a-dropdown>
                </div>
            </a-space>
        </div>
        <!-- 退出确认模态框 -->
        <confirm-modal :visible="confirmModalVisible" status="warning" title="提示" content="是否退出系统？"
                       @ok="logout()" @cancel="confirmModalVisible = false"/>
    </div>
</template>
<script setup>
import { ref, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { useFullscreen } from '@vueuse/core'
import { logoutApi } from "~/api/user.js";
import { useSysStore } from '~/store/module/sys-store.js'

//路由
const router = useRouter()
//缓存
const sysStore = useSysStore()
//全局实例
const {proxy} = getCurrentInstance()

//主题2种模式，true白天，false黑夜
const theme = ref(true)
//切换主题
const themeChange = () => {
    theme.value = !theme.value
    if (theme.value) {
        // 恢复亮色主题
        document.body.removeAttribute('arco-theme');
    } else {
        // 设置为暗黑主题
        document.body.setAttribute('arco-theme', 'dark')
    }
}
//vue use isFullscreen: 是否全屏，toggle: 切换全屏
const { isFullscreen, toggle } = useFullscreen()
//退出登录确认模态框选项
const confirmModalVisible = ref(false)
//下拉菜单
const dropdownSelect = (val) => {
    //用户设置
    if (val === 'userSetting') {
        //跳转到用户设置页
        router.push('/user/setting')
    }
    //退出系统
    if (val === 'logout') {
        confirmModalVisible.value = true
    }
}
//退出
const logout = () => {
    logoutApi().then(res => {
        sysStore.userLogoutHandler()
        confirmModalVisible.value = false
        //跳转到登录页
        router.push('/login')
    })
}
//点击logo -> 跳转到首页
const logoClick = () => {
    router.push('/')
}
//超链接跳转
const skipLink = (url) => {
    window.open(url, '_blank')
}
</script>
<style scoped>
.logo-text {
    font-size: 1.2rem;
    color: var(--color-text-1);
    margin-left: 0.5rem;
}
</style>