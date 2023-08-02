import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import WindiCSS from 'vite-plugin-windicss'
import path from 'path'
// 当前执行node命令时文件夹的地址(工作目录)
const root = process.cwd()

export default defineConfig(({command, mode }) => {
    //获取.env文件里定义的环境变量
    const env = loadEnv(mode, process.cwd());
    return {
        root: root,
        base: env.VITE_BASE_PATH,
        resolve: {
            //别名配置
            alias: {
                //输入 ~ ，相当于src目录
                '~': path.resolve(__dirname, 'src')
            }
        },
        //本地运行配置
        server: {
            //代理
            proxy: {
                //请求 env.VITE_API_BASE_PREFIX，相当于请求 env.VITE_REQUEST_URL
                '/minimalist': {
                    target: env.VITE_REQUEST_URL,
                    //允许跨域
                    changeOrigin: true,
                    //规则匹配
                    rewrite: (path) => path.replace(/^\/minimalist/, '')
                }
            }
        },
        plugins: [vue(), WindiCSS()],
    }
})
