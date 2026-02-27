# 用户服务开发与测试指南

## 1. 开发顺序与规划

### 1.1 开发步骤

#### 步骤1：项目初始化与配置
- **创建项目结构**：使用Spring Initializr创建Spring Boot项目
- **添加依赖**：在build.gradle中添加必要的依赖
- **配置文件**：创建application.yml和application-dev.yml
- **配置类**：创建RedisConfig、SecurityConfig、SentinelConfig等配置类

#### 步骤2：数据库设计与初始化
- **数据库表结构**：创建user和address表
- **实体类**：创建User和Address实体类
- **Mapper接口**：创建UserMapper和AddressMapper
- **Mapper XML**：创建对应的XML映射文件
- **数据库初始化**：编写数据库初始化脚本

#### 步骤3：工具类开发
- **JwtUtil**：实现JWT令牌生成和验证
- **PasswordUtil**：实现密码加密和验证
- **测试**：为工具类编写单元测试

#### 步骤4：DTO与VO开发
- **DTO类**：创建UserDTO、LoginDTO、AddressDTO
- **VO类**：创建UserVO、AddressVO
- **测试**：为DTO和VO编写单元测试

#### 步骤5：服务层开发
- **AuthService**：实现用户认证相关功能
- **UserService**：实现用户管理相关功能
- **AddressService**：实现地址管理相关功能
- **测试**：为每个服务类编写单元测试

#### 步骤6：控制器开发
- **AuthController**：实现认证相关接口
- **UserController**：实现用户管理相关接口
- **AddressController**：实现地址管理相关接口
- **测试**：为每个控制器编写单元测试

#### 步骤7：主应用类开发
- **UserServiceApplication**：创建主应用类
- **测试**：编写集成测试

### 1.2 开发与测试并行策略
- **测试驱动开发**：先编写测试用例，再实现功能
- **单元测试**：每个类开发完成后立即编写单元测试
- **集成测试**：服务层和控制器开发完成后编写集成测试
- **持续集成**：配置CI/CD流程，自动运行测试

## 2. 测试文件夹结构

```plaintext
user-service/
├── src/
│   ├── main/
│   │   ├── java/...  # 主代码
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── pdd/
│       │           └── user/
│       │               ├── config/          # 配置类测试
│       │               │   └── SecurityConfigTest.java
│       │               ├── controller/      # 控制器测试
│       │               │   ├── AuthControllerTest.java
│       │               │   ├── UserControllerTest.java
│       │               │   └── AddressControllerTest.java
│       │               ├── service/         # 服务层测试
│       │               │   ├── AuthServiceTest.java
│       │               │   ├── UserServiceTest.java
│       │               │   └── AddressServiceTest.java
│       │               ├── util/            # 工具类测试
│       │               │   ├── JwtUtilTest.java
│       │               │   └── PasswordUtilTest.java
│       │               ├── dto/             # DTO测试
│       │               │   ├── UserDTOTest.java
│       │               │   └── AddressDTOTest.java
│       │               ├── integration/      # 集成测试
│       │               │   └── UserServiceIntegrationTest.java
│       │               └── UserServiceApplicationTests.java  # 应用测试
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
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Spring Cloud Alibaba
    implementation 'com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery:2023.0.1.0'
    implementation 'com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config:2023.0.1.0'
    implementation 'com.alibaba.cloud:spring-cloud-starter-alibaba-sentinel:2023.0.1.0'
    
    // 数据库
    implementation 'com.baomidou:mybatis-plus-boot-starter:3.5.5'
    implementation 'mysql:mysql-connector-java:8.3.0'
    
    // 工具
    implementation 'io.jsonwebtoken:jjwt:0.12.5'
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
    name: user-service
  datasource:
    url: jdbc:mysql://localhost:3306/pdd_user?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml

server:
  port: 8081
  servlet:
    context-path: /user

jwt:
  secret: pdd_user_service_secret_key
  expiration: 86400

# Sentinel配置
sentinel:
  transport:
    dashboard: localhost:8080
  eager: true

# 日志配置
logging:
  level:
    com.pdd.user: info
  config:
    classpath: logback-spring.xml
```

#### 3.1.3 配置类开发
- **RedisConfig.java**：配置RedisTemplate
- **SecurityConfig.java**：配置Spring Security
- **SentinelConfig.java**：配置Sentinel限流规则

### 3.2 步骤2：数据库设计与初始化

#### 3.2.1 数据库表结构
```sql
-- 用户表
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) UNIQUE NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) UNIQUE,
  `email` VARCHAR(100) UNIQUE,
  `nickname` VARCHAR(50),
  `avatar` VARCHAR(255),
  `gender` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 地址表
CREATE TABLE `address` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `province` VARCHAR(50) NOT NULL,
  `city` VARCHAR(50) NOT NULL,
  `district` VARCHAR(50) NOT NULL,
  `detail` VARCHAR(255) NOT NULL,
  `is_default` TINYINT DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);
```

#### 3.2.2 实体类开发
- **User.java**：用户实体
- **Address.java**：地址实体

#### 3.2.3 Mapper开发
- **UserMapper.java**：用户数据访问接口
- **AddressMapper.java**：地址数据访问接口
- **UserMapper.xml**：用户SQL映射
- **AddressMapper.xml**：地址SQL映射

### 3.3 步骤3：工具类开发

#### 3.3.1 JwtUtil.java
```java
public class JwtUtil {
    public static String generateToken(Long userId, String secret, Long expiration) {
        // 实现JWT令牌生成
    }
    
    public static Long getUserIdFromToken(String token, String secret) {
        // 实现JWT令牌解析
    }
}
```

#### 3.3.2 PasswordUtil.java
```java
public class PasswordUtil {
    public static String encode(String password) {
        // 实现密码加密
    }
    
    public static boolean matches(String rawPassword, String encodedPassword) {
        // 实现密码验证
    }
}
```

#### 3.3.3 工具类测试
- **JwtUtilTest.java**：测试JWT工具类
- **PasswordUtilTest.java**：测试密码工具类

### 3.4 步骤4：DTO与VO开发

#### 3.4.1 DTO类
- **UserDTO.java**：用户数据传输对象
- **LoginDTO.java**：登录请求数据传输对象
- **AddressDTO.java**：地址数据传输对象

#### 3.4.2 VO类
- **UserVO.java**：用户视图对象
- **AddressVO.java**：地址视图对象

#### 3.4.3 DTO与VO测试
- **UserDTOTest.java**：测试用户DTO
- **AddressDTOTest.java**：测试地址DTO

### 3.5 步骤5：服务层开发

#### 3.5.1 AuthService.java
```java
@Service
public class AuthService {
    public LoginResult login(LoginDTO loginDTO) {
        // 实现登录逻辑
    }
    
    public void logout(String token) {
        // 实现登出逻辑
    }
    
    public String refreshToken(String token) {
        // 实现令牌刷新
    }
}
```

#### 3.5.2 UserService.java
```java
@Service
public class UserService {
    public UserDTO getUserInfo(Long userId) {
        // 实现获取用户信息
    }
    
    public UserDTO updateUserInfo(Long userId, UserDTO userDTO) {
        // 实现更新用户信息
    }
    
    public void updateUserStatus(Long userId, Integer status) {
        // 实现更新用户状态
    }
}
```

#### 3.5.3 AddressService.java
```java
@Service
public class AddressService {
    public AddressDTO addAddress(Long userId, AddressDTO addressDTO) {
        // 实现添加地址
    }
    
    public List<AddressDTO> getAddressList(Long userId) {
        // 实现获取地址列表
    }
    
    public AddressDTO getAddressById(Long addressId) {
        // 实现获取地址详情
    }
    
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        // 实现更新地址
    }
    
    public void deleteAddress(Long addressId) {
        // 实现删除地址
    }
    
    public void setDefaultAddress(Long userId, Long addressId) {
        // 实现设置默认地址
    }
}
```

#### 3.5.4 服务层测试
- **AuthServiceTest.java**：测试认证服务
- **UserServiceTest.java**：测试用户服务
- **AddressServiceTest.java**：测试地址服务

### 3.6 步骤6：控制器开发

#### 3.6.1 AuthController.java
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/register")
    public Result<LoginResult> register(@Valid @RequestBody RegisterDTO registerDTO) {
        // 实现注册接口
    }
    
    @PostMapping("/login")
    public Result<LoginResult> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 实现登录接口
    }
    
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        // 实现登出接口
    }
    
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        // 实现刷新令牌接口
    }
}
```

#### 3.6.2 UserController.java
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping
    public Result<List<UserVO>> getUserList() {
        // 实现获取用户列表
    }
    
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        // 实现获取用户详情
    }
    
    @PutMapping("/{id}")
    public Result<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        // 实现更新用户信息
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        // 实现删除用户
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        // 实现更新用户状态
    }
}
```

#### 3.6.3 AddressController.java
```java
@RestController
@RequestMapping("/api/users/{userId}/addresses")
public class AddressController {
    @GetMapping
    public Result<List<AddressVO>> getAddressList(@PathVariable Long userId) {
        // 实现获取地址列表
    }
    
    @PostMapping
    public Result<AddressVO> addAddress(@PathVariable Long userId, @Valid @RequestBody AddressDTO addressDTO) {
        // 实现添加地址
    }
    
    @GetMapping("/{id}")
    public Result<AddressVO> getAddressById(@PathVariable Long userId, @PathVariable Long id) {
        // 实现获取地址详情
    }
    
    @PutMapping("/{id}")
    public Result<AddressVO> updateAddress(@PathVariable Long userId, @PathVariable Long id, @Valid @RequestBody AddressDTO addressDTO) {
        // 实现更新地址
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long userId, @PathVariable Long id) {
        // 实现删除地址
    }
    
    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long userId, @PathVariable Long id) {
        // 实现设置默认地址
    }
}
```

#### 3.6.4 控制器测试
- **AuthControllerTest.java**：测试认证控制器
- **UserControllerTest.java**：测试用户控制器
- **AddressControllerTest.java**：测试地址控制器

### 3.7 步骤7：主应用类开发

#### 3.7.1 UserServiceApplication.java
```java
@SpringBootApplication
@MapperScan("com.pdd.user.mapper")
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

#### 3.7.2 集成测试
- **UserServiceIntegrationTest.java**：测试完整的用户服务流程

## 4. 测试策略

### 4.1 单元测试
- **工具类测试**：测试JwtUtil和PasswordUtil的核心方法
- **服务层测试**：测试AuthService、UserService和AddressService的业务逻辑
- **控制器测试**：测试各个接口的请求处理和响应

### 4.2 集成测试
- **服务集成测试**：测试服务层之间的调用
- **控制器集成测试**：测试完整的HTTP请求流程
- **数据库集成测试**：测试数据库操作的正确性

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

### 7.1 数据库连接问题
- **解决方案**：检查数据库配置和网络连接

### 7.2 令牌验证失败
- **解决方案**：检查JWT密钥和过期时间

### 7.3 权限控制问题
- **解决方案**：检查Spring Security配置

### 7.4 测试数据问题
- **解决方案**：使用测试数据隔离和清理机制

---

**文档版本**：v1.0
**编写日期**：2026-02-26
**编写人**：System Designer