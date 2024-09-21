import {hasPerm} from "~/utils/sys.js";

/**
 * 自定义指令 v-perm，可传递两类参数，数组和对象
 * v-perm="['user:add', 'user:get']"    -> 数组参数：传入权限编码
 * v-perm="{ perms: [], userIds: [] }"  -> 对象参数：传入权限编码 和 用户ID，为何要传入用户ID请参考 hasPerm方法的注释
 */
export default {
    install(app) {
        app.directive('perm', {
            mounted(el, binding) {
                //权限列表参数
                let perms = []
                //用户ID参数
                let userIds = []
                let params = binding.value
                if (params instanceof Array) {
                    perms = params
                } else if (params instanceof Object) {
                    perms = params.perms
                    userIds = params.userIds
                } else {
                    throw new Error('使用v-perm指令时参数错误')
                }
                //校验权限
                let checkPerm = hasPerm(perms, userIds)
                if (!checkPerm && el?.parentNode) {
                     el.parentNode.removeChild(el)
                }
            }
        })
    }
}
