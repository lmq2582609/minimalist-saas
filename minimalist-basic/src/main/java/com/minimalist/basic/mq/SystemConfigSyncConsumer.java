package com.minimalist.basic.mq;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.entity.vo.config.ConfigVO;
import com.minimalist.basic.utils.CommonConstant;
import com.minimalist.basic.utils.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 系统配置同步消费者
 * 主要是防止部署多个节点，修改后不同步问题
 * 因为数据会缓存在一个Map中，在页面上修改后，需要让所有节点在内存中的Map同时更新
 */
@Slf4j
@Component
public class SystemConfigSyncConsumer implements ApplicationRunner {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RPatternTopic topic = redissonClient.getPatternTopic(RedisKeyConstant.SYSTEM_CONFIG_TOPIC_KEY + ".*");
        topic.addListener(String.class, (pattern, channel, msg) -> {
            log.info("系统配置同步处理，参数：{}", msg);
            try {
                //区分新增、修改、删除
                String opt = StrUtil.subAfter(channel, ".", true);
                if (CommonConstant.ADD.equals(opt) || CommonConstant.UPDATE.equals(opt)) {
                    ConfigVO configVO = JSONUtil.toBean(msg, ConfigVO.class);
                    CommonConstant.systemConfigMap.put(configVO.getConfigKey(), configVO);
                }
                if (CommonConstant.DELETE.equals(opt)) {
                    CommonConstant.systemConfigMap.remove(msg);
                }
                log.info("系统配置同步处理，完毕！");
            } catch (Exception e) {
                log.error("系统配置同步处理异常：", e);
            }
        });
        System.out.println("-------------------- 系统配置同步消费者监听启动完毕 --------------------");
    }

}
