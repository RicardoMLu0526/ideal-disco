# 用户服务设计文档

## 1. 文档概述

### 1.1 文档目的
本文档详细描述用户服务的架构设计、核心功能、接口定义和集成方案，为用户服务的开发和集成提供完整的技术指导。

### 1.2 适用范围
- 系统架构师：用于整体架构设计和决策
- 后端开发人员：用于服务开发和接口实现
- 前端开发人员：用于理解后端服务调用关系
- 测试人员：用于设计集成测试用例
- 运维人员：用于理解服务依赖关系

### 1.3 文档版本
| 版本 | 日期 | 修改内容 | 作者 |
|:---|:---|:---|:---|
| v1.0 | 2026-02-26 | 初始版本 | System Designer |

## 2. 服务架构总览

### 2.1 服务拓扑图

```
                                    ┌─────────────────────────────────────────────────────────────┐
                                    │                        API Gateway                           │
                                    │                         (8080)                               │
                                    └─────────────────────────────────────────────────────────────┘
                                                              │
                    ┌─────────────────────────────────────────┼─────────────────────────────────────────┐
                    │                                         │                                         │
                    ▼                                         ▼                                         ▼
        ┌───────────────────────┐               ┌───────────────────────┐               ┌───────────────────────┐
        │      用户服务          │               │      商品服务          │               │      订单服务          │
        │    (user-service)     │               │  (product-service)    │               │  (order-service)      │
        │        (8081)         │               │        (8082)         │               │        (8083)         │
        │                       │               │                       │               │                       │
        │  - 用户注册/登录       │               │  - 商品管理            │               │  - 订单创建            │
        │  - 用户信息管理        │               │  - 库存管理            │               │  - 订单查询            │
        │  - 用户认证授权        │               │  - 商品搜索            │               │  - 订单状态管理        │
        │  - 收货地址管理        │               │  - 分类管理            │               │  - 订单取消/退款       │
        └───────────────────────┘               └───────────────────────┘               └───────────────────────┘
                    │                                         │                                         │
                    │                                         │                                         │
                    └─────────────────────────────────────────┼─────────────────────────────────────────┘
                                                              │
                                                              ▼
                                                ┌───────────────────────┐
                                                │      支付服务          │
                                                │  (payment-service)    │
                                                │        (8084)         │
                                                │                       │
                                                │  - 支付创建            │
                                                │  - 支付执行            │
                                                │  - 支付回调            │
                                                │  - 退款处理            │
                                                └───────────────────────┘
```

### 2.2 服务分层架构

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    接入层 (Access Layer)                             │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                              API Gateway (8080)                              │   │
│  │  职责：路由转发、认证鉴权、限流熔断、请求聚合、日志记录                         │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                          │
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    业务服务层 (Business Service Layer)              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │    用户服务      │  │    商品服务      │  │    订单服务      │  │    支付服务      │ │
│  │   (基础服务)     │  │   (基础服务)     │  │   (核心服务)     │  │   (核心服务)     │ │
│  │                 │  │                 │  │                 │  │                 │ │
│  │  用户管理       │  │  商品管理       │  │  订单管理       │  │  支付管理       │ │
│  │  认证授权       │  │  库存管理       │  │  订单编排       │  │  退款管理       │ │
│  │  地址管理       │  │  搜索服务       │  │  状态流转       │  │  支付回调       │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                          │
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    数据层 (Data Layer)                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   MySQL (用户)   │  │   MySQL (商品)   │  │   MySQL (订单)   │  │   MySQL (支付)   │ │
│  │   Redis (缓存)   │  │   Redis (库存)   │  │   Redis (订单)   │  │   Redis (支付)   │ │
│  │                  │  │   ES (搜索)      │  │                  │  │                  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

## 3. 服务详细定义

### 3.1 用户服务 (User Service)

#### 3.1.1 服务职责
| 职责 | 描述 | 优先级 |
|:---|:---|:---|
| 用户注册 | 新用户账号注册，支持手机号/邮箱注册 | P0 |
| 用户登录 | 用户身份认证，返回JWT Token | P0 |
| 用户信息管理 | 用户基本信息CRUD操作 | P0 |
| 认证授权 | Token验证、权限校验 | P0 |
| 收货地址管理 | 用户收货地址的增删改查 | P1 |
| 用户状态管理 | 用户账号状态管理（启用/禁用） | P1 |

#### 3.1.2 核心实体

```java
// 用户实体
public class User {
    private Long id;                    // 用户ID
    private String username;            // 用户名
    private String password;            // 密码（加密存储）
    private String phone;               // 手机号
    private String email;               // 邮箱
    private String nickname;            // 昵称
    private String avatar;              // 头像URL
    private Integer gender;             // 性别（0:未知,1:男,2:女）
    private Integer status;             // 状态：0-禁用，1-正常
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}

// 收货地址实体
public class Address {
    private Long id;                    // 地址ID
    private Long userId;                // 用户ID
    private String name;                // 收货人姓名
    private String phone;               // 收货人电话
    private String province;            // 省
    private String city;                // 市
    private String district;            // 区
    private String detail;              // 详细地址
    private Integer isDefault;          // 是否默认：0-否，1-是
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}
```

#### 3.1.3 对外接口定义

| 接口 | 方法 | 路径 | 描述 | 调用方 |
|:---|:---|:---|:---|:---|
| 用户注册 | POST | /api/auth/register | 用户注册 | 前端 |
| 用户登录 | POST | /api/auth/login | 用户登录认证 | 前端 |
| 获取用户信息 | GET | /api/users/{userId} | 获取用户详细信息 | 订单服务、前端 |
| 更新用户信息 | PUT | /api/users/{userId} | 更新用户信息 | 前端 |
| 验证Token | POST | /api/auth/validate | 验证JWT Token有效性 | 网关、其他服务 |
| 获取用户地址列表 | GET | /api/users/{userId}/addresses | 获取用户所有收货地址 | 订单服务、前端 |
| 获取地址详情 | GET | /api/users/addresses/{addressId} | 获取单个地址详情 | 订单服务、前端 |
| 添加收货地址 | POST | /api/users/{userId}/addresses | 添加收货地址 | 前端 |
| 更新收货地址 | PUT | /api/users/{userId}/addresses/{id} | 更新收货地址 | 前端 |
| 删除收货地址 | DELETE | /api/users/{userId}/addresses/{id} | 删除收货地址 | 前端 |
| 设置默认地址 | PUT | /api/users/{userId}/addresses/{id}/default | 设置默认地址 | 前端 |

## 4. 服务间关系与依赖

### 4.1 服务依赖矩阵

| 服务 | 用户服务 | 商品服务 | 订单服务 | 支付服务 | 被依赖次数 |
|:---|:---:|:---:|:---:|:---:|:---:|
| **用户服务** | - | - | ✓ | - | 1 |
| **商品服务** | - | - | ✓ | - | 1 |
| **订单服务** | ✓ | ✓ | - | ✓ | 2 |
| **支付服务** | - | - | ✓ | - | 1 |
| **依赖服务数** | 0 | 0 | 3 | 1 | - |

**说明**：
- ✓ 表示行服务依赖列服务
- 用户服务和商品服务为基础服务，不依赖其他业务服务
- 订单服务为核心编排服务，依赖用户、商品、支付三个服务
- 支付服务与订单服务存在**双向依赖**：
  - 订单服务 → 支付服务：创建支付记录、查询支付状态
  - 支付服务 → 订单服务：支付成功更新订单、支付失败取消订单、支付超时取消订单

### 4.2 服务调用关系图

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    服务调用关系                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

                              ┌─────────────────┐
                              │    订单服务      │
                              │  (编排中心)      │
                              └────────┬────────┘
                                       │
           ┌───────────────────────────┼───────────────────────────┐
           │                           │                           │
           │ 获取用户信息               │ 扣减/查询库存              │ 创建支付记录
           │ 验证用户状态               │ 查询商品信息               │ 查询支付状态
           │ 获取收货地址               │ 释放库存                   │
           ▼                           ▼                           ▼
    ┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐
    │    用户服务      │        │    商品服务      │        │    支付服务      │
    │                 │        │                 │        │                 │
    │  提供用户数据    │        │  提供商品数据    │        │  支付成功回调    │
    │  提供地址数据    │        │  提供库存数据    │        │  支付失败回调    │
    │                 │        │                 │        │  支付超时取消    │
    └─────────────────┘        └─────────────────┘        │       │         │
                                                          │       │         │
                                                          │       ▼         │
                                                          │  ┌─────────────┐ │
                                                          │  │ 更新订单状态 │ │
                                                          │  │ 取消订单     │ │
                                                          │  │ 释放库存     │ │
                                                          │  └─────────────┘ │
                                                          │       │         │
                                                          └───────┼─────────┘
                                                                  │
                                                                  ▼
                                                          ┌─────────────────┐
                                                          │    订单服务      │
                                                          │  (状态更新)      │
                                                          └─────────────────┘
```

### 4.3 数据流向图

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    数据流向分析                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

【用户数据流向】
用户服务 ─────────────────────────────────────────────────────────────────────────────▶
    │
    ├──▶ 订单服务：用户基本信息、收货地址信息
    │
    └──▶ 前端：用户个人中心数据

【商品数据流向】
商品服务 ─────────────────────────────────────────────────────────────────────────────▶
    │
    ├──▶ 订单服务：商品信息、库存信息
    │
    └──▶ 前端：商品列表、商品详情、搜索结果

【订单数据流向】
订单服务 ◀────────────────────────────────────────────────────────────────────────────▶
    │
    ├──▶ 用户服务：获取用户信息（读）
    │
    ├──▶ 商品服务：获取商品信息（读）、扣减库存（写）
    │
    ├──▶ 支付服务：创建支付（写）、获取支付状态（读）
    │
    └──▶ 前端：订单列表、订单详情

【支付数据流向】
支付服务 ◀────────────────────────────────────────────────────────────────────────────▶
    │
    ├──▶ 订单服务：创建支付记录（写）、更新订单状态（写）
    │
    └──▶ 第三方支付：支付请求、支付回调
```

## 5. 服务间接口详细设计

### 5.1 用户服务对外接口

#### 5.1.1 获取用户信息

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 获取用户信息 |
| 接口路径 | GET /api/users/{userId} |
| 调用方 | 订单服务、前端 |
| 接口描述 | 根据用户ID获取用户详细信息 |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| userId | Long | BIGINT | 非空，正整数 | 用户ID |

**请求头**：
| 参数名 | Java类型 | 约束 | 说明 |
|:---|:---|:---|:---|
| Authorization | String | 非空，格式：Bearer {token} | 认证令牌 |

**输出定义**：

**真实返回类型**：`Result<UserDTO>`

**响应DTO类**：
```java
// 统一响应包装类（泛型T支持任意类型）
public class Result<T> {
    private Integer code;        // 响应码，200-成功，400-参数错误，404-未找到，500-服务器错误
    private String message;      // 响应消息
    private T data;              // 数据对象（本接口T = UserDTO）
    private Long timestamp;      // 时间戳（毫秒）
}

// 用户信息DTO（单个对象）
public class UserDTO {
    private Long userId;         // 用户ID，BIGINT，非空
    private String username;     // 用户名，VARCHAR(50)，非空，字母数字下划线
    private String phone;        // 手机号，VARCHAR(20)，11位数字
    private String email;        // 邮箱，VARCHAR(100)，邮箱格式
    private String nickname;     // 昵称，VARCHAR(50)，可为空
    private String avatar;       // 头像URL，VARCHAR(255)，URL格式
    private Integer status;      // 状态，TINYINT，0-禁用，1-正常
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<UserDTO>                                                │
│  (本接口返回类型：单个用户对象)                                   │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                       │
│  message: "success"                                              │
│  timestamp: 1708867200000                                        │
│  data: UserDTO ──────────────────────────────────────────────┐  │
│         │ 这是一个单个对象，不是列表                           │  │
│         ▼                                                     │  │
│         ┌─────────────────────────────────────────────────┐   │  │
│         │ userId: 1001                                     │   │  │
│         │ username: "user001"                              │   │  │
│         │ phone: "13800138000"                              │   │  │
│         │ email: "user@example.com"                         │   │  │
│         │ nickname: "用户昵称"                               │   │  │
│         │ avatar: "https://example.com/avatar.jpg"          │   │  │
│         │ status: 1                                         │   │  │
│         └─────────────────────────────────────────────────┘   │  │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404, 500 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | UserDTO | 可为null | - | 用户信息对象 |
| data.userId | Long | 非空 | 正整数 | 用户唯一标识 |
| data.username | String | 非空 | 4-50字符，字母数字下划线 | 登录用户名 |
| data.phone | String | 可为null | 11位数字 | 手机号码 |
| data.email | String | 可为null | 邮箱格式，最大100字符 | 电子邮箱 |
| data.nickname | String | 可为null | 最大50字符 | 用户昵称 |
| data.avatar | String | 可为null | URL格式，最大255字符 | 头像图片地址 |
| data.status | Integer | 非空 | 0, 1 | 账号状态 |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1001,
    "username": "user001",
    "phone": "13800138000",
    "email": "user@example.com",
    "nickname": "用户昵称",
    "avatar": "https://example.com/avatar.jpg",
    "status": 1
  },
  "timestamp": 1708867200000
}
```

**错误响应**：
| HTTP状态码 | code | message | 场景 |
|:---|:---|:---|:---|
| 400 | 400 | 用户ID格式错误 | userId不是有效数字 |
| 404 | 404 | 用户不存在 | 用户ID不存在 |
| 401 | 401 | 未授权访问 | Token无效或过期 |

**Feign客户端定义**：
```java
@FeignClient(name = "user-service", path = "/api/users")
public interface UserClient {
    
    @GetMapping("/{userId}")
    Result<UserDTO> getUserById(@PathVariable("userId") Long userId);
}
```

#### 5.1.2 获取用户收货地址列表

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 获取用户收货地址列表 |
| 接口路径 | GET /api/users/{userId}/addresses |
| 调用方 | 订单服务、前端 |
| 接口描述 | 获取指定用户的所有收货地址（返回地址列表） |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| userId | Long | BIGINT | 非空，正整数 | 用户ID |

**输出定义**：

**真实返回类型**：`Result<List<AddressDTO>>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;                // 数据对象（本接口T = List<AddressDTO>）
    private Long timestamp;        // 时间戳（毫秒）
}

// 单个地址DTO（用于列表中的每一项）
public class AddressDTO {
    private Long addressId;        // 地址ID，BIGINT，非空
    private Long userId;           // 用户ID，BIGINT，非空
    private String receiverName;   // 收货人姓名，VARCHAR(50)，非空
    private String receiverPhone;  // 收货人电话，VARCHAR(20)，非空，11位数字
    private String province;       // 省份，VARCHAR(50)，非空
    private String city;           // 城市，VARCHAR(50)，非空
    private String district;       // 区县，VARCHAR(50)，非空
    private String detailAddress;  // 详细地址，VARCHAR(200)，非空
    private Integer isDefault;     // 是否默认，TINYINT，0-否，1-是
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<List<AddressDTO>>                                       │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                       │
│  message: "success"                                              │
│  timestamp: 1708867200000                                        │
│  data: List<AddressDTO> ─────────────────────────────────────┐  │
│         ┌─────────────────────────────────────────────────────┐ │
│         │ [0] AddressDTO                                      │ │
│         │     ├── addressId: 1                                │ │
│         │     ├── userId: 1001                                │ │
│         │     ├── receiverName: "张三"                         │ │
│         │     ├── receiverPhone: "13800138000"                │ │
│         │     ├── province: "广东省"                           │ │
│         │     ├── city: "深圳市"                               │ │
│         │     ├── district: "南山区"                           │ │
│         │     ├── detailAddress: "科技园路1号"                  │ │
│         │     └── isDefault: 1                                 │ │
│         ├─────────────────────────────────────────────────────┤ │
│         │ [1] AddressDTO                                      │ │
│         │     ├── addressId: 2                                │ │
│         │     ├── userId: 1001                                │ │
│         │     ├── receiverName: "李四"                         │ │
│         │     ├── receiverPhone: "13900139000"                │ │
│         │     ├── province: "广东省"                           │ │
│         │     ├── city: "广州市"                               │ │
│         │     ├── district: "天河区"                           │ │
│         │     ├── detailAddress: "天河路100号"                 │ │
│         │     └── isDefault: 0                                 │ │
│         └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **List\<AddressDTO\>** | 可为空列表 | - | **地址列表（可包含0-N个地址）** |
| data[].addressId | Long | 非空 | 正整数 | 地址唯一标识 |
| data[].userId | Long | 非空 | 正整数 | 所属用户ID |
| data[].receiverName | String | 非空 | 1-50字符 | 收货人姓名 |
| data[].receiverPhone | String | 非空 | 11位数字 | 收货人手机号 |
| data[].province | String | 非空 | 最大50字符 | 省份名称 |
| data[].city | String | 非空 | 最大50字符 | 城市名称 |
| data[].district | String | 非空 | 最大50字符 | 区县名称 |
| data[].detailAddress | String | 非空 | 1-200字符 | 详细地址 |
| data[].isDefault | Integer | 非空 | 0, 1 | 是否默认地址 |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例（包含多个地址）**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "addressId": 1,
      "userId": 1001,
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "province": "广东省",
      "city": "深圳市",
      "district": "南山区",
      "detailAddress": "科技园路1号",
      "isDefault": 1
    },
    {
      "addressId": 2,
      "userId": 1001,
      "receiverName": "李四",
      "receiverPhone": "13900139000",
      "province": "广东省",
      "city": "广州市",
      "district": "天河区",
      "detailAddress": "天河路100号",
      "isDefault": 0
    }
  ],
  "timestamp": 1708867200000
}
```

**响应示例（空列表 - 用户无地址）**：
```json
{
  "code": 200,
  "message": "success",
  "data": [],
  "timestamp": 1708867200000
}
```

#### 5.1.3 获取单个收货地址

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 获取单个收货地址 |
| 接口路径 | GET /api/users/addresses/{addressId} |
| 调用方 | 订单服务、前端 |
| 接口描述 | 根据地址ID获取收货地址详情（返回单个地址对象） |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| addressId | Long | BIGINT | 非空，正整数 | 地址ID |

**输出定义**：

**真实返回类型**：`Result<AddressDTO>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;                // 数据对象（本接口T = AddressDTO）
    private Long timestamp;        // 时间戳（毫秒）
}

// 单个地址DTO
public class AddressDTO {
    private Long addressId;        // 地址ID，BIGINT，非空
    private Long userId;           // 用户ID，BIGINT，非空
    private String receiverName;   // 收货人姓名，VARCHAR(50)，非空
    private String receiverPhone;  // 收货人电话，VARCHAR(20)，非空
    private String province;       // 省份，VARCHAR(50)，非空
    private String city;           // 城市，VARCHAR(50)，非空
    private String district;       // 区县，VARCHAR(50)，非空
    private String detailAddress;  // 详细地址，VARCHAR(200)，非空
    private Integer isDefault;     // 是否默认，TINYINT，0-否，1-是
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<AddressDTO>                                             │
│  (本接口返回类型：单个地址对象)                                   │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                       │
│  message: "success"                                              │
│  timestamp: 1708867200000                                        │
│  data: AddressDTO ───────────────────────────────────────────┐  │
│         │ 这是一个单个对象，不是列表                           │  │
│         ▼                                                     │  │
│         ┌─────────────────────────────────────────────────┐   │  │
│         │ addressId: 1                                     │   │  │
│         │ userId: 1001                                      │   │  │
│         │ receiverName: "张三"                               │   │  │
│         │ receiverPhone: "13800138000"                      │   │  │
│         │ province: "广东省"                                 │   │  │
│         │ city: "深圳市"                                     │   │  │
│         │ district: "南山区"                                 │   │  │
│         │ detailAddress: "科技园路1号"                        │   │  │
│         │ isDefault: 1                                       │   │  │
│         └─────────────────────────────────────────────────┘   │  │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **AddressDTO** | 可为null | - | **单个地址对象** |
| data.addressId | Long | 非空 | 正整数 | 地址唯一标识 |
| data.userId | Long | 非空 | 正整数 | 所属用户ID |
| data.receiverName | String | 非空 | 1-50字符 | 收货人姓名 |
| data.receiverPhone | String | 非空 | 11位数字 | 收货人手机号 |
| data.province | String | 非空 | 最大50字符 | 省份名称 |
| data.city | String | 非空 | 最大50字符 | 城市名称 |
| data.district | String | 非空 | 最大50字符 | 区县名称 |
| data.detailAddress | String | 非空 | 1-200字符 | 详细地址 |
| data.isDefault | Integer | 非空 | 0, 1 | 是否默认地址 |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**Feign客户端定义**：
```java
@FeignClient(name = "user-service", path = "/api/users")
public interface UserClient {
    
    @GetMapping("/{id}")
    Result<UserDTO> getUserById(@PathVariable("id") Long id);
    
    @GetMapping("/{userId}/addresses")
    Result<List<AddressDTO>> getUserAddresses(@PathVariable("userId") Long userId);
    
    @GetMapping("/addresses/{id}")
    Result<AddressDTO> getAddressById(@PathVariable("id") Long id);
}
```

## 6. 技术实现

### 6.1 技术栈

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

### 6.2 目录结构

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

### 6.3 核心配置

#### 6.3.1 应用配置 (`application.yml`)

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

#### 6.3.2 安全配置

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

### 6.4 核心代码

#### 6.4.1 认证服务

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

#### 6.4.2 用户服务

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

#### 6.4.3 地址服务

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

#### 6.4.4 JWT工具类

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

## 7. 集成方案

### 7.1 服务注册与发现

- 使用Nacos作为服务注册中心，用户服务启动时自动注册到Nacos
- 其他服务通过服务名调用用户服务

### 7.2 网关集成

- API网关通过Nacos发现用户服务
- 网关统一处理认证、限流、日志等横切关注点
- 路由规则配置：将`/user/**`路径路由到用户服务

### 7.3 订单服务集成

- 订单服务调用用户服务获取用户信息
- 用户服务提供用户认证和授权功能

### 7.4 支付服务集成

- 支付服务调用用户服务验证用户身份
- 用户服务提供用户信息查询接口

## 8. 部署方案

### 8.1 容器化部署

- 使用Docker容器化用户服务
- 配置Docker Compose实现多服务编排

### 8.2 集群部署

- 部署多个用户服务实例
- 通过Nacos实现服务发现和负载均衡

## 9. 注意事项

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

## 10. 扩展计划

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
