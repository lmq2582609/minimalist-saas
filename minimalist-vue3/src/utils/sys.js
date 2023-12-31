import pinia from '../store'
import { useSysStore } from '../store/module/sys-store.js'

//缓存
const sysStore = useSysStore(pinia)
//全局 - '是/否' 枚举
export const yesNo = {
    yes: {key: true, value: '是'},
    no: {key: false, value: '否'}
}
//全局 - '文件来源' 枚举
export const fileSource = {
    notice_cover_img: {key: 1, value: '系统公告封面图片'},
    notice_content_img: {key: 2, value: '系统公告内容图片'},
}
//全局 - '文件类型' 枚举
export const fileAccept = {
    //图片允许的类型
    img: '.jpg,.jpeg,.png,.jfif,.bmp,.webp',
}
//全局 - '上传文件列表样式' 枚举
export const fileListType = {
    text: 'text',
    picture: 'picture',
    pictureCard: 'picture-card'
}
//操作类型
export const operationType = {
    //新增
    add: {type: 'add', success: '添加成功', error: '添加失败'},
    //修改
    update: {type: 'update', success: '修改成功', error: '修改失败'},
    //查看详情
    detail: {type: 'detail'},
    //删除
    delete: {type: 'delete', success: '删除成功', error: '删除失败'},
    //上传
    upload: {type: 'upload', success: '上传成功', error: '上传失败'}
}

/**
 * 获取所有树中的父ID(只要有子集，就视为是父节点)
 * @param allTree 树
 * @param key 父ID的key名称
 * @returns [] 父ID
 */
export const getAllTreeParentId = (allTree, key) => {
    //所有父ID
    let allId = []
    const findAllTree = (arr) => {
        arr.forEach(node => {
            //如果包含子集，递归执行
            if (node.children && node.children.length > 0) {
                //有子集，放入返回结果
                allId.push(node[key])
                //递归
                findAllTree(node.children)
            }
        })
    }
    findAllTree(allTree)
    return allId
}

//0~9
const characters = '0123456789';
/**
 * 生成随机编码
 * @param length 编码位数
 */
export const randomCode = (length) => {
    let result = '';
    const charactersLength = characters.length;
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}

/**
 * 检查权限，不止按照权限标识检查，若 userIdArr 中任意数据与当前登录用户一致，则放行
 * @param permArr 权限标识数组
 * @param userIdArr 放行的用户ID
 */
export const hasPerm = (permArr = [], userIdArr = []) => {
    //当前登录用户ID
    let currentLoginUserId = sysStore.user.userId
    //当前用户如果是系统租户，则放行
    if (currentLoginUserId === '0') { return true }
    //传入的用户ID，与当前登录用户一致，则放行
    let checkUser = userIdArr.includes(currentLoginUserId)
    if (checkUser) { return true }
    //当前登录用户权限列表
    let perms = sysStore.user.perms
    return permArr.some(elem => perms.includes(elem))
}

/**
 * 检查角色，不止按照角色标识检查，若 userIdArr 中任意数据与当前登录用户一致，则放行
 * @param roleArr 角色标识数组
 * @param userIdArr 放行的用户ID
 */
export const hasRole = (roleArr = [], userIdArr = []) => {
    //当前登录用户ID
    let currentLoginUserId = sysStore.user.userId
    //当前用户如果是系统租户，则放行
    if (currentLoginUserId === '0') { return true }
    //传入的用户ID，与当前登录用户一致，则放行
    let checkUser = userIdArr.includes(currentLoginUserId)
    if (checkUser) { return true }
    //当前登录用户角色列表
    let roles = sysStore.user.roles
    return roleArr.some(elem => roles.includes(elem))
}