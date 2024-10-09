import {createRouter, createWebHashHistory} from "vue-router";
import { useCookies } from '@vueuse/integrations/useCookies'
import {getToken} from "~/utils/cookie";
import Container from '~/pages/container.vue'
import Index from '~/pages/index.vue'
import UserSetting from '~/pages/basic/user/UserSetting.vue'
import Msg from '~/utils/msg';
import {getUserInfoApi} from "~/api/user.js";
import { useSysStore } from '~/store/module/sys-store.js'
//页面加载条
import NProgress from 'nprogress'
import'nprogress/nprogress.css'
//登录页
import Login from '~/pages/login.vue'
//404页面
import NotFound from '~/pages/404.vue'
//网页标题
const pageTitle = '极简多租户管理系统'
//公共路由，所有用户共享
const commonRoutes = [
    {
        path: '/',
        name: 'Container',
        component: Container,
        children: [
                {path: '/', component: Index, meta: {title: '控制台'}},
                {path: '/user/setting', component: UserSetting, meta: {title: '用户设置'}},
        ]
    },
    {path: '/login', name: 'Login', component: Login, meta: {title: '登录'}},
    {path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound, meta: {title: '页面不存在'}},
]
//导入所有路由，动态加载
const routeAll = import.meta.glob(`../pages/**/*.vue`);
const router = createRouter({
    history: createWebHashHistory(),
    routes: commonRoutes
})

/**
 * 动态添加路由
 * @param menus 菜单
 * @returns {boolean}
 */
function dynamicAddRoutes(menus) {
    //是否有新的路由
    let hasNewRouter = false
    //获取已有路由
    let allRouter = router.getRoutes()
    const findAndAddRoutesByMenus = (arr) => {
        //遍历菜单
        arr.forEach(e => {
            //检查该路由是否已存在
            let item = allRouter.find(o => o.path === e.permPath)
            //如果未查找到，则该路由不存在，进行添加
            if (!item) {
                //如果指定了组件路径，则添加路由
                if (e.component) {
                    //查找路由
                    let comp = findRouter(e.component)
                    if (comp) {
                        //向外层路由中添加子路由
                        router.addRoute('Container', {
                            path: e.permPath,
                            meta: {title: e.permName},
                            component: comp
                        })
                        hasNewRouter = true
                    }
                }
            }
            //如果包含子菜单，递归执行
            if (e.children && e.children.length > 0) {
                findAndAddRoutesByMenus(e.children)
            }
        })
    }
    findAndAddRoutesByMenus(menus)
    return hasNewRouter
}

/**
 * 查找路由
 * @param component 路由
 * @returns import的路由
 */
function findRouter(component) {
    for (let key in routeAll) {
        if (key.includes(component)) {
            return routeAll[key]
        }
    }
    //未找到路由
    return null
}

//路由白名单 索引0为登录路由
const routerWhiteList = ['/login']
//路由前置守卫
router.beforeEach(async (to, from, next) => {
    //显示加载条
    NProgress.start()
    //获取token
    const token = getToken()
    //如果已经登录 并且 还访问登录页
    if (token && routerWhiteList[0] === to.path) {
        //有原页面继续停留在原页面，否则将跳转至首页
        return next({path: from.path ? from.path : '/'})
    }
    //白名单页面访问放行
    if (routerWhiteList.includes(to.path)) {
        return next()
    }
    //如果未登录，将跳转到登录页进行登录
    if (!token) {
        Msg.error('请登录')
        return next({path: routerWhiteList[0]})
    }
    //设置页面动态标题
    document.title = (to.meta.title ? to.meta.title + ' - ' : '') + pageTitle
    //是否有新添加的路由
    let hasNewRouter = false

    //缓存
    let useStore = useSysStore()
    //是否调用过getUserInfo接口，没有调用过则调用，调用后不再重复调用
    let hasGetUserInfo = useStore.hasGetUserinfo
    //未调用过getUserInfo，就执行获取用户信息、菜单等数据
    if (!hasGetUserInfo) {
        //获取用户信息
        await getUserInfoApi().then(res => {
            //用户信息存储到缓存
            useStore.user = res
            //已调用过getUserInfo
            useStore.hasGetUserinfo = true
            //渲染动态路由
            hasNewRouter = dynamicAddRoutes(useStore.user.menus)
        }).catch(res => {
            //401 认证失败，重新登录
            if (res.response.status === 401) {
                //退出
                useStore.userLogoutHandler()
                //跳转到登录页
                return next({path: routerWhiteList[0]})
            }
        })
    }

    //放行，如果跳转到新添加的路由next()中需要给参数to.fullPath，否则刷新会404
    hasNewRouter ?  next(to.fullPath) : next()
})

//路由后置守卫
router.afterEach(() => {
    //关闭加载条
    NProgress.done()
})

export default router
