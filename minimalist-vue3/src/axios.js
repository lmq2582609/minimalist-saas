import axios from "axios";
import Msg from '~/utils/msg';

const service = axios.create({
    //基础URL
    baseURL: import.meta.env.VITE_API_BASE_PREFIX,
    //请求超时30秒
    timeout: 30000
})
//请求拦截器
service.interceptors.request.use(function (config) {
    return config;
}, function (err) {
    //请求错误
    return Promise.reject(err);
});


//响应拦截器
service.interceptors.response.use(function (response) {
    //对响应数据做点什么
    return response.data
}, function (res) {
    //响应错误
    Msg.error(res.response.data || '请求失败')
    return Promise.reject(res);
});

export default service