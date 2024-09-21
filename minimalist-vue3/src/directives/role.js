import { hasRole } from "~/utils/sys.js";

/**
 * 自定义指令 v-role，可传递两类参数，数组和对象
 * v-role="['admin', 'pm']"    -> 数组参数：传入角色编码
 * v-role="{ roles: [], userIds: [] }"  -> 对象参数：传入角色编码 和 用户ID，为何要传入用户ID请参考 hasPerm方法的注释
 */
export default {
    install(app) {
        app.directive('role', {
            mounted(el, binding) {
                //角色列表参数
                let roles = []
                //用户ID参数
                let userIds = []
                let params = binding.value
                if (params instanceof Array) {
                    roles = params
                } else if (params instanceof Object) {
                    roles = params.roles
                    userIds = params.userIds
                } else {
                    throw new Error('使用v-role指令时参数错误')
                }
                //校验角色
                let checkRole = hasRole(roles, userIds)
                if (!checkRole && el?.parentNode) {
                     el.parentNode.removeChild(el)
                }
            }
        })
    }
}
