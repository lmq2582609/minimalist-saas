import { Message } from '@arco-design/web-vue';

export default {
    success(msg) {
        Message.success(msg || '操作成功')
    },
    error(msg) {
        Message.error(msg || '操作失败')
    }
}