# PDD 电商微服务系统

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 4.0.3](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud Alibaba 2023.0.1.0](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2023.0.1.0-orange.svg)](https://spring.io/projects/spring-cloud-alibaba)
[![Gradle 9.2.0](https://img.shields.io/badge/Gradle-9.2.0-purple.svg)](https://gradle.org/)

## 项目简介

PDD 电商微服务系统是一个基于 Spring Boot 和 Spring Cloud Alibaba 构建的现代化电商平台，采用微服务架构设计，包含订单服务、用户服务、产品服务和 Flutter 前端应用。系统实现了电商平台的核心功能，支持高并发、高可用的业务场景。

## 功能特性

### 订单服务
- 订单创建、查询、更新和取消
- 订单明细管理
- 分布式事务处理（基于 Seata）
- 订单状态流转管理

### 用户服务
- 用户注册、登录和信息管理
- 地址管理
- JWT 认证和授权
- 密码加密和安全管理

### 产品服务
- 产品管理（创建、更新、删除、查询）
- 库存管理和扣减
- 分类管理
- 产品搜索和筛选

### 前端应用
- 基于 Flutter 的跨平台应用
- 响应式用户界面
- 与后端服务的 RESTful API 通信
- 用户友好的交互体验

## 技术栈

### 后端技术
- **Java 21**：采用最新的 Java 版本，提供更好的性能和特性
- **Spring Boot 4.0.3**：简化应用开发和配置
- **Spring Cloud Alibaba 2023.0.1.0**：提供微服务生态组件
- **Gradle 9.2.0**：现代化的构建工具
- **MySQL 8.0+**：关系型数据库，存储业务数据
- **Redis 7.0+**：缓存和会话管理
- **Seata**：分布式事务解决方案
- **Sentinel**：流量控制和熔断降级
- **Feign**：声明式 HTTP 客户端

### 前端技术
- **Flutter 3.0+**：跨平台移动应用框架
- **Dart**：Flutter 的开发语言
- **Provider**：状态管理
- **HTTP**：网络请求

## 项目结构

```
pdd-microservices/
├── order-service/        # 订单服务
│   ├── src/main/java/    # 源代码
│   ├── src/main/resources/ # 配置文件
│   ├── src/test/         # 测试代码
│   └── build.gradle      # 构建配置
├── user-service/         # 用户服务
│   ├── src/main/java/    # 源代码
│   ├── src/main/resources/ # 配置文件
│   ├── src/test/         # 测试代码
│   └── build.gradle      # 构建配置
├── product-service/      # 产品服务
│   ├── src/main/java/    # 源代码
│   ├── src/main/resources/ # 配置文件
│   ├── src/test/         # 测试代码
│   └── build.gradle      # 构建配置
├── frontend/             # Flutter前端应用
│   ├── lib/              # 源代码
│   ├── assets/           # 静态资源
│   └── pubspec.yaml      # 依赖配置
├── build.gradle          # 根构建文件
├── settings.gradle       # 项目配置文件
└── README.md             # 项目说明文档
```

## 快速开始

### 前提条件
- JDK 21 或更高版本
- Gradle 9.2.0 或更高版本
- MySQL 8.0 或更高版本
- Redis 7.0 或更高版本
- Flutter 3.0 或更高版本（仅前端开发需要）

### 环境配置

1. **数据库配置**
   - 创建三个数据库：`order_db`、`user_db`、`product_db`
   - 执行各服务下的 SQL 初始化脚本

2. **Redis 配置**
   - 确保 Redis 服务运行在默认端口（6379）

3. **Seata 配置**
   - 启动 Seata 服务
   - 配置 Seata 数据源

### 构建和运行

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd pdd-microservices
   ```

2. **构建项目**
   ```bash
   ./gradlew clean build
   ```

3. **运行服务**
   - 启动 order-service
   ```bash
   cd order-service
   ./gradlew bootRun
   ```

   - 启动 user-service
   ```bash
   cd user-service
   ./gradlew bootRun
   ```

   - 启动 product-service
   ```bash
   cd product-service
   ./gradlew bootRun
   ```

4. **运行前端**
   ```bash
   cd frontend
   flutter pub get
   flutter run
   ```

## API 文档

各服务的 API 文档可通过 Swagger UI 访问：
- 订单服务：http://localhost:8080/swagger-ui.html
- 用户服务：http://localhost:8081/swagger-ui.html
- 产品服务：http://localhost:8082/swagger-ui.html

## 测试

### 单元测试
```bash
./gradlew test
```

### 集成测试
```bash
./gradlew integrationTest
```

## 贡献指南

1. **Fork 本仓库**
2. **创建功能分支**
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **提交更改**
   ```bash
   git commit -m "Add your feature"
   ```
4. **推送到分支**
   ```bash
   git push origin feature/your-feature-name
   ```
5. **创建 Pull Request**

## 代码规范

- 遵循 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- 使用 4 个空格进行缩进
- 类名使用驼峰命名法（PascalCase）
- 方法名和变量名使用驼峰命名法（camelCase）
- 常量使用全大写加下划线（UPPER_CASE_WITH_UNDERSCORES）

## 许可证

本项目采用 [MIT License](LICENSE)。

## 联系方式

- **项目维护者**：PDD 开发团队
- **邮箱**：1417623595@qq.com
- **GitHub**：https://github.com/pdd/pdd-microservices

## 鸣谢

感谢以下开源项目的支持：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud Alibaba](https://spring.io/projects/spring-cloud-alibaba)
- [Seata](https://seata.io/)
- [Flutter](https://flutter.dev/)
