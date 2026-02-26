# 项目技术栈版本说明

## 1. 版本概览

| 组件 | 版本 | 说明 |
|:---|:---|:---|
| **Gradle** | 9.2.0 | 构建工具 |
| **Spring Boot** | 4.0.3 | 核心框架 |
| **Spring Cloud Alibaba** | 2023.0.1.0 | 微服务组件 |
| **Java** | 21 | JDK版本 |
| **MyBatis-Plus** | 3.5.5 | ORM框架 |
| **MySQL Connector** | 8.3.0 | 数据库驱动 |
| **Redis** | 7.2.x | 缓存（服务端版本） |
| **Nacos** | 2.3.x | 注册中心/配置中心 |

## 2. 核心依赖版本

### 2.1 Spring Boot 4.0.3 相关依赖

| 依赖 | 版本 | 说明 |
|:---|:---|:---|
| spring-boot-starter-web | 4.0.3 | Web开发 |
| spring-boot-starter-actuator | 4.0.3 | 健康检查 |
| spring-boot-starter-test | 4.0.3 | 测试框架 |
| spring-boot-starter-data-redis | 4.0.3 | Redis集成 |
| spring-boot-starter-security | 4.0.3 | 安全框架 |
| spring-boot-starter-validation | 4.0.3 | 参数验证 |

### 2.2 Spring Cloud Alibaba 2023.0.1.0 相关依赖

| 依赖 | 版本 | 说明 |
|:---|:---|:---|
| spring-cloud-starter-alibaba-nacos-discovery | 2023.0.1.0 | 服务注册发现 |
| spring-cloud-starter-alibaba-nacos-config | 2023.0.1.0 | 配置中心 |
| spring-cloud-starter-alibaba-sentinel | 2023.0.1.0 | 流量控制 |
| spring-cloud-starter-alibaba-seata | 2023.0.1.0 | 分布式事务 |

### 2.3 数据库相关依赖

| 依赖 | 版本 | 说明 |
|:---|:---|:---|
| mybatis-plus-boot-starter | 3.5.5 | MyBatis增强 |
| mysql-connector-j | 8.3.0 | MySQL驱动（新命名） |
| druid-spring-boot-starter | 1.2.21 | 数据库连接池 |

### 2.4 工具类依赖

| 依赖 | 版本 | 说明 |
|:---|:---|:---|
| guava | 33.0.0-jre | Google工具库 |
| commons-lang3 | 3.14.0 | Apache工具库 |
| lombok | 1.18.30 | 代码简化 |
| mapstruct | 1.5.5.Final | 对象映射 |
| hutool-all | 5.8.25 | Java工具集 |

### 2.5 日志相关依赖

| 依赖 | 版本 | 说明 |
|:---|:---|:---|
| slf4j-api | 2.0.11 | 日志门面 |
| logback-classic | 1.4.14 | 日志实现 |

### 2.6 测试相关依赖

| 依赖 | 版本 | 说明 |
|:---|:---|:---|
| junit-jupiter | 5.10.1 | 单元测试 |
| mockito-core | 5.8.0 | Mock框架 |
| testcontainers | 1.19.3 | 容器化测试 |

## 3. 版本兼容性说明

### 3.1 Spring Boot 4.0.3 新特性

- **Java 21 基线**：Spring Boot 4.0 要求 Java 21 作为最低版本
- **虚拟线程支持**：默认启用虚拟线程（Project Loom）
- **GraalVM原生镜像**：改进的AOT处理
- **可观测性增强**：集成Micrometer Tracing
- **安全性提升**：默认启用更严格的安全配置

### 3.2 Spring Cloud Alibaba 2023.0.1.0

- 与 Spring Boot 4.0.x 完全兼容
- 支持 Java 21
- Nacos 2.3.x 支持
- Sentinel 流量控制增强

### 3.3 版本对应关系

```
Spring Boot 4.0.x
    ├── Spring Cloud 2023.x (2023.0.1.0 for Alibaba)
    ├── Spring Security 6.2.x
    ├── Spring Data 2024.0.x
    └── Spring Framework 6.2.x
```

## 4. Gradle配置

### 4.1 Gradle Wrapper配置

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-9.2.0-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

### 4.2 Gradle版本要求

- Gradle 9.x 是最新稳定版本
- 支持 Java 21 编译
- 改进的依赖解析性能
- 更好的Kotlin DSL支持

## 5. 环境要求

### 5.1 开发环境

| 组件 | 版本要求 | 说明 |
|:---|:---|:---|
| JDK | 21+ | 必须使用Java 21或更高版本 |
| Gradle | 9.2.0 | 构建工具 |
| IDE | IntelliJ IDEA 2024.1+ | 推荐IDE |
| MySQL | 8.0+ | 数据库 |
| Redis | 7.0+ | 缓存 |
| Nacos | 2.3.x | 注册/配置中心 |

### 5.2 运行环境

| 组件 | 版本要求 | 说明 |
|:---|:---|:---|
| JRE | 21+ | Java运行时 |
| Docker | 24.0+ | 容器化部署 |
| Kubernetes | 1.28+ | 容器编排（可选） |

## 6. 版本升级指南

### 6.1 从 Spring Boot 3.x 升级到 4.0

**主要变更**：

1. **Java版本升级**
   - 从 Java 17 升级到 Java 21
   - 启用虚拟线程支持

2. **依赖更新**
   - Spring Security 6.2.x
   - Spring Data 2024.0.x
   - Spring Framework 6.2.x

3. **配置变更**
   - `spring.mvc.pathmatch.matching-strategy` 默认为 `path-pattern-parser`
   - 新增虚拟线程配置 `spring.threads.virtual.enabled=true`

4. **API变更**
   - 部分废弃API已移除
   - 新增可观测性API

### 6.2 从 Spring Cloud Alibaba 2022.0.0.0 升级到 2023.0.1.0

**主要变更**：

1. **Nacos客户端升级**
   - 支持 Nacos 2.3.x
   - 改进的服务发现性能

2. **Sentinel升级**
   - 支持虚拟线程
   - 改进的流量控制策略

3. **Seata升级**
   - 支持新的分布式事务模式
   - 性能优化

## 7. 常见问题

### 7.1 版本冲突解决

**问题**：依赖版本冲突
**解决方案**：使用 `dependencyManagement` 统一管理版本

```groovy
dependencyManagement {
    imports {
        mavenBom 'org.springframework.boot:spring-boot-dependencies:4.0.3'
        mavenBom 'com.alibaba.cloud:spring-cloud-alibaba-dependencies:2023.0.1.0'
    }
}
```

### 7.2 Java 21 兼容性

**问题**：部分依赖不支持 Java 21
**解决方案**：升级到兼容版本，或使用 `--add-opens` JVM参数

### 7.3 Gradle 9.x 兼容性

**问题**：旧插件不兼容 Gradle 9.x
**解决方案**：升级插件版本，或使用兼容性配置

## 8. 版本更新日志

| 日期 | 版本变更 | 说明 |
|:---|:---|:---|
| 2026-02-25 | Spring Boot 3.2.0 → 4.0.3 | 升级到最新稳定版 |
| 2026-02-25 | Spring Cloud Alibaba 2022.0.0.0 → 2023.0.1.0 | 兼容Spring Boot 4.0 |
| 2026-02-25 | Java 17 → 21 | 升级JDK版本 |
| 2026-02-25 | MyBatis-Plus 3.5.0 → 3.5.5 | 兼容Java 21 |
| 2026-02-25 | MySQL Connector 8.0.33 → 8.3.0 | 升级驱动版本 |

---

**文档版本**：v1.0
**创建日期**：2026-02-25
**最后更新**：2026-02-25
**维护者**：架构组