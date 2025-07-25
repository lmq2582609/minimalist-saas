import { useCookies } from '@vueuse/integrations/useCookies'

const cookie = useCookies()

//租户切换的租户ID 字符串格式 key
export const CHANGE_TENANT_ID = 'change_tenant_id'
//租户切换的租户ID base64格式 key
export const CHANGE_TENANT_ID_BASE64 = 'change_tenant_id_base64'
//标签页 key
export const PAGE_TAB_LIST = 'pageTabList'
//token key
export const Authentication = 'Authentication'
//租户切换操作权限标记 key
export const CHANGE_TENANT_ALLOW = 'changeTenantAllow'

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


export function getCookie(key) {
    return cookie.get(key)
}
export function setCookie(key, value) {
    return cookie.set(key, value)
}
