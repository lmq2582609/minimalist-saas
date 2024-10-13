<template>
    <a-row class="min-h-screen bg-indigo-400" align="stretch">
        <a-col :lg="16" :md="12" :sm="24" :xs="24" class="flex items-center justify-center">
            <div>
                <img class="login-pic-animation" style="width: 35em;" src="../assets/login-pic.png" alt="">
            </div>
        </a-col>
        <a-col :lg="8" :md="12" :sm="24" :xs="24" class="flex flex-col items-center justify-center bg-light-50">
            <h2 class="font-bold text-3xl text-gray-800">极简多租户管理系统</h2>
            <div class="flex items-center justify-center my-5 text-gray-400 space-x-2">
                <span class="h-[1px] w-16 bg-gray-300"></span>
                <span>账号密码登录</span>
                <span class="h-[1px] w-16 bg-gray-300"></span>
            </div>
            <a-form ref="loginFormRef" :rules="loginRules" :model="loginForm" class="w-[280px]">
                <a-form-item field="username" feedback hide-label>
                    <a-input v-model="loginForm.username" placeholder="请输入用户名" size="large" allow-clear @keydown.enter="loginSubmitClick()">
                        <template #prefix>
                            <icon-user />
                        </template>
                    </a-input>
                </a-form-item>
                <a-form-item field="password" feedback hide-label>
                    <a-input-password v-model="loginForm.password" placeholder="请输入密码" size="large" allow-clear @keydown.enter="loginSubmitClick()">
                        <template #prefix>
                            <icon-lock />
                        </template>
                    </a-input-password>
                </a-form-item>
                <a-form-item field="captcha" feedback hide-label v-if="isCaptchaEnable">
                    <div class="flex items-center justify-between h-[100%]">
                        <a-input class="w-7/12" v-model="loginForm.captcha" placeholder="请输入验证码" size="large" allow-clear  @keydown.enter="loginSubmitClick()">
                            <template #prefix>
                                <icon-robot />
                            </template>
                        </a-input>
                        <div class="w-4/12">
                            <img :src="imageCaptchaBase64" alt="刷新" @click="getImageCaptcha" class="w-[100%] h-[100%]" />
                        </div>
                    </div>
                </a-form-item>
                <a-form-item hide-label>
                    <a-button type="primary" :loading="loginLoading" class="w-[100%]" @click="loginSubmitClick()">登录</a-button>
                </a-form-item>
            </a-form>
        </a-col>
    </a-row>
</template>
<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { getImageCaptchaApi, loginApi } from '~/api/user'
import { setToken } from '~/utils/cookie'
import { useRouter } from 'vue-router'
//路由
const router = useRouter()
//全局实例
const {proxy} = getCurrentInstance()

/****************** 登录 ******************/
//登录表单
const loginFormRef = ref(null)
const loginForm = reactive({
    username: null,
    password: null,
    captcha: null,
    captchaId: null
})
//登录加载loading
const loginLoading = ref(false)
//登录表单验证规则
const loginRules = {
    username: [{required: true, message: '用户名不能为空', trigger: 'submit'}],
    password: [{required: true, message: '密码不能为空', trigger: 'submit'}],
}
//登录点击事件
const loginSubmitClick = () => {
    //登录表单验证
    loginFormRef.value.validate((valid) => {
        if (valid) {return false}
        //加载中
        loginLoading.value = true
        //登录请求
        loginApi(loginForm).then(res => {
            //登录成功消息提示
            proxy.$msg.success('登陆成功')
            //存储token
            setToken(res.tokenValue)
            //跳转页面到后台首页
            router.push('/')
        }).catch(() => {
            //登录失败，重新刷新验证码
            getImageCaptcha()
        }).finally(() => {
            //加载完毕
            loginLoading.value = false
        })
    })
}

/****************** 验证码 ******************/
//图形验证码base64
const imageCaptchaBase64 = ref('')
//是否需要验证码
const isCaptchaEnable = ref(false)
//获取图形验证码
const getImageCaptcha = () => {
    getImageCaptchaApi().then(res => {
        isCaptchaEnable.value = res.enable
        if (isCaptchaEnable.value) {
            loginRules.captcha = [{required: true, message: '验证码不能为空', trigger: 'submit'}]
            imageCaptchaBase64.value = 'data:image/jpg;base64,' + res.captchaImg
            loginForm.captchaId = res.captchaId
        }
    })
}

//获取图形验证码
getImageCaptcha()
</script>
<style scoped>
@keyframes floating {
    0% { transform: translateY(0); }
    50% { transform: translateY(-20px); }
    100% { transform: translateY(0); }
}
.login-pic-animation {
    animation: floating 3s ease infinite;
}
</style>
