/**
 * 存储系统缓存
 */
import { defineStore } from 'pinia'
import { removeToken } from "~/utils/cookie";

//命名最好以 `use` 开头且以 `Store` 结尾，(比如 `useUserStore`，`useCartStore`，`useProductStore`)
//"sysStore" -> 第一个参数是应用中 Store 的唯一ID
export const useSysStore = defineStore('sysStore', {
	state: () => ({
        //用户信息
        user: null,
        //sider展开/缩起，false展开，true缩起
        siderCollapsed: false,
        //sider当前宽度
        siderWidth: 200,
        //sider展开时的宽度
        siderMaxWidth: 200,
        //sider缩起时的宽度
        siderMinWidth: 48,
    }),
    actions: {
        //修改用户头像
        updateUserAvatar(userAvatar) {
            this.user.userAvatar = userAvatar
        },
        //用户退出登录后的处理
        userLogoutHandler() {
            //移除cookie里的token
            removeToken()
            //清除当前用户状态
            this.user = null
        }
    }
})
