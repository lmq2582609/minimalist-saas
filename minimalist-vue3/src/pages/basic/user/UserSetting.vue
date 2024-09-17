<template>
    <div>
        <a-space direction="vertical" fill>
            <a-card>
                <div class="flex items-center">
                    <a-avatar :size="100" @click="avatarBtnClick()" class="bg-blue-300">
                        <template #trigger-icon>
                            <IconCamera />
                        </template>
                        <template v-if="sysStore.user?.userAvatar">
                            <img :src="sysStore.user.userAvatar" alt="头像" />
                        </template>
                        <template v-else>
                            <img src="../../../assets/default-avatar.jpg" alt="头像" />
                        </template>
                    </a-avatar>
                    <a-descriptions :column="2" bordered class="ml-10">
                        <a-descriptions-item label="用户账号">{{ sysStore.user.username }}</a-descriptions-item>
                        <a-descriptions-item label="用户昵称">{{ userInfoForm.nickname }}</a-descriptions-item>
                        <a-descriptions-item label="用户姓名">{{ userInfoForm.userRealName }}</a-descriptions-item>
                        <a-descriptions-item label="用户手机">{{ userInfoForm.phone }}</a-descriptions-item>
                        <a-descriptions-item label="用户邮箱">{{ userInfoForm.email }}</a-descriptions-item>
                        <a-descriptions-item label="用户性别">
                            <dict-convert :dict-data="dicts[proxy.DICT.userSex]" :dict-key="userInfoForm.userSex" />
                        </a-descriptions-item>
                        <a-descriptions-item label="用户岗位">
                            <template v-for="(post, index) in sysStore.user.postList" :key="index">
                                {{ post.postName }}
                                {{index < sysStore.user.postList.length - 1 ? '   ' : ''}}
                            </template>
                        </a-descriptions-item>
                        <a-descriptions-item label="所属部门">
                            <template v-for="(dept, index) in sysStore.user.deptList" :key="index">
                                {{ dept.deptName }}
                                {{index < sysStore.user.deptList.length - 1 ? '   ' : ''}}
                            </template>
                        </a-descriptions-item>
                    </a-descriptions>
                </div>
            </a-card>
            <a-card>
                <a-spin class="w-[100%]" :size="35" :loading="spinLoading" tip="正在处理, 请稍候...">
                    <a-tabs type="rounded" default-active-key="user-info">
                        <a-tab-pane key="user-info" title="基本信息">
                            <a-form :model="userInfoForm" ref="userInfoFormRef" :rules="userInfoFormRules" auto-label-width>
                                <a-form-item class="w-[350px]" field="nickname" label="用户昵称" required>
                                    <a-input v-model="userInfoForm.nickname" placeholder="用户昵称" />
                                </a-form-item>
                                <a-form-item class="w-[350px]" field="userRealName" label="用户真实姓名" required>
                                    <a-input v-model="userInfoForm.userRealName" placeholder="用户真实姓名" />
                                </a-form-item>
                                <a-form-item class="w-[350px]" field="phone" label="手机号" required>
                                    <a-input v-model="userInfoForm.phone" placeholder="手机号" />
                                </a-form-item>
                                <a-form-item class="w-[350px]" field="email" label="邮箱">
                                    <a-input v-model="userInfoForm.email" placeholder="邮箱" />
                                </a-form-item>
                                <a-form-item class="w-[350px]" field="userSex" label="用户性别" required>
                                    <a-select v-model="userInfoForm.userSex" placeholder="用户性别" allow-clear allow-search>
                                        <a-option v-for="(d, index) in dicts[proxy.DICT.userSex]" :key="index" :value="d.dictKey" :label="d.dictValue" />
                                    </a-select>
                                </a-form-item>
                            </a-form>

                            <!-- 分割线 -->
                            <a-divider class="mt-0" />

                            <div class="w-[300px] flex justify-end">
                                <a-space>
                                    <a-button @click="closePage()">关闭</a-button>
                                    <a-button type="primary" @click="updateUserInfoOkBtnClick()">确定</a-button>
                                </a-space>
                            </div>
                        </a-tab-pane>
                        <a-tab-pane key="re-password" title="修改密码">
                            <a-form :model="rePasswordForm" ref="rePasswordFormRef" :rules="rePasswordFormRules" auto-label-width>
                                <a-form-item class="w-[350px]" field="oldPassword" label="旧密码" required>
                                    <a-input-password v-model="rePasswordForm.oldPassword" placeholder="旧密码" />
                                </a-form-item>
                                <a-form-item class="w-[350px]" field="newPassword" label="新密码" required>
                                    <a-input-password v-model="rePasswordForm.newPassword" placeholder="新密码" />
                                </a-form-item>
                                <a-form-item class="w-[350px]" field="reNewPassword" label="重复新密码" required>
                                    <a-input-password v-model="rePasswordForm.reNewPassword" placeholder="重复新密码" />
                                </a-form-item>
                            </a-form>

                            <!-- 分割线 -->
                            <a-divider class="mt-0" />

                            <div class="w-[300px] flex justify-end">
                                <a-space>
                                    <a-button @click="closePage()">关闭</a-button>
                                    <a-button type="primary" @click="rePasswordOkBtnClick()">确定</a-button>
                                </a-space>
                            </div>
                        </a-tab-pane>
                    </a-tabs>
                </a-spin>
            </a-card>
        </a-space>

        <!-- 裁剪头像模态框 -->
        <a-modal v-model:visible="avatarModal.visible" width="400px" :esc-to-close="false" :mask-closable="false" draggable :footer="false">
            <template #title>修改头像</template>
            <div class="flex items-center">
                <div class="w-[100%] h-[100%] flex flex-col items-center">
                    <a-spin :size="35" :loading="userAvatarLoading" tip="正在处理, 请稍候...">
                        <vue-cropper style="width: 300px; height: 300px;" ref="cropperRef"
                            :img="avatarModal.img"
                            :outputSize="avatarModal.outputSize"
                            :outputType="avatarModal.outputType"
                            :info="avatarModal.info"
                            :canScale="avatarModal.canScale"
                            :autoCrop="avatarModal.autoCrop"
                            :autoCropWidth="avatarModal.autoCropWidth"
                            :autoCropHeight="avatarModal.autoCropHeight"
                            :fixedBox="avatarModal.fixedBox"
                            :fixed="avatarModal.fixed"
                            :fixedNumber="avatarModal.fixedNumber"
                            :canMove="avatarModal.canMove"
                            :canMoveBox="avatarModal.canMoveBox"
                            :original="avatarModal.original"
                            :centerBox="avatarModal.centerBox"
                            :infoTrue="avatarModal.infoTrue"
                            :full="avatarModal.full"
                            :enlarge="avatarModal.enlarge"
                            :mode="avatarModal.mode"
                            >
                        </vue-cropper>
                    </a-spin>
                    <p class="mt-3">提示: 头像文件大小需小于100kb。</p>
                </div>
            </div>

            <!-- 分割线 -->
            <a-divider />

            <div class="flex justify-between">
                <a-upload v-model="avatarFileList" action="" :show-file-list="false" :auto-upload="false" @change="customUpload" accept=".jpg, .jpeg, .png, .jfif, .bmp, .webp" />
                <a-space>
                    <a-button @click="avatarModalClose()">关闭</a-button>
                    <a-button type="primary" @click="avatarOkBtnClick()">确定</a-button>
                </a-space>
            </div>
        </a-modal>
    </div>
</template>

<script setup>
import {ref, reactive, getCurrentInstance} from 'vue'
import { useSysStore } from '~/store/module/sys-store.js'
import { useRouter } from 'vue-router'
import {resetPasswordApi, updateUserInfoApi, updateUserAvatarApi} from "~/api/user.js";
import 'vue-cropper/dist/index.css'
import { VueCropper }  from "vue-cropper";


//路由
const router = useRouter()
//全局实例
const {proxy} = getCurrentInstance()
//加载字典
const dicts = proxy.LoadDicts([proxy.DICT.userSex])
//缓存
const sysStore = useSysStore()
//裁剪头像组件ref
const cropperRef = ref(null)
//头像文件
const avatarFileList = ref([])
//裁剪头像模态框
const avatarModal = reactive({
    //是否显示
    visible: false,
    //裁剪图片的地址 url 地址, base64, blob
    img: '',
    //裁剪生成图片的质量
    outputSize: 0.6,
    //裁剪生成图片的格式 jpeg, png, webp
    outputType: 'jpeg',
    //裁剪框的大小信息
    info: false,
    //图片是否允许滚轮缩放
    canScale: false,
    //是否默认生成截图框
    autoCrop: true,
    //默认生成截图框宽度
    autoCropWidth: 300,
    //默认生成截图框高度
    autoCropHeight: 300,
    //固定截图框大小 不允许改变
    fixedBox: false,
    //是否开启截图框宽高固定比例，这个如果设置为true，截图框会是固定比例缩放的，如果设置为false，则截图框的宽高比例就不固定了
    fixed: true,
    //上传图片是否可以移动
    canMove: false,
    //截图框能否拖动
    canMoveBox: true,
    //上传图片按照原始比例渲染
    original: false,
    //截图框是否被限制在图片里面
    centerBox: true,
    //true展示真实输出图片宽高 false展示看到的截图框宽高
    infoTrue: true,
    //是否输出原图比例的截图
    full: true,
    //图片根据截图框输出比例倍数
    enlarge: '1',
    //图片默认渲染方式 contain , cover, 100px, 100% auto
    mode: 'contain'
})
//加载中...
const spinLoading = ref(false)
//修改密码表单ref
const rePasswordFormRef = ref(null)
//修改密码表单
const rePasswordForm = reactive({
    oldPassword: null,
    newPassword: null,
    reNewPassword: null,
})
//修改密码表单校验规则
const rePasswordFormRules = {
    oldPassword: [{required: true, message: '旧密码输入不能为空', trigger: 'submit'}],
    newPassword: [{required: true, message: '新密码输入不能为空', trigger: 'submit'}],
    reNewPassword: [{required: true, message: '重复输入新密码不能为空', trigger: 'submit'}]
}
//修改基本信息表单ref
const userInfoFormRef = ref(null)
//修改基本信息表单
const userInfoForm = reactive({
    //用户昵称
    nickname: sysStore.user.nickname,
    //用户真实姓名
    userRealName: sysStore.user.userRealName,
    //手机号
    phone: sysStore.user.phone,
    //邮箱
    email: sysStore.user.email,
    //用户性别
    userSex: sysStore.user.userSex
})
//修改基本信息表单校验规则
const userInfoFormRules = {
    nickname: [{required: true, message: '用户昵称不能为空', trigger: 'submit'}],
    userRealName: [{required: true, message: '用户姓名不能为空', trigger: 'submit'}],
    phone: [{required: true, message: '手机号不能为空', trigger: 'submit'}],
    userSex: [{required: true, message: '性别不能为空', trigger: 'submit'}]
}
//关闭页面
const closePage = () => {
    //跳转页面到后台首页
    router.push('/')
}
//点击头像
const avatarBtnClick = () => {
    avatarModal.visible = true
    if (sysStore.user.userAvatar) {
        avatarModal.img = sysStore.user.userAvatar
    }
}
//关闭裁剪头像
const avatarModalClose = () => {
    avatarModal.visible = false
}
//头像自定义上传处理
const customUpload = (fileList, fileItem) => {
    //校验图片大小
    if (fileItem.file.size > 100 * 1024) {
        proxy.$msg.error('头像文件需小于100kb')
    } else {
        //本地文件展示让用户裁剪
        avatarModal.img = fileItem.url
    }
}
//修改密码确定
const rePasswordOkBtnClick = () => {
    //表单验证
    rePasswordFormRef.value.validate((valid) => {
        if (valid) {return false}
        //校验两次输入的密码是否一致
        if (rePasswordForm.newPassword !== rePasswordForm.reNewPassword) {
            proxy.$msg.error('新密码两次输入的不一致')
            return false
        }
        //加载中
        spinLoading.value = true
        //登录请求
        resetPasswordApi(rePasswordForm).then(res => {
            //消息提示
            proxy.$msg.success(proxy.operationType.update.success + '，下次登录请使用新密码登录')
            //跳转到登录页
            router.push('/login')
        }).finally(() => {
            //加载完毕
            spinLoading.value = false
        })
    })
}
//修改用户基本信息确定
const updateUserInfoOkBtnClick = () => {
    //表单验证
    userInfoFormRef.value.validate((valid) => {
        if (valid) {return false}
        //加载中
        spinLoading.value = true
        //登录请求
        updateUserInfoApi(userInfoForm).then(res => {
            //消息提示
            proxy.$msg.success(proxy.operationType.update.success)
        }).finally(() => {
            //加载完毕
            spinLoading.value = false
        })
    })
}
//上传头像加载中...
const userAvatarLoading = ref(false)
//裁剪头像确定
const avatarOkBtnClick = () => {
    userAvatarLoading.value = true
    //获取截图base64数据
    cropperRef.value.getCropData(data => {
        updateUserAvatarApi(data).then(res => {
            //消息提示
            proxy.$msg.success(proxy.operationType.update.success)
            //关闭模态框
            avatarModal.visible = false
            //头像变更
            sysStore.updateUserAvatar(data)
        }).finally(() => {
            userAvatarLoading.value = false
        })
    })
}
</script>
<style scoped>
:deep(.arco-avatar-trigger-icon-button) {
    width: 35px;
    height: 35px;
    line-height: 35px;
}
:deep(.arco-avatar-trigger-icon-button .arco-icon-camera) {
    margin-top: 8px;
    color: rgb(var(--arcoblue-6));
    font-size: 18px;
}
</style>
