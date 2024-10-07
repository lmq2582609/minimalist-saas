import { useCookies } from '@vueuse/integrations/useCookies'

const cookie = useCookies()

//租户ID 字符串格式
export const TENANT_ID = 'tenant_id'
//租户ID base64格式
export const TENANT_ID_BASE64 = 'tenant_id_base64'
//标签页
export const PAGE_TAB_LIST = 'pageTabList'
//token
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
