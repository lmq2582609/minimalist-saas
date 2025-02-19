package com.minimalist.basic.mq;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.minimalist.basic.entity.enums.TenantEnum;
import com.minimalist.basic.entity.vo.tenant.TenantVO;
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
        RPatternTopic topic = redissonClient.getPatternTopic(RedisKeyConstant.TENANT_DATA_TOPIC_KEY + ".*");
        topic.addListener(String.class, (pattern, channel, msg) -> {
            log.info("租户数据源信息同步处理，参数：{}", msg);
            try {
                //区分新增、修改、删除
                String opt = StrUtil.subAfter(channel, ".", true);
                if (CommonConstant.ADD.equals(opt) || CommonConstant.UPDATE.equals(opt)) {
                    TenantVO tenantVO = JSONUtil.toBean(msg, TenantVO.class);
                    CommonConstant.tenantMap.put(tenantVO.getTenantId(), tenantVO);
                    //动态加载数据源
                    if (TenantEnum.DataIsolation.DB.getCode().equals(tenantVO.getDataIsolation())
                        && ObjectUtil.isNotNull(tenantVO.getTenantDatasource())) {
                        String tenantId = tenantVO.getTenantId().toString();
                        //删除旧数据源
                        tenantManager.dynamicDeleteDatasource(tenantId);
                        //动态添加数据源
                        tenantManager.dynamicAddDatasource(tenantId, tenantVO.getTenantDatasource());
                    } else {
                        //不需要数据库隔离，删除数据源
                        tenantManager.dynamicDeleteDatasource(tenantVO.getTenantId().toString());
                    }
                }
                if (CommonConstant.DELETE.equals(opt)) {
                    CommonConstant.tenantMap.remove(Long.parseLong(msg));
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
