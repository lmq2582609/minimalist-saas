import { createApp } from 'vue'
import '~/assets/globalStyle.css'
import ArcoVue from '@arco-design/web-vue';
import '@arco-design/web-vue/dist/arco.css';
import ArcoVueIcon from '@arco-design/web-vue/es/icon';
import App from './App.vue'
//windicss
import 'virtual:windi.css'



const app = createApp(App)
//全局挂载公共工具类
import Msg from '~/utils/msg'
import { randomCode, operationType, yesNo, hasPerm, hasRole } from '~/utils/sys'
import { LoadDicts, DICT } from '~/utils/dict'
app.config.globalProperties = {
    //消息提示
    $msg: Msg,
    //生成随即编码
    randomCode: randomCode,
    //操作类型
    operationType: operationType,
    //'是/否' 枚举
    yesNo: yesNo,
    //字典枚举
    DICT: DICT,
    //加载字典
    LoadDicts: LoadDicts,
    //权限校验
    hasPerm: hasPerm,
    //角色校验
    hasRole: hasRole
}

//状态管理
import pinia from '~/store'
app.use(pinia)

//路由
import Router from './router'
app.use(Router)

//arco
app.use(ArcoVue)
//arco图标
app.use(ArcoVueIcon)

//字典转换组件
import DictValue from '~/components/dictValue/index.vue'
app.component('dict-convert', DictValue)

//分页组件
import Pagination from '~/components/pagination/index.vue'
app.component('pagination', Pagination)

//自定义权限指令
import permission from "~/directives/permission.js";
app.use(permission)
//自定义角色限指令
import role from "~/directives/role.js";
app.use(role)

app.mount('#app')
