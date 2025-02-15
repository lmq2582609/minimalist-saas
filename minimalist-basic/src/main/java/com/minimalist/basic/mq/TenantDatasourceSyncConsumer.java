package com.minimalist.basic.mq;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.entity.po.MTenantDatasource;
import com.minimalist.basic.entity.vo.tenant.TenantDatasourceVO;
import com.minimalist.basic.manager.TenantManager;
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
 * 租户数据源信息同步消费者
 * 主要是防止部署多个节点，修改后不同步问题
 * 因为数据会缓存在一个Map中，在页面上修改后，需要让所有节点在内存中的Map同时更新
 */
@Slf4j
@Component
public class TenantDatasourceSyncConsumer implements ApplicationRunner {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TenantManager tenantManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RPatternTopic topic = redissonClient.getPatternTopic(RedisKeyConstant.TENANT_DATASOURCE_TOPIC_KEY + ".*");
        topic.addListener(String.class, (pattern, channel, msg) -> {
            log.info("租户数据源信息同步处理，参数：{}", msg);
            try {
                //区分新增、修改、删除
                String opt = StrUtil.subAfter(channel, ".", true);
                if (CommonConstant.ADD.equals(opt) || CommonConstant.UPDATE.equals(opt)) {
                    MTenantDatasource tenantDatasource = JSONUtil.toBean(msg, MTenantDatasource.class);
                    TenantDatasourceVO tenantDatasourceVO = BeanUtil.copyProperties(tenantDatasource, TenantDatasourceVO.class);
                    //数据源名称 = 租户ID
                    String tenantId = tenantDatasource.getTenantId().toString();
                    CommonConstant.tenantDatasourceMap.put(tenantId, tenantDatasourceVO);
                    //动态添加数据源
                    tenantManager.dynamicAddDatasource(tenantId, tenantDatasourceVO);
                }
                if (CommonConstant.DELETE.equals(opt)) {
                    CommonConstant.tenantDatasourceMap.remove(msg);
                    //动态删除数据源
                    tenantManager.dynamicDeleteDatasource(msg);
                }
                log.info("租户数据源信息同步处理，完毕！");
            } catch (Exception e) {
                log.error("租户数据源信息同步处理异常：", e);
            }
        });
        System.out.println("-------------------- 租户数据源信息同步消费者监听启动完毕 --------------------");
    }

}
