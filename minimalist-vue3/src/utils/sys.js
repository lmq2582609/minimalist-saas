import pinia from '../store'
import { useSysStore } from '../store/module/sys-store.js'

//缓存
const sysStore = useSysStore(pinia)

//公共的状态
export const status = {
    status_0: {key: 0, value: '禁用'},
    status_1: {key: 1, value: '正常'}
}

//全局 - '是/否' 枚举
export const yesNo = {
    yes: {key: true, value: '是'},
    no: {key: false, value: '否'}
}
//文件类型
export const fileType = {
    image: {key: 'image', value: '图片'},
    video: {key: 'video', value: '视频'}
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
    //视频允许的类型
    video: '.mp4,.avi,.mov,.flv,.mpeg,.3gp,.rmvb,.ts',
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
    upload: {type: 'upload', success: '上传成功', error: '上传失败'},
    //操作
    operation: {type: 'operation', success: '操作成功', error: '操作失败'}
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
 * 视频类型处理
 * @param url 视频url
 * @returns {`video/${string}`}
 */
export const videoTypeHandler = (url) => {
    let type = url.split('.').pop().toLowerCase()
    return `video/${type}`
}

/**
 * 检查权限，不止按照权限标识检查，若 userIdArr 中任意数据与当前登录用户一致，则放行
 * 场景举例：假设有一个项目管理系统，有一个产品需求列表，有很多行数据，
 * 当某一行数据的产品经理 = 当前登录的用户时，才显示修改和删除按钮，其余数据隐藏按钮
 * @param permArr 权限标识数组
 * @param userIdArr 放行的用户ID
 */
export const hasPerm = (permArr = [], userIdArr = []) => {
    return checkPermOrRole(permArr, userIdArr, 'perm')
}

/**
 * 检查角色，不止按照角色标识检查，若 userIdArr 中任意数据与当前登录用户一致，则放行
 * 场景举例：假设有一个项目管理系统，有一个产品需求列表，有很多行数据，
 * 当某一行数据的产品经理 = 当前登录的用户时，才显示修改和删除按钮，其余数据隐藏按钮
 * @param roleArr 角色标识数组
 * @param userIdArr 放行的用户ID
 */
export const hasRole = (roleArr = [], userIdArr = []) => {
    return checkPermOrRole(roleArr, userIdArr, 'role')
}

const checkPermOrRole = (checkArr = [], userIdArr = [], checkType) => {
    //当前登录用户ID
    let currentLoginUserId = sysStore.user.userId
    //传入的用户ID，与当前登录用户一致，则放行
    let checkUser = userIdArr.includes(currentLoginUserId)
    if (checkUser) { return true }
    //校验权限或角色
    let arr = checkType === 'role' ? sysStore.user.roles : sysStore.user.perms
    return checkArr.some(elem => arr.includes(elem))
}
