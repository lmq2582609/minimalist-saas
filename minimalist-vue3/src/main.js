import { createApp } from 'vue'
import pinia from '~/store'
import '~/assets/globalStyle.css'
import ArcoVue from '@arco-design/web-vue';
import '@arco-design/web-vue/dist/arco.css';
import ArcoVueIcon from '@arco-design/web-vue/es/icon';
import App from './App.vue'
import Router from './router'
import Msg from '~/utils/msg'
import { randomCode, operationType, yesNo } from '~/utils/sys'
import { LoadDicts, DICT } from '~/utils/dict'
import DictValue from '~/components/dictValue/index.vue'
import Pagination from '~/components/pagination/index.vue'
import ConfirmModal from '~/components/confirmModal/index.vue'

const app = createApp(App)


//全局挂载公共工具类
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
    LoadDicts: LoadDicts
}

//状态管理
app.use(pinia)

//路由
app.use(Router)

//arco
app.use(ArcoVue)

//arco图标
app.use(ArcoVueIcon)

//字典转换组件
app.component('dict-convert', DictValue)
//分页组件
app.component('pagination', Pagination)
//确认模态框组件
app.component('confirm-modal', ConfirmModal)

//windicss
import 'virtual:windi.css'

app.mount('#app')