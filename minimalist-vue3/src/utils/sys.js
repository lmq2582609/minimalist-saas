
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
