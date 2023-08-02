package com.minimalist.basic.config.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.minimalist.basic.entity.mybatis.BaseEntity;

import java.util.Collections;
import java.util.Scanner;

public class MybatisPlusCodeGenerator {

    private static final String url = "jdbc:mysql://117.50.179.102:12306/minimalist?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true&useAffectedRows=true";
    private static final String username = "root";
    private static final String password = "iI483hP0PA3HhrBJ";

    //读取控制台内容
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        System.out.println("请注意：本次操作将覆盖已生成的文件，请做好备份！！！");
        System.out.println("*******************************************");
        System.out.println("*******************************************");
        //获取程序当前路径
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        String inc = scanner("要生成的表名，多个英文逗号分割");

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.fileOverride() // 覆盖已生成文件
                            .outputDir(projectPath + "\\generator\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.minimalist.mybatisextend") // 设置父包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/generator/resources/mappers")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .enableLombok()
                            //不要开启链式模式，否则批量插入会因为找不到set方法报错
                            //.enableChainModel()
                            .enableTableFieldAnnotation()
                            .superClass(BaseEntity.class)
                            .addSuperEntityColumns("id", "deleted", "create_time", "create_id", "update_id", "update_time", "version")
                            .idType(IdType.AUTO);
                    builder.addInclude(inc.split(",")); // 设置需要生成的表名
                })
                .execute();
    }

}
