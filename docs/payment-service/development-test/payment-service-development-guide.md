# 支付服务开发与测试指南

## 1. 开发顺序与规划

### 1.1 开发步骤

#### 步骤1：项目初始化与配置
- **创建项目结构**：使用Spring Initializr创建Spring Boot项目
- **添加依赖**：在build.gradle中添加必要的依赖
- **配置文件**：创建application.yml和application-dev.yml
- **配置类**：创建RedisConfig、SeataConfig、SentinelConfig、WechatPayConfig、AlipayConfig等配置类

#### 步骤2：数据库设计与初始化
- **数据库表结构**：创建payment和refund表
- **实体类**：创建Payment和Refund实体类
- **Mapper接口**：创建PaymentMapper和RefundMapper
- **Mapper XML**：创建对应的XML映射文件
- **数据库初始化**：编写数据库初始化脚本

#### 步骤3：工具类开发
- **SignatureUtil**：实现签名验证工具
- **PaymentUtil**：实现支付工具类
- **测试**：为工具类编写单元测试

#### 步骤4：DTO与VO开发
- **DTO类**：创建PaymentDTO、RefundDTO、NotifyDTO
- **VO类**：创建PaymentVO、RefundVO
- **测试**：为DTO和VO编写单元测试

#### 步骤5：服务层开发
- **PaymentService**：实现支付管理相关功能
- **RefundService**：实现退款管理相关功能
- **WechatPayService**：实现微信支付相关功能
- **AlipayService**：实现支付宝相关功能
- **NotifyService**：实现支付回调相关功能
- **测试**：为每个服务类编写单元测试

#### 步骤6：控制器开发
- **PaymentController**：实现支付管理相关接口
- **RefundController**：实现退款管理相关接口
- **NotifyController**：实现支付回调相关接口
- **测试**：为每个控制器编写单元测试

#### 步骤7：主应用类开发
- **PaymentServiceApplication**：创建主应用类
- **测试**：编写集成测试

### 1.2 开发与测试并行策略
- **测试驱动开发**：先编写测试用例，再实现功能
- **单元测试**：每个类开发完成后立即编写单元测试
- **集成测试**：服务层和控制器开发完成后编写集成测试
- **持续集成**：配置CI/CD流程，自动运行测试

## 2. 测试文件夹结构

```plaintext
payment-service/
├── src/
│   ├── main/
│   │   ├── java/...  # 主代码
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── pdd/
│       │           └── payment/
│       │               ├── config/          # 配置类测试
│       │               │   ├── WechatPayConfigTest.java
│       │               │   └── AlipayConfigTest.java
│       │               ├── controller/      # 控制器测试
│       │               │   ├── PaymentControllerTest.java
│       │               │   ├── RefundControllerTest.java
│       │               │   └── NotifyControllerTest.java
│       │               ├── service/         # 服务层测试
│       │               │   ├── PaymentServiceTest.java
│       │               │   ├── RefundServiceTest.java
│       │               │   ├── WechatPayServiceTest.java
│       │               │   ├── AlipayServiceTest.java
│       │               │   └── NotifyServiceTest.java
│       │               ├── util/            # 工具类测试
│       │               │   ├── SignatureUtilTest.java
│       │               │   └── PaymentUtilTest.java
│       │               ├── dto/             # DTO测试
│       │               │   ├── PaymentDTOTest.java
│       │               │   └── RefundDTOTest.java
│       │               ├── integration/      # 集成测试
│       │               │   └── PaymentServiceIntegrationTest.java
│       │               └── PaymentServiceApplicationTests.java  # 应用测试
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
    
    // 第三方支付
    implementation 'com.github.wxpay:wxpay-sdk:3.0.9'
    implementation 'com.alipay.sdk:alipay-sdk-java:4.38.0.ALL'
    
    // 工具
    implementation 'org.apache.commons:commons-lang3:3.14.0'
    implementation 'org.projectlombok:lombok:1.18.30'
    implementation 'com.google.guava:guava:33.0.0-jre'
    
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
    name: payment-service
  datasource:
    url: jdbc:mysql://localhost:3306/pdd_payment?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
    password:
    database: 3
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml
    seata:
      tx-service-group: payment-service-group

server:
  port: 8084
  servlet:
    context-path: /payment

# 微信支付配置
wechat:
  pay:
    app-id: wx1234567890123456
    mch-id: 1234567890
    mch-key: 12345678901234567890123456789012
    notify-url: http://localhost:8084/payment/api/payments/notify/wechat

# 支付宝配置
alipay:
  app-id: 2016000000000000
  app-private-key: "私钥"
  alipay-public-key: "支付宝公钥"
  notify-url: http://localhost:8084/payment/api/payments/notify/alipay
  return-url: http://localhost:8080/payment/success

# Sentinel配置
sentinel:
  transport:
    dashboard: localhost:8080
  eager: true

# 日志配置
logging:
  level:
    com.pdd.payment: info
  config:
    classpath: logback-spring.xml
```

#### 3.1.3 配置类开发
- **RedisConfig.java**：配置RedisTemplate
- **SeataConfig.java**：配置Seata分布式事务
- **SentinelConfig.java**：配置Sentinel限流规则
- **WechatPayConfig.java**：配置微信支付
- **AlipayConfig.java**：配置支付宝

### 3.2 步骤2：数据库设计与初始化

#### 3.2.1 数据库表结构
```sql
-- 支付表
CREATE TABLE `payment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `payment_no` VARCHAR(32) UNIQUE NOT NULL,
  `order_id` BIGINT NOT NULL,
  `order_no` VARCHAR(32) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `payment_method` VARCHAR(20) NOT NULL,
  `payment_status` TINYINT DEFAULT 0,
  `transaction_id` VARCHAR(100),
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `pay_time` TIMESTAMP,
  `expire_time` TIMESTAMP,
  `notify_time` TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 退款表
CREATE TABLE `refund` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `refund_no` VARCHAR(32) UNIQUE NOT NULL,
  `payment_id` BIGINT NOT NULL,
  `order_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `refund_amount` DECIMAL(10,2) NOT NULL,
  `refund_reason` VARCHAR(255),
  `refund_status` TINYINT DEFAULT 0,
  `transaction_id` VARCHAR(100),
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `refund_time` TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`payment_id`) REFERENCES `payment`(`id`)
);
```

#### 3.2.2 实体类开发
- **Payment.java**：支付实体
- **Refund.java**：退款实体

#### 3.2.3 Mapper开发
- **PaymentMapper.java**：支付数据访问接口
- **RefundMapper.java**：退款数据访问接口
- **PaymentMapper.xml**：支付SQL映射
- **RefundMapper.xml**：退款SQL映射

### 3.3 步骤3：工具类开发

#### 3.3.1 SignatureUtil.java
```java
public class SignatureUtil {
    public static String generateSignature(Map<String, String> params, String key) {
        // 实现签名生成
    }
    
    public static boolean verifySignature(Map<String, String> params, String key) {
        // 实现签名验证
    }
}
```

#### 3.3.2 PaymentUtil.java
```java
public class PaymentUtil {
    public static String generatePaymentNo() {
        // 生成支付单号
    }
    
    public static String generateRefundNo() {
        // 生成退款单号
    }
    
    public static boolean isPaymentExpired(Date expireTime) {
        // 检查支付是否过期
    }
}
```

#### 3.3.3 工具类测试
- **SignatureUtilTest.java**：测试签名工具类
- **PaymentUtilTest.java**：测试支付工具类

### 3.4 步骤4：DTO与VO开发

#### 3.4.1 DTO类
- **PaymentDTO.java**：支付数据传输对象
- **RefundDTO.java**：退款数据传输对象
- **NotifyDTO.java**：回调数据传输对象

#### 3.4.2 VO类
- **PaymentVO.java**：支付视图对象
- **RefundVO.java**：退款视图对象

#### 3.4.3 DTO与VO测试
- **PaymentDTOTest.java**：测试支付DTO
- **RefundDTOTest.java**：测试退款DTO

### 3.5 步骤5：服务层开发

#### 3.5.1 PaymentService.java
```java
@Service
public class PaymentService {
    @GlobalTransactional(name = "create-payment", rollbackFor = Exception.class)
    public PaymentCreateResult createPayment(PaymentCreateDTO createDTO) {
        // 实现创建支付
    }
    
    public PaymentDTO getPaymentById(Long paymentId) {
        // 实现获取支付详情
    }
    
    public PaymentDTO getPaymentByNo(String paymentNo) {
        // 实现通过支付单号获取支付
    }
    
    @GlobalTransactional(name = "handle-payment-notify", rollbackFor = Exception.class)
    public boolean handlePaymentNotify(String paymentMethod, Map<String, String> params) {
        // 实现处理支付回调
    }
    
    public void closePayment(Long paymentId) {
        // 实现关闭支付
    }
}
```

#### 3.5.2 RefundService.java
```java
@Service
public class RefundService {
    @GlobalTransactional(name = "create-refund", rollbackFor = Exception.class)
    public RefundCreateResult createRefund(RefundCreateDTO createDTO) {
        // 实现创建退款
    }
    
    public RefundDTO getRefundById(Long refundId) {
        // 实现获取退款详情
    }
    
    public RefundDTO getRefundByNo(String refundNo) {
        // 实现通过退款单号获取退款
    }
    
    @GlobalTransactional(name = "handle-refund-notify", rollbackFor = Exception.class)
    public boolean handleRefundNotify(String paymentMethod, Map<String, String> params) {
        // 实现处理退款回调
    }
}
```

#### 3.5.3 WechatPayService.java
```java
@Service
public class WechatPayService {
    public String createPayment(Payment payment) {
        // 实现创建微信支付
    }
    
    public boolean verifyNotify(Map<String, String> params) {
        // 实现验证微信支付回调
    }
    
    public boolean refund(Payment payment, Refund refund) {
        // 实现微信退款
    }
}
```

#### 3.5.4 AlipayService.java
```java
@Service
public class AlipayService {
    public String createPayment(Payment payment) {
        // 实现创建支付宝支付
    }
    
    public boolean verifyNotify(Map<String, String> params) {
        // 实现验证支付宝回调
    }
    
    public boolean refund(Payment payment, Refund refund) {
        // 实现支付宝退款
    }
}
```

#### 3.5.5 NotifyService.java
```java
@Service
public class NotifyService {
    public void handleWechatNotify(Map<String, String> params) {
        // 处理微信支付回调
    }
    
    public void handleAlipayNotify(Map<String, String> params) {
        // 处理支付宝回调
    }
}
```

#### 3.5.6 服务层测试
- **PaymentServiceTest.java**：测试支付服务
- **RefundServiceTest.java**：测试退款服务
- **WechatPayServiceTest.java**：测试微信支付服务
- **AlipayServiceTest.java**：测试支付宝服务
- **NotifyServiceTest.java**：测试回调服务

### 3.6 步骤6：控制器开发

#### 3.6.1 PaymentController.java
```java
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @PostMapping
    public Result<PaymentCreateResult> createPayment(@Valid @RequestBody PaymentCreateDTO createDTO) {
        // 实现创建支付
    }
    
    @GetMapping("/{id}")
    public Result<PaymentVO> getPaymentById(@PathVariable Long id) {
        // 实现获取支付详情
    }
    
    @GetMapping("/{id}/status")
    public Result<PaymentStatusVO> getPaymentStatus(@PathVariable Long id) {
        // 实现获取支付状态
    }
    
    @PostMapping("/{id}/close")
    public Result<Void> closePayment(@PathVariable Long id) {
        // 实现关闭支付
    }
}
```

#### 3.6.2 RefundController.java
```java
@RestController
@RequestMapping("/api/refunds")
public class RefundController {
    @PostMapping
    public Result<RefundCreateResult> createRefund(@Valid @RequestBody RefundCreateDTO createDTO) {
        // 实现创建退款
    }
    
    @GetMapping("/{id}")
    public Result<RefundVO> getRefundById(@PathVariable Long id) {
        // 实现获取退款详情
    }
    
    @GetMapping("/{id}/status")
    public Result<RefundStatusVO> getRefundStatus(@PathVariable Long id) {
        // 实现获取退款状态
    }
}
```

#### 3.6.3 NotifyController.java
```java
@RestController
@RequestMapping("/api/payments/notify")
public class NotifyController {
    @PostMapping("/wechat")
    public String handleWechatNotify(HttpServletRequest request) {
        // 处理微信支付回调
    }
    
    @PostMapping("/alipay")
    public String handleAlipayNotify(HttpServletRequest request) {
        // 处理支付宝回调
    }
}
```

#### 3.6.4 控制器测试
- **PaymentControllerTest.java**：测试支付控制器
- **RefundControllerTest.java**：测试退款控制器
- **NotifyControllerTest.java**：测试回调控制器

### 3.7 步骤7：主应用类开发

#### 3.7.1 PaymentServiceApplication.java
```java
@SpringBootApplication
@MapperScan("com.pdd.payment.mapper")
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
```

#### 3.7.2 集成测试
- **PaymentServiceIntegrationTest.java**：测试完整的支付服务流程

## 4. 测试策略

### 4.1 单元测试
- **工具类测试**：测试SignatureUtil和PaymentUtil的核心方法
- **服务层测试**：测试PaymentService、RefundService、WechatPayService、AlipayService和NotifyService的业务逻辑
- **控制器测试**：测试各个接口的请求处理和响应

### 4.2 集成测试
- **服务集成测试**：测试服务层之间的调用
- **控制器集成测试**：测试完整的HTTP请求流程
- **数据库集成测试**：测试数据库操作的正确性
- **分布式事务测试**：测试Seata分布式事务的正确性
- **第三方支付集成测试**：测试与微信支付和支付宝的集成

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

### 7.1 第三方支付回调问题
- **解决方案**：确保回调地址可访问，验证签名正确性

### 7.2 分布式事务问题
- **解决方案**：检查Seata配置和事务注解

### 7.3 支付状态同步问题
- **解决方案**：实现定时任务同步支付状态

### 7.4 测试数据问题
- **解决方案**：使用测试数据隔离和清理机制

---

**文档版本**：v1.0
**编写日期**：2026-02-26
**编写人**：System Designer