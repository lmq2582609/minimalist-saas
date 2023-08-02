import { useCookies } from '@vueuse/integrations/useCookies'

const cookie = useCookies()
//用户key
const userKey = 'user'
//token请求头中的key
export const Authentication = 'Authentication'

//获取token
export function getToken() {
    return cookie.get(Authentication)
}

//设置token
export function setToken(token) {
    return cookie.set(Authentication, token)
}

//清除token
export function removeToken() {
    return cookie.remove(Authentication)
}

//设置用户信息
export function setUser(user) {
    return cookie.set(userKey, JSON.stringify(user))
}
