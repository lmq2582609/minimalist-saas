package com.minimalist.basic.config.mybatis;

import com.minimalist.basic.config.mybatis.bo.BaseEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.TableConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 配置Mybatis-Flex生成代码
 */
public class GeneratorCodeHandler {

    public static final String url = "jdbc:mysql://127.0.0.1:3306/minimalist?characterEncoding=utf-8";

    public static final String username = "root";

    public static final String password = "123456";

    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        //项目根目录
        String projectPath = System.getProperty("user.dir");
        //生成配置
        GlobalConfig globalConfig = new GlobalConfig();
        //设置根包
        globalConfig.setBasePackage("com.minimalist.basic");
        //生成路径
        globalConfig.setSourceDir(projectPath + "\\generator\\java");

        /* ------------------------ 实体配置 ------------------------ */

        //生成实体
        globalConfig.setEntityGenerateEnable(true);
        //使用lombok
        globalConfig.setEntityWithLombok(true);
        //设置项目的JDK版本，项目的JDK为14及以上时建议设置该项，小于14则可以不设置
        globalConfig.setEntityJdkVersion(17);
        //设置生成 entity 包名
        globalConfig.setEntityPackage("com.minimalist.basic.entity.po");
        //自动填充
        TableConfig tableConfig = new TableConfig();
        tableConfig.setInsertListenerClass(InsertFullColumnHandler.class);
        tableConfig.setUpdateListenerClass(UpdateFullColumnHandler.class);
        globalConfig.setTableConfig(tableConfig);
        //设置实体类的父类
        globalConfig.setEntitySuperClass(BaseEntity.class);
        //父类字段忽略
        globalConfig.getStrategyConfig()
                .setIgnoreColumns("id", "deleted", "create_time", "create_id", "update_id", "update_time", "version");

        /* ------------------------ mapper 配置 ------------------------ */

        //设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);
        //mapper包
        globalConfig.setMapperPackage("com.minimalist.basic.mapper");
        //生成mapper xml文件
        globalConfig.enableMapperXml();
        //生成mapper.xml路径
        globalConfig.setMapperXmlPath(projectPath + "/generator/resources/mapper");

        /* ------------------------ 生成哪些表 ------------------------ */
        globalConfig.setGenerateTable(
                "m_config",
                "m_dept",
                "m_dict",
                "m_file",
                "m_notice",
                "m_perms",
                "m_post",
                "m_role",
                "m_role_dept",
                "m_role_perm",
                "m_storage",
                "m_tenant",
                "m_tenant_package",
                "m_tenant_package_perm",
                "m_user",
                "m_user_dept",
                "m_user_post",
                "m_user_role"
        );
        //是否生成service和controller
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);
        globalConfig.setControllerGenerateEnable(true);

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generatorHandler = new Generator(dataSource, globalConfig);
        generatorHandler.generate();
    }

}
