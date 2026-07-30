package com.minimalist.basic.service.impl;

import com.minimalist.basic.config.exception.BusinessException;
import com.minimalist.basic.entity.vo.tenant.TenantDatasourceVO;
import com.minimalist.basic.service.TenantDbInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * 租户数据库初始化服务实现
 * 创建租户时，连接目标数据库执行建表模板SQL
 */
@Slf4j
@Service
public class TenantDbInitServiceImpl implements TenantDbInitService {

    /** 建表模板SQL路径（classpath下） */
    private static final String TEMPLATE_SQL_PATH = "sql/tenant_init_template.sql";

    /**
     * 初始化租户数据库（自动建库 + 建表）
     * @param datasourceVO 数据源连接信息
     */
    @Override
    public void initTenantDatabase(TenantDatasourceVO datasourceVO) {
        //① 连接 MySQL（不指定库），创建数据库
        createDatabaseIfNotExists(datasourceVO);
        //② 连接目标数据库，执行建表模板SQL
        executeTemplateSql(datasourceVO);
    }

    /**
     * 创建数据库（如果不存在）
     */
    private void createDatabaseIfNotExists(TenantDatasourceVO datasourceVO) {
        String sql = "CREATE DATABASE IF NOT EXISTS `" + datasourceVO.getDatasourceName() + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";
        try (Connection conn = DriverManager.getConnection(
                datasourceVO.buildJdbcUrlWithoutDb(),
                datasourceVO.getUsername(),
                datasourceVO.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.info("租户数据库创建成功（或已存在），数据库名：{}", datasourceVO.getDatasourceName());
        } catch (Exception e) {
            log.error("租户数据库创建失败：", e);
            throw new BusinessException("租户数据库创建失败：" + e.getMessage());
        }
    }

    /**
     * 执行建表模板SQL
     */
    private void executeTemplateSql(TenantDatasourceVO datasourceVO) {
        String sql = loadTemplateSql();
        try (Connection conn = DriverManager.getConnection(
                datasourceVO.buildJdbcUrl(),
                datasourceVO.getUsername(),
                datasourceVO.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.info("租户数据库建表成功，数据库：{}", datasourceVO.getDatasourceName());
        } catch (Exception e) {
            log.error("租户数据库建表失败：", e);
            throw new BusinessException("租户数据库初始化失败：" + e.getMessage());
        }
    }

    /**
     * 加载建表模板SQL
     * @return SQL字符串
     */
    private String loadTemplateSql() {
        try {
            ClassPathResource resource = new ClassPathResource(TEMPLATE_SQL_PATH);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            log.error("读取租户建表模板SQL失败：", e);
            throw new BusinessException("读取租户建表模板SQL失败");
        }
    }

}
