package com.pdd.order.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DatabaseInitializer {

    private static final String URL = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void initialize() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            // 读取初始化脚本
            List<String> sqlStatements = readSqlFile("src/main/resources/init.sql");

            // 执行SQL语句
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    statement.executeUpdate(sql);
                    log.info("执行SQL: {}", sql);
                }
            }

            log.info("数据库初始化成功");

            // 插入测试数据
            List<String> testDataStatements = readSqlFile("src/test/resources/test-data.sql");
            for (String sql : testDataStatements) {
                if (!sql.trim().isEmpty()) {
                    statement.executeUpdate(sql);
                    log.info("执行测试数据SQL: {}", sql);
                }
            }

            log.info("测试数据插入成功");

        } catch (Exception e) {
            log.error("数据库初始化失败", e);
        }
    }

    private static List<String> readSqlFile(String filePath) throws IOException {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                currentStatement.append(line);
                if (line.endsWith(";")) {
                    statements.add(currentStatement.toString());
                    currentStatement = new StringBuilder();
                }
            }
        }

        return statements;
    }

    public static void main(String[] args) {
        initialize();
    }
}
