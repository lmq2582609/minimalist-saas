/**
 * 存储系统缓存
 */
import { defineStore } from 'pinia'
import { useCookies } from '@vueuse/integrations/useCookies'
import {
    Authentication,
    PAGE_TAB_LIST,
    CHANGE_TENANT_ID,
    CHANGE_TENANT_ID_BASE64,
    CHANGE_TENANT_ALLOW
} from "~/utils/cookie.js";

//命名最好以 `use` 开头且以 `Store` 结尾，(比如 `useUserStore`，`useCartStore`，`useProductStore`)
//"sysStore" -> 第一个参数是应用中 Store 的唯一ID
export const useSysStore = defineStore('sysStore', {
	state: () => ({
        //用户信息
        user: null,
        //是否调用过用户信息接口
        hasGetUserinfo: false,
        //sider展开/缩起，false展开，true缩起
        siderCollapsed: false,
        //sider当前宽度
        siderWidth: 200,
        //sider展开时的宽度
        siderMaxWidth: 200,
        //sider缩起时的宽度
        siderMinWidth: 48,
        //缓存的页面 - 首页index默认被缓存
        includePage: ['index']
    }),
    actions: {
        //修改用户头像
        updateUserAvatar(userAvatar) {
            this.user.userAvatar = userAvatar
        },
        //用户退出登录后的处理
        userLogoutHandler() {
            //清空cookie
            let cookie = useCookies()
            //清空请求头
            cookie.remove(Authentication)
            //清空租户切换的租户ID
            cookie.remove(CHANGE_TENANT_ID)
            //清空租户切换的租户ID
            cookie.remove(CHANGE_TENANT_ID_BASE64)
            //清空标签页
            cookie.remove(PAGE_TAB_LIST)
            //清空租户切换权限标记
            cookie.remove(CHANGE_TENANT_ALLOW)
            //清除当前用户状态
            this.user = null
            //是否获取过用户信息接口置为false
            this.hasGetUserinfo = false
        }
    }
})
