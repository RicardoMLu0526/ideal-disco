# 订单服务开发与测试指南

## 1. 开发顺序与规划

### 1.1 开发步骤

#### 步骤1：项目初始化与配置
- **创建项目结构**：使用Spring Initializr创建Spring Boot项目
- **添加依赖**：在build.gradle中添加必要的依赖
- **配置文件**：创建application.yml和application-dev.yml
- **配置类**：创建RedisConfig、SeataConfig、SentinelConfig等配置类

#### 步骤2：数据库设计与初始化
- **数据库表结构**：创建order和order_item表
- **实体类**：创建Order和OrderItem实体类
- **Mapper接口**：创建OrderMapper和OrderItemMapper
- **Mapper XML**：创建对应的XML映射文件
- **数据库初始化**：编写数据库初始化脚本

#### 步骤3：工具类开发
- **IdGenerator**：实现分布式ID生成
- **OrderNoGenerator**：实现订单号生成
- **测试**：为工具类编写单元测试

#### 步骤4：DTO与VO开发
- **DTO类**：创建OrderDTO、OrderCreateDTO、OrderItemDTO
- **VO类**：创建OrderVO、OrderItemVO
- **测试**：为DTO和VO编写单元测试

#### 步骤5：服务层开发
- **OrderService**：实现订单管理相关功能
- **OrderItemService**：实现订单项管理相关功能
- **InventoryService**：实现库存服务（远程调用）
- **测试**：为每个服务类编写单元测试

#### 步骤6：控制器开发
- **OrderController**：实现订单管理相关接口
- **OrderItemController**：实现订单项管理相关接口
- **测试**：为每个控制器编写单元测试

#### 步骤7：主应用类开发
- **OrderServiceApplication**：创建主应用类
- **测试**：编写集成测试

### 1.2 开发与测试并行策略
- **测试驱动开发**：先编写测试用例，再实现功能
- **单元测试**：每个类开发完成后立即编写单元测试
- **集成测试**：服务层和控制器开发完成后编写集成测试
- **持续集成**：配置CI/CD流程，自动运行测试

## 2. 测试文件夹结构

```plaintext
order-service/
├── src/
│   ├── main/
│   │   ├── java/...  # 主代码
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── pdd/
│       │           └── order/
│       │               ├── config/          # 配置类测试
│       │               │   └── SeataConfigTest.java
│       │               ├── controller/      # 控制器测试
│       │               │   ├── OrderControllerTest.java
│       │               │   └── OrderItemControllerTest.java
│       │               ├── service/         # 服务层测试
│       │               │   ├── OrderServiceTest.java
│       │               │   ├── OrderItemServiceTest.java
│       │               │   └── InventoryServiceTest.java
│       │               ├── util/            # 工具类测试
│       │               │   ├── IdGeneratorTest.java
│       │               │   └── OrderNoGeneratorTest.java
│       │               ├── dto/             # DTO测试
│       │               │   ├── OrderDTOTest.java
│       │               │   └── OrderItemDTOTest.java
│       │               ├── integration/      # 集成测试
│       │               │   └── OrderServiceIntegrationTest.java
│       │               └── OrderServiceApplicationTests.java  # 应用测试
│       └── resources/  # 测试资源文件
│           ├── application-test.yml  # 测试配置
│           └── test-data.sql        # 测试数据
```

## 3. 详细开发指南

### 3.1 步骤1：项目初始化与配置

#### 3.1.1 build.gradle配置
```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.0.3'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.pdd'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '21'
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot核心依赖
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Spring Cloud Alibaba
    implementation 'com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery:2023.0.1.0'
    implementation 'com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config:2023.0.1.0'
    implementation 'com.alibaba.cloud:spring-cloud-starter-alibaba-sentinel:2023.0.1.0'
    implementation 'com.alibaba.cloud:spring-cloud-starter-alibaba-seata:2023.0.1.0'
    
    // 数据库
    implementation 'com.baomidou:mybatis-plus-boot-starter:3.5.5'
    implementation 'mysql:mysql-connector-java:8.3.0'
    
    // 工具
    implementation 'org.apache.commons:commons-lang3:3.14.0'
    implementation 'org.projectlombok:lombok:1.18.30'
    
    // 测试
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.mockito:mockito-core:5.8.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

#### 3.1.2 应用配置文件 (application.yml)
```yaml
spring:
  application:
    name: order-service
  datasource:
    url: jdbc:mysql://localhost:3306/pdd_order?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
    password:
    database: 2
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml
    seata:
      tx-service-group: order-service-group

server:
  port: 8083
  servlet:
    context-path: /order

# Sentinel配置
sentinel:
  transport:
    dashboard: localhost:8080
  eager: true

# 日志配置
logging:
  level:
    com.pdd.order: info
  config:
    classpath: logback-spring.xml
```

#### 3.1.3 配置类开发
- **RedisConfig.java**：配置RedisTemplate
- **SeataConfig.java**：配置Seata分布式事务
- **SentinelConfig.java**：配置Sentinel限流规则

### 3.2 步骤2：数据库设计与初始化

#### 3.2.1 数据库表结构
```sql
-- 订单表
CREATE TABLE `order` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_no` VARCHAR(32) UNIQUE NOT NULL,
  `user_id` BIGINT NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `actual_amount` DECIMAL(10,2) NOT NULL,
  `payment_status` TINYINT DEFAULT 0,
  `order_status` TINYINT DEFAULT 0,
  `shipping_address` VARCHAR(255) NOT NULL,
  `receiver_name` VARCHAR(50) NOT NULL,
  `receiver_phone` VARCHAR(20) NOT NULL,
  `payment_time` TIMESTAMP,
  `shipping_time` TIMESTAMP,
  `confirm_time` TIMESTAMP,
  `cancel_time` TIMESTAMP,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 订单项表
CREATE TABLE `order_item` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `product_sku_id` BIGINT,
  `product_name` VARCHAR(200) NOT NULL,
  `product_sku` VARCHAR(255),
  `price` DECIMAL(10,2) NOT NULL,
  `quantity` INT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`order_id`) REFERENCES `order`(`id`)
);
```

#### 3.2.2 实体类开发
- **Order.java**：订单实体
- **OrderItem.java**：订单项实体

#### 3.2.3 Mapper开发
- **OrderMapper.java**：订单数据访问接口
- **OrderItemMapper.java**：订单项数据访问接口
- **OrderMapper.xml**：订单SQL映射
- **OrderItemMapper.xml**：订单项SQL映射

### 3.3 步骤3：工具类开发

#### 3.3.1 IdGenerator.java
```java
@Component
public class IdGenerator {
    private final Snowflake snowflake;
    
    public IdGenerator() {
        long workerId = 1;
        long datacenterId = 1;
        this.snowflake = new Snowflake(workerId, datacenterId);
    }
    
    public Long nextId() {
        return snowflake.nextId();
    }
}
```

#### 3.3.2 OrderNoGenerator.java
```java
public class OrderNoGenerator {
    public static String generate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String datePart = sdf.format(new Date());
        String randomPart = String.format("%06d", new Random().nextInt(1000000));
        return datePart + randomPart;
    }
}
```

#### 3.3.3 工具类测试
- **IdGeneratorTest.java**：测试ID生成器
- **OrderNoGeneratorTest.java**：测试订单号生成器

### 3.4 步骤4：DTO与VO开发

#### 3.4.1 DTO类
- **OrderDTO.java**：订单数据传输对象
- **OrderCreateDTO.java**：订单创建数据传输对象
- **OrderItemDTO.java**：订单项数据传输对象

#### 3.4.2 VO类
- **OrderVO.java**：订单视图对象
- **OrderItemVO.java**：订单项视图对象

#### 3.4.3 DTO与VO测试
- **OrderDTOTest.java**：测试订单DTO
- **OrderItemDTOTest.java**：测试订单项DTO

### 3.5 步骤5：服务层开发

#### 3.5.1 OrderService.java
```java
@Service
public class OrderService {
    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public OrderCreateResult createOrder(OrderCreateDTO createDTO) {
        // 实现创建订单逻辑
    }
    
    public OrderDTO getOrderById(Long orderId) {
        // 实现获取订单详情
    }
    
    public List<OrderDTO> getOrderList(Long userId) {
        // 实现获取订单列表
    }
    
    @GlobalTransactional(name = "cancel-order", rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        // 实现取消订单
    }
    
    @GlobalTransactional(name = "pay-order", rollbackFor = Exception.class)
    public void payOrder(Long orderId, String paymentMethod) {
        // 实现支付订单
    }
}
```

#### 3.5.2 OrderItemService.java
```java
@Service
public class OrderItemService {
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        // 实现获取订单项列表
    }
    
    public OrderItemDTO getOrderItemById(Long orderItemId) {
        // 实现获取订单项详情
    }
}
```

#### 3.5.3 InventoryService.java
```java
@Service
public class InventoryService {
    public boolean preDeductStock(Long productId, Integer quantity) {
        // 实现预扣库存
    }
    
    public void confirmDeductStock(Long productId, Integer quantity) {
        // 实现确认扣减库存
    }
    
    public void releaseStock(Long productId, Integer quantity) {
        // 实现释放库存
    }
}
```

#### 3.5.4 服务层测试
- **OrderServiceTest.java**：测试订单服务
- **OrderItemServiceTest.java**：测试订单项服务
- **InventoryServiceTest.java**：测试库存服务

### 3.6 步骤6：控制器开发

#### 3.6.1 OrderController.java
```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @GetMapping
    public Result<List<OrderVO>> getOrderList() {
        // 实现获取订单列表
    }
    
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderById(@PathVariable Long id) {
        // 实现获取订单详情
    }
    
    @PostMapping
    public Result<OrderCreateResult> createOrder(@Valid @RequestBody OrderCreateDTO createDTO) {
        // 实现创建订单
    }
    
    @PutMapping("/{id}")
    public Result<OrderVO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        // 实现更新订单
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        // 实现删除订单
    }
    
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        // 实现取消订单
    }
    
    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(@PathVariable Long id, @RequestBody PaymentDTO paymentDTO) {
        // 实现支付订单
    }
    
    @PostMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        // 实现确认收货
    }
}
```

#### 3.6.2 OrderItemController.java
```java
@RestController
@RequestMapping("/api/orders/{orderId}/items")
public class OrderItemController {
    @GetMapping
    public Result<List<OrderItemVO>> getOrderItems(@PathVariable Long orderId) {
        // 实现获取订单项列表
    }
    
    @GetMapping("/{id}")
    public Result<OrderItemVO> getOrderItemById(@PathVariable Long orderId, @PathVariable Long id) {
        // 实现获取订单项详情
    }
}
```

#### 3.6.3 控制器测试
- **OrderControllerTest.java**：测试订单控制器
- **OrderItemControllerTest.java**：测试订单项控制器

### 3.7 步骤7：主应用类开发

#### 3.7.1 OrderServiceApplication.java
```java
@SpringBootApplication
@MapperScan("com.pdd.order.mapper")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

#### 3.7.2 集成测试
- **OrderServiceIntegrationTest.java**：测试完整的订单服务流程

## 4. 测试策略

### 4.1 单元测试
- **工具类测试**：测试IdGenerator和OrderNoGenerator的核心方法
- **服务层测试**：测试OrderService、OrderItemService和InventoryService的业务逻辑
- **控制器测试**：测试各个接口的请求处理和响应

### 4.2 集成测试
- **服务集成测试**：测试服务层之间的调用
- **控制器集成测试**：测试完整的HTTP请求流程
- **数据库集成测试**：测试数据库操作的正确性
- **分布式事务测试**：测试Seata分布式事务的正确性

### 4.3 测试覆盖率目标
- **代码覆盖率**：≥80%
- **分支覆盖率**：≥70%
- **行覆盖率**：≥85%

## 5. 开发规范

### 5.1 代码规范
- **命名规范**：使用驼峰命名法
- **代码风格**：遵循Java Code Conventions
- **注释规范**：每个类和方法都要有Javadoc注释

### 5.2 提交规范
- **提交信息格式**：`type(scope): description`
- **提交频率**：小步提交，每个功能或bug修复单独提交
- **代码审查**：提交前进行自我审查，确保代码质量

### 5.3 测试规范
- **测试命名**：`TestClassName`
- **测试方法命名**：`testMethodName`
- **测试数据**：使用真实的测试数据
- **测试断言**：使用Junit5的断言方法

## 6. 持续集成

### 6.1 CI/CD配置
- **Jenkins**：配置Jenkins pipeline
- **GitHub Actions**：配置GitHub Actions workflow
- **GitLab CI**：配置GitLab CI pipeline

### 6.2 构建与测试流程
1. **代码检查**：执行静态代码分析
2. **构建项目**：编译代码
3. **运行测试**：执行单元测试和集成测试
4. **生成报告**：生成测试覆盖率报告
5. **部署测试**：部署到测试环境

## 7. 常见问题与解决方案

### 7.1 分布式事务问题
- **解决方案**：检查Seata配置和事务注解

### 7.2 库存扣减问题
- **解决方案**：确保库存扣减的原子性和一致性

### 7.3 订单状态管理
- **解决方案**：使用状态机管理订单状态流转

### 7.4 测试数据问题
- **解决方案**：使用测试数据隔离和清理机制

---

**文档版本**：v1.0
**编写日期**：2026-02-26
**编写人**：System Designer