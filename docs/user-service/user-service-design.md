# 用户服务设计文档

## 1. 服务概述

用户服务是电商系统的核心服务之一，负责用户的全生命周期管理，包括用户的注册、登录、认证、信息管理、地址管理等功能。该服务采用微服务架构设计，与其他服务通过RESTful API进行通信。

## 2. 技术栈

| 类别 | 技术 | 版本 | 说明 |
| :--- | :--- | :--- | :--- |
| 基础框架 | Spring Boot | 4.0.3 | 应用基础框架 |
| 微服务框架 | Spring Cloud Alibaba | 2023.0.1.0 | 微服务生态 |
| 注册中心 | Nacos | 2.2.0 | 服务注册与发现 |
| 配置中心 | Nacos Config | 2.2.0 | 分布式配置管理 |
| 限流熔断 | Sentinel | 1.8.6 | 服务限流与熔断 |
| 数据库 | MySQL | 8.0 | 持久化存储 |
| 缓存 | Redis | 7.0+ | 缓存与会话管理 |
| 认证 | Spring Security | 6.2.0 | 安全认证框架 |
| 校验 | Spring Validation | 6.2.0 | 请求参数校验 |
| 日志 | Logback + SkyWalking | - | 日志收集与链路追踪 |

## 3. 目录结构

```plaintext
user-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── pdd/
│   │   │           └── user/
│   │   │               ├── UserServiceApplication.java       # 应用启动类
│   │   │               ├── config/                           # 配置类
│   │   │               │   ├── RedisConfig.java              # Redis配置
│   │   │               │   ├── SecurityConfig.java           # 安全配置
│   │   │               │   └── SentinelConfig.java           # Sentinel配置
│   │   │               ├── controller/                       # 控制器
│   │   │               │   ├── UserController.java           # 用户管理
│   │   │               │   ├── AuthController.java           # 认证管理
│   │   │               │   └── AddressController.java        # 地址管理
│   │   │               ├── dto/                              # 数据传输对象
│   │   │               │   ├── UserDTO.java                  # 用户DTO
│   │   │               │   ├── LoginDTO.java                 # 登录DTO
│   │   │               │   └── AddressDTO.java               # 地址DTO
│   │   │               ├── entity/                           # 实体类
│   │   │               │   ├── User.java                     # 用户实体
│   │   │               │   └── Address.java                  # 地址实体
│   │   │               ├── mapper/                           # MyBatis映射
│   │   │               │   ├── UserMapper.java               # 用户映射
│   │   │               │   └── AddressMapper.java            # 地址映射
│   │   │               ├── service/                          # 业务逻辑
│   │   │               │   ├── UserService.java              # 用户服务
│   │   │               │   ├── AuthService.java              # 认证服务
│   │   │               │   └── AddressService.java           # 地址服务
│   │   │               ├── util/                             # 工具类
│   │   │               │   ├── JwtUtil.java                  # JWT工具
│   │   │               │   └── PasswordUtil.java             # 密码工具
│   │   │               └── vo/                               # 视图对象
│   │   │                   ├── UserVO.java                   # 用户VO
│   │   │                   └── AddressVO.java                # 地址VO
│   │   └── resources/
│   │       ├── application.yml                              # 应用配置
│   │       ├── application-dev.yml                          # 开发环境配置
│   │       └── mapper/                                       # MyBatis映射文件
│   │           ├── UserMapper.xml                           # 用户映射文件
│   │           └── AddressMapper.xml                        # 地址映射文件
│   └── test/                                                 # 测试代码
├── pom.xml                                                   # Maven依赖
└── Dockerfile                                                # Docker构建文件
```

## 4. 核心功能

### 4.1 用户管理

- **用户注册**：支持手机号、邮箱注册
- **用户登录**：支持账号密码登录、手机号验证码登录
- **用户信息管理**：修改头像、昵称、密码等
- **用户状态管理**：启用、禁用用户

### 4.2 认证授权

- **JWT令牌生成**：登录成功后生成JWT令牌
- **令牌验证**：验证请求中的JWT令牌
- **权限控制**：基于角色的权限控制

### 4.3 地址管理

- **收货地址CRUD**：增删改查收货地址
- **默认地址设置**：设置默认收货地址

### 4.4 限流熔断

- **接口限流**：使用Sentinel对接口进行限流
- **服务熔断**：当服务异常时进行熔断保护

## 5. 数据库设计

### 5.1 用户表 (`user`)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 用户ID |
| `username` | `VARCHAR(50)` | `UNIQUE NOT NULL` | 用户名 |
| `password` | `VARCHAR(100)` | `NOT NULL` | 密码（加密存储） |
| `phone` | `VARCHAR(20)` | `UNIQUE` | 手机号 |
| `email` | `VARCHAR(100)` | `UNIQUE` | 邮箱 |
| `nickname` | `VARCHAR(50)` | | 昵称 |
| `avatar` | `VARCHAR(255)` | | 头像URL |
| `gender` | `TINYINT` | `DEFAULT 0` | 性别（0:未知,1:男,2:女） |
| `status` | `TINYINT` | `DEFAULT 1` | 状态（1:正常,0:禁用） |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 5.2 地址表 (`address`)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 地址ID |
| `user_id` | `BIGINT` | `NOT NULL` | 用户ID |
| `name` | `VARCHAR(20)` | `NOT NULL` | 收货人姓名 |
| `phone` | `VARCHAR(20)` | `NOT NULL` | 收货人电话 |
| `province` | `VARCHAR(50)` | `NOT NULL` | 省份 |
| `city` | `VARCHAR(50)` | `NOT NULL` | 城市 |
| `district` | `VARCHAR(50)` | `NOT NULL` | 区县 |
| `detail` | `VARCHAR(255)` | `NOT NULL` | 详细地址 |
| `is_default` | `TINYINT` | `DEFAULT 0` | 是否默认地址 |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

## 6. API设计

### 6.1 认证相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/auth/register` | `POST` | `AuthController` | 用户注册 | `{"username": "...", "password": "...", "phone": "..."}` | `{"code": 200, "message": "success", "data": {"userId": 1, "token": "..."}}` |
| `/api/auth/login` | `POST` | `AuthController` | 用户登录 | `{"username": "...", "password": "..."}` | `{"code": 200, "message": "success", "data": {"userId": 1, "token": "...", "userInfo": {...}}}` |
| `/api/auth/logout` | `POST` | `AuthController` | 用户登出 | N/A | `{"code": 200, "message": "success"}` |
| `/api/auth/refresh` | `POST` | `AuthController` | 刷新令牌 | `{"token": "..."}` | `{"code": 200, "message": "success", "data": {"token": "..."}}` |

### 6.2 用户相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/users` | `GET` | `UserController` | 获取用户列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/users/{id}` | `GET` | `UserController` | 获取用户详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{id}` | `PUT` | `UserController` | 更新用户信息 | `{"nickname": "...", "avatar": "..."}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{id}` | `DELETE` | `UserController` | 删除用户 | N/A | `{"code": 200, "message": "success"}` |
| `/api/users/{id}/status` | `PUT` | `UserController` | 修改用户状态 | `{"status": 1}` | `{"code": 200, "message": "success"}` |

### 6.3 地址相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/users/{userId}/addresses` | `GET` | `AddressController` | 获取用户地址列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/users/{userId}/addresses` | `POST` | `AddressController` | 添加收货地址 | `{"name": "...", "phone": "...", "province": "...", "city": "...", "district": "...", "detail": "...", "isDefault": true}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{userId}/addresses/{id}` | `GET` | `AddressController` | 获取地址详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{userId}/addresses/{id}` | `PUT` | `AddressController` | 更新地址信息 | `{"name": "...", "phone": "...", "province": "...", "city": "...", "district": "...", "detail": "..."}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{userId}/addresses/{id}` | `DELETE` | `AddressController` | 删除收货地址 | N/A | `{"code": 200, "message": "success"}` |
| `/api/users/{userId}/addresses/{id}/default` | `PUT` | `AddressController` | 设置默认地址 | N/A | `{"code": 200, "message": "success"}` |

## 7. 核心配置

### 7.1 应用配置 (`application.yml`)

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

### 7.2 安全配置

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 8. 核心代码

### 8.1 认证服务

```java
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * 用户登录
     */
    public LoginResult login(LoginDTO loginDTO) {
        // 1. 验证用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null || !PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 生成JWT令牌
        String token = JwtUtil.generateToken(user.getId(), jwtSecret, jwtExpiration);

        // 3. 缓存用户信息到Redis
        redisTemplate.opsForValue().set("user:token:" + token, user.getId(), jwtExpiration, TimeUnit.SECONDS);

        // 4. 构建返回结果
        LoginResult result = new LoginResult();
        result.setUserId(user.getId());
        result.setToken(token);
        result.setUserInfo(UserDTO.fromEntity(user));
        return result;
    }

    /**
     * 用户登出
     */
    public void logout(String token) {
        redisTemplate.delete("user:token:" + token);
    }
}
```

### 8.2 用户服务

```java
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取用户信息
     */
    public UserDTO getUserInfo(Long userId) {
        // 先从Redis缓存获取
        String key = "user:info:" + userId;
        UserDTO userDTO = (UserDTO) redisTemplate.opsForValue().get(key);
        if (userDTO != null) {
            return userDTO;
        }

        // 从数据库获取
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 缓存到Redis
        userDTO = UserDTO.fromEntity(user);
        redisTemplate.opsForValue().set(key, userDTO, 3600, TimeUnit.SECONDS);
        return userDTO;
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUserInfo(Long userId, UserDTO userDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        BeanUtils.copyProperties(userDTO, user, "id", "password", "createdAt");
        userMapper.updateById(user);

        // 清除缓存
        redisTemplate.delete("user:info:" + userId);

        return UserDTO.fromEntity(user);
    }
}
```

### 8.3 地址服务

```java
@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 添加收货地址
     */
    @Transactional
    public AddressDTO addAddress(Long userId, AddressDTO addressDTO) {
        Address address = AddressDTO.toEntity(addressDTO);
        address.setUserId(userId);

        // 如果设置为默认地址，先将其他地址设为非默认
        if (address.getIsDefault()) {
            addressMapper.updateNonDefaultByUserId(userId);
        }

        addressMapper.insert(address);
        return AddressDTO.fromEntity(address);
    }

    /**
     * 设置默认地址
     */
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // 先将所有地址设为非默认
        addressMapper.updateNonDefaultByUserId(userId);
        // 将指定地址设为默认
        Address address = new Address();
        address.setId(addressId);
        address.setIsDefault(true);
        addressMapper.updateById(address);
    }
}
```

### 8.4 JWT工具类

```java
public class JwtUtil {

    /**
     * 生成JWT令牌
     */
    public static String generateToken(Long userId, String secret, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 解析JWT令牌
     */
    public static Long getUserIdFromToken(String token, String secret) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
}
```

## 9. 集成方案

### 9.1 服务注册与发现

- 使用Nacos作为服务注册中心，用户服务启动时自动注册到Nacos
- 其他服务通过服务名调用用户服务

### 9.2 网关集成

- API网关通过Nacos发现用户服务
- 网关统一处理认证、限流、日志等横切关注点
- 路由规则配置：将`/user/**`路径路由到用户服务

### 9.3 订单服务集成

- 订单服务调用用户服务获取用户信息
- 用户服务提供用户认证和授权功能

### 9.4 支付服务集成

- 支付服务调用用户服务验证用户身份
- 用户服务提供用户信息查询接口

## 10. 部署方案

### 10.1 容器化部署

- 使用Docker容器化用户服务
- 配置Docker Compose实现多服务编排

### 10.2 集群部署

- 部署多个用户服务实例
- 通过Nacos实现服务发现和负载均衡

## 11. 注意事项

1. **密码安全**：密码必须加密存储，使用BCrypt等安全的加密算法
2. **SQL注入防护**：使用MyBatis的参数化查询，避免SQL注入
3. **XSS防护**：对用户输入进行过滤，防止XSS攻击
4. **CSRF防护**：实现CSRF Token验证
5. **接口限流**：对敏感接口进行限流，防止恶意请求
6. **数据脱敏**：对敏感数据（如手机号、邮箱）进行脱敏处理
7. **事务一致性**：确保关键操作的事务一致性
8. **缓存一致性**：确保缓存与数据库的数据一致性
9. **异常处理**：统一异常处理，避免敏感信息泄露
10. **日志记录**：详细记录操作日志，便于问题排查

## 12. 扩展计划

1. **支持第三方登录**：集成微信、支付宝等第三方登录
2. **用户画像**：建立用户画像系统，支持个性化推荐
3. **社交关系**：实现用户之间的关注、粉丝关系
4. **消息通知**：集成消息通知系统，及时通知用户重要事件
5. **多端适配**：支持PC、移动端、小程序等多端访问
6. **用户积分**：实现用户积分系统，鼓励用户活跃度
7. **会员体系**：建立会员等级制度，提供差异化服务
8. **数据统计**：实现用户行为数据统计和分析

---

**文档版本**：v1.0
**编写日期**：2026-02-26
**编写人**：System Designer