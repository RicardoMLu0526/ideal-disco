# 微服务集成架构设计文档

## 1. 文档概述

### 1.1 文档目的
本文档详细描述用户服务、支付服务、产品服务和订单服务之间的相互关系、接口设计和集成方案，为自顶而下的系统设计提供完整的技术指导。

### 1.2 适用范围
- 系统架构师：用于整体架构设计和决策
- 后端开发人员：用于服务开发和接口实现
- 前端开发人员：用于理解后端服务调用关系
- 测试人员：用于设计集成测试用例
- 运维人员：用于理解服务依赖关系

### 1.3 文档版本
| 版本 | 日期 | 修改内容 | 作者 |
|:---|:---|:---|:---|
| v1.0 | 2026-02-25 | 初始版本 | System Architect |

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
    private Long userId;              // 用户ID
    private String username;            // 用户名
    private String password;            // 密码（加密存储）
    private String phone;               // 手机号
    private String email;               // 邮箱
    private String nickname;            // 昵称
    private String avatar;              // 头像URL
    private Integer status;             // 状态：0-禁用，1-正常
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
}

// 收货地址实体
public class UserAddress {
    private Long addressId;             // 地址ID
    private Long userId;                // 用户ID
    private String receiverName;        // 收货人姓名
    private String receiverPhone;       // 收货人电话
    private String province;            // 省
    private String city;                // 市
    private String district;            // 区
    private String detailAddress;       // 详细地址
    private Integer isDefault;          // 是否默认：0-否，1-是
}
```

#### 3.1.3 对外接口定义

| 接口 | 方法 | 路径 | 描述 | 调用方 |
|:---|:---|:---|:---|:---|
| 用户注册 | POST | /api/users/register | 用户注册 | 前端 |
| 用户登录 | POST | /api/users/login | 用户登录认证 | 前端 |
| 获取用户信息 | GET | /api/users/{userId} | 获取用户详细信息 | 订单服务、前端 |
| 更新用户信息 | PUT | /api/users/{userId} | 更新用户信息 | 前端 |
| 验证Token | POST | /api/users/validate | 验证JWT Token有效性 | 网关、其他服务 |
| 获取用户地址列表 | GET | /api/users/{userId}/addresses | 获取用户所有收货地址 | 订单服务、前端 |
| 获取地址详情 | GET | /api/users/addresses/{addressId} | 获取单个地址详情 | 订单服务、前端 |

### 3.2 商品服务 (Product Service)

#### 3.2.1 服务职责
| 职责 | 描述 | 优先级 |
|:---|:---|:---|
| 商品管理 | 商品信息的CRUD操作 | P0 |
| 库存管理 | 商品库存的增减、预扣、回滚 | P0 |
| 商品搜索 | 基于ES的商品搜索功能 | P0 |
| 分类管理 | 商品分类的层级管理 | P1 |
| 商品状态管理 | 商品上下架管理 | P1 |

#### 3.2.2 核心实体

```java
// 商品实体
public class Product {
    private Long id;                    // 商品ID
    private String name;                // 商品名称
    private String description;         // 商品描述
    private BigDecimal price;           // 商品价格
    private BigDecimal originalPrice;   // 原价
    private Integer stock;              // 库存数量
    private Integer sales;              // 销量
    private Long categoryId;            // 分类ID
    private String mainImage;           // 主图URL
    private List<String> subImages;     // 子图URL列表
    private Integer status;             // 状态：0-下架，1-上架
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
}

// 商品分类实体
public class Category {
    private Long id;                    // 分类ID
    private String name;                // 分类名称
    private Long parentId;              // 父分类ID
    private Integer level;              // 层级
    private Integer sortOrder;          // 排序
}

// 库存流水实体
public class StockLog {
    private Long id;                    // 流水ID
    private Long productId;             // 商品ID
    private Integer quantity;           // 变动数量（正数增加，负数减少）
    private String type;                // 类型：DEDUCT-扣减, RETURN-归还, PRE_DEDUCT-预扣
    private String orderNo;             // 关联订单号
    private LocalDateTime createTime;   // 创建时间
}
```

#### 3.2.3 对外接口定义

| 接口 | 方法 | 路径 | 描述 | 调用方 |
|:---|:---|:---|:---|:---|
| 商品列表 | GET | /api/products | 获取商品列表（分页） | 前端 |
| 商品详情 | GET | /api/products/{id} | 获取商品详细信息 | 订单服务、前端 |
| 商品搜索 | GET | /api/products/search | 关键字搜索商品 | 前端 |
| 扣减库存 | POST | /api/products/stock/deduct | 扣减商品库存 | 订单服务 |
| 预扣库存 | POST | /api/products/stock/pre-deduct | 预扣库存（创建订单时） | 订单服务 |
| 释放库存 | POST | /api/products/stock/release | 释放预扣库存（取消订单时） | 订单服务 |
| 查询库存 | GET | /api/products/{id}/stock | 查询商品实时库存 | 订单服务、前端 |
| 分类列表 | GET | /api/categories | 获取商品分类树 | 前端 |

### 3.3 订单服务 (Order Service)

#### 3.3.1 服务职责
| 职责 | 描述 | 优先级 |
|:---|:---|:---|
| 订单创建 | 创建订单，协调用户、商品、支付服务 | P0 |
| 订单查询 | 订单列表、详情查询 | P0 |
| 订单状态管理 | 订单状态流转管理 | P0 |
| 订单取消 | 取消订单，释放库存 | P0 |
| 订单编排 | 协调多个服务完成订单流程 | P0 |

#### 3.3.2 核心实体

```java
// 订单实体
public class Order {
    private Long id;                    // 订单ID
    private String orderNo;             // 订单编号
    private Long userId;                // 用户ID
    private BigDecimal totalAmount;     // 订单总金额
    private BigDecimal payAmount;       // 实付金额
    private BigDecimal freightAmount;   // 运费
    private BigDecimal discountAmount;  // 优惠金额
    private Integer status;             // 订单状态
    private String receiverName;        // 收货人姓名
    private String receiverPhone;       // 收货人电话
    private String receiverAddress;     // 收货地址
    private Long paymentId;             // 支付ID
    private Integer paymentStatus;      // 支付状态
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime payTime;      // 支付时间
    private LocalDateTime deliveryTime; // 发货时间
    private LocalDateTime receiveTime;  // 收货时间
}

// 订单项实体
public class OrderItem {
    private Long id;                    // 订单项ID
    private Long orderId;               // 订单ID
    private Long productId;             // 商品ID
    private String productName;         // 商品名称
    private String productImage;        // 商品图片
    private BigDecimal price;           // 商品单价
    private Integer quantity;           // 购买数量
    private BigDecimal totalAmount;     // 小计金额
}

// 订单状态枚举
public enum OrderStatus {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    DELIVERED(3, "已送达"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    REFUNDING(6, "退款中"),
    REFUNDED(7, "已退款");
}
```

#### 3.3.3 对外接口定义

| 接口 | 方法 | 路径 | 描述 | 调用方 |
|:---|:---|:---|:---|:---|
| 创建订单 | POST | /api/orders | 创建新订单 | 前端 |
| 订单列表 | GET | /api/orders | 获取用户订单列表 | 前端 |
| 订单详情 | GET | /api/orders/{orderNo} | 获取订单详细信息 | 支付服务、前端 |
| 取消订单 | POST | /api/orders/{orderNo}/cancel | 取消订单 | 前端 |
| 更新订单状态 | PUT | /api/orders/{orderNo}/status | 更新订单状态 | 支付服务 |
| 订单支付成功回调 | POST | /api/orders/{orderNo}/paid | 支付成功回调 | 支付服务 |
| 确认收货 | POST | /api/orders/{orderNo}/receive | 确认收货 | 前端 |

### 3.4 支付服务 (Payment Service)

#### 3.4.1 服务职责
| 职责 | 描述 | 优先级 |
|:---|:---|:---|
| 支付创建 | 创建支付记录，生成支付参数 | P0 |
| 支付执行 | 调用第三方支付接口 | P0 |
| 支付回调 | 处理第三方支付回调通知 | P0 |
| 退款处理 | 处理退款请求 | P0 |
| 支付查询 | 查询支付状态 | P1 |

#### 3.4.2 核心实体

```java
// 支付记录实体
public class Payment {
    private Long id;                    // 支付ID
    private String paymentNo;           // 支付流水号
    private String orderNo;             // 订单编号
    private Long userId;                // 用户ID
    private BigDecimal amount;          // 支付金额
    private Integer paymentMethod;      // 支付方式：1-微信，2-支付宝
    private Integer status;             // 支付状态：0-待支付，1-支付成功，2-支付失败
    private String thirdPartyNo;        // 第三方支付流水号
    private LocalDateTime payTime;      // 支付时间
    private LocalDateTime createTime;   // 创建时间
}

// 退款记录实体
public class Refund {
    private Long id;                    // 退款ID
    private String refundNo;            // 退款流水号
    private String paymentNo;           // 支付流水号
    private String orderNo;             // 订单编号
    private BigDecimal amount;          // 退款金额
    private String reason;              // 退款原因
    private Integer status;             // 退款状态：0-处理中，1-成功，2-失败
    private LocalDateTime refundTime;   // 退款时间
    private LocalDateTime createTime;   // 创建时间
}

// 支付状态枚举
public enum PaymentStatus {
    PENDING(0, "待支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    CLOSED(3, "已关闭");
}
```

#### 3.4.3 对外接口定义

| 接口 | 方法 | 路径 | 描述 | 调用方 |
|:---|:---|:---|:---|:---|
| 创建支付 | POST | /api/payments | 创建支付记录 | 订单服务 |
| 发起支付 | POST | /api/payments/{paymentNo}/pay | 发起支付请求 | 前端 |
| 支付成功回调 | POST | /api/payments/callback/{channel}/success | 第三方支付成功回调 | 第三方支付 |
| 支付失败回调 | POST | /api/payments/callback/{channel}/fail | 第三方支付失败回调 | 第三方支付 |
| 查询支付状态 | GET | /api/payments/{paymentNo} | 查询支付状态 | 订单服务、前端 |
| 申请退款 | POST | /api/payments/refund | 申请退款 | 订单服务 |
| 退款回调 | POST | /api/payments/refund/callback/{channel} | 第三方退款回调 | 第三方支付 |
| 支付超时取消 | POST | /api/payments/{paymentNo}/timeout | 支付超时自动取消 | 定时任务 |

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

**关键调用说明**：
1. **订单服务 → 支付服务**：
   - 创建支付记录：订单创建时调用
   - 查询支付状态：用户查询订单时调用

2. **支付服务 → 订单服务**（重要！）：
   - 支付成功：更新订单状态为"已支付"，确认扣减库存
   - 支付失败：取消订单，释放预扣库存
   - 支付超时：自动取消订单，释放预扣库存

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

### 5.2 商品服务对外接口

#### 5.2.1 获取商品详情

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 获取商品详情 |
| 接口路径 | GET /api/products/{productId} |
| 调用方 | 订单服务、前端 |
| 接口描述 | 根据商品ID获取商品详细信息（返回单个商品对象） |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| productId | Long | BIGINT | 非空，正整数 | 商品ID |

**输出定义**：

**真实返回类型**：`Result<ProductDTO>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;                // 数据对象（本接口T = ProductDTO）
    private Long timestamp;        // 时间戳（毫秒）
}

// 商品信息DTO（单个对象）
public class ProductDTO {
    private Long productId;        // 商品ID，BIGINT，非空
    private String name;           // 商品名称，VARCHAR(200)，非空
    private String description;    // 商品描述，TEXT，可为空
    private BigDecimal price;      // 商品价格，DECIMAL(10,2)，非空，>=0
    private BigDecimal originalPrice; // 原价，DECIMAL(10,2)，可为空，>=price
    private Integer stock;         // 库存数量，INT，非空，>=0
    private Integer sales;         // 销量，INT，非空，>=0
    private String mainImage;      // 主图URL，VARCHAR(255)，可为空
    private Integer status;        // 状态，TINYINT，0-下架，1-上架
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<ProductDTO>                                             │
│  (本接口返回类型：单个商品对象)                                   │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                       │
│  message: "success"                                              │
│  timestamp: 1708867200000                                        │
│  data: ProductDTO ───────────────────────────────────────────┐  │
│         │ 这是一个单个对象，不是列表                           │  │
│         ▼                                                     │  │
│         ┌─────────────────────────────────────────────────┐   │  │
│         │ productId: 1001                                  │   │  │
│         │ name: "iPhone 15 Pro"                             │   │  │
│         │ description: "最新款iPhone"                        │   │  │
│         │ price: 8999.00                                    │   │  │
│         │ originalPrice: 9999.00                            │   │  │
│         │ stock: 100                                        │   │  │
│         │ sales: 50                                         │   │  │
│         │ mainImage: "https://example.com/product.jpg"      │   │  │
│         │ status: 1                                         │   │  │
│         └─────────────────────────────────────────────────┘   │  │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **ProductDTO** | 可为null | - | **单个商品对象** |
| data.productId | Long | 非空 | 正整数 | 商品唯一标识 |
| data.name | String | 非空 | 1-200字符 | 商品名称 |
| data.description | String | 可为null | 最大65535字符 | 商品详细描述 |
| data.price | BigDecimal | 非空 | >=0，最多2位小数 | 当前销售价格 |
| data.originalPrice | BigDecimal | 可为null | >=price，最多2位小数 | 商品原价 |
| data.stock | Integer | 非空 | >=0 | 当前库存数量 |
| data.sales | Integer | 非空 | >=0 | 累计销售数量 |
| data.mainImage | String | 可为null | URL格式，最大255字符 | 商品主图地址 |
| data.status | Integer | 非空 | 0, 1 | 商品上下架状态 |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productId": 1001,
    "name": "iPhone 15 Pro",
    "description": "最新款iPhone",
    "price": 8999.00,
    "originalPrice": 9999.00,
    "stock": 100,
    "sales": 50,
    "mainImage": "https://example.com/product.jpg",
    "status": 1
  },
  "timestamp": 1708867200000
}
```

#### 5.2.2 查询商品库存

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 查询商品库存 |
| 接口路径 | GET /api/products/{productId}/stock |
| 调用方 | 订单服务、前端 |
| 接口描述 | 查询商品实时库存数量（返回整数） |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| productId | Long | BIGINT | 非空，正整数 | 商品ID |

**输出定义**：

**真实返回类型**：`Result<Integer>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;                // 数据对象（本接口T = Integer）
    private Long timestamp;        // 时间戳（毫秒）
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<Integer>                                                │
│  (本接口返回类型：整数 - 库存数量)                                │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                       │
│  message: "success"                                              │
│  timestamp: 1708867200000                                        │
│  data: 100                                                       │
│         ↑ 这是一个简单的整数，不是对象                           │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **Integer** | 非空 | >=0 | **库存数量（简单整数）** |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": 100,
  "timestamp": 1708867200000
}
```

#### 5.2.3 预扣库存

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 预扣库存 |
| 接口路径 | **POST /api/products/stock/pre-deduct** |
| 调用方 | 订单服务 |
| 接口描述 | 创建订单时预扣库存，防止超卖 |

> **设计说明**：虽然预扣库存和确认扣减的请求DTO结构相同，但通过**不同的接口路径**区分：
> - 预扣：`/stock/pre-deduct`（创建订单时调用）
> - 实扣：`/stock/deduct`（支付成功后调用）
> 
> 服务端根据接口路径判断操作类型，执行不同的业务逻辑。

**输入定义**：

**请求DTO类**：
```java
public class StockDeductRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;        // 商品ID，BIGINT，非空
    
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;      // 扣减数量，INT，非空，>=1
    
    @NotBlank(message = "订单号不能为空")
    @Pattern(regexp = "^ORD\\d{17}$", message = "订单号格式错误")
    private String orderNo;        // 订单编号，VARCHAR(50)，非空，格式：ORD+17位数字
}
```

**请求字段详细说明**：
| 字段名 | Java类型 | 数据库类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|:---|
| productId | Long | BIGINT | 非空 | 正整数 | 商品唯一标识 |
| quantity | Integer | INT | 非空 | >=1 | 预扣库存数量 |
| orderNo | String | VARCHAR(50) | 非空 | ORD+17位数字 | 订单编号 |

**请求示例**：
```json
{
  "productId": 1001,
  "quantity": 2,
  "orderNo": "ORD202602250001"
}
```

**输出定义**：

**真实返回类型**：`Result<Void>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;                // 数据对象（本接口T = Void，无数据）
    private Long timestamp;        // 时间戳（毫秒）
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<Void>                                                   │
│  (本接口返回类型：无数据，仅返回操作结果状态)                      │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                       │
│  message: "预扣库存成功"                                          │
│  timestamp: 1708867200000                                        │
│  data: null                                                      │
│         ↑ 无返回数据，仅通过code和message表示操作结果             │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 500 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **Void** | 固定为null | - | **无返回数据** |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例（成功）**：
```json
{
  "code": 200,
  "message": "预扣库存成功",
  "data": null,
  "timestamp": 1708867200000
}
```

**响应示例（失败-库存不足）**：
```json
{
  "code": 500,
  "message": "库存不足，当前库存：1，需要：2",
  "data": null,
  "timestamp": 1708867200000
}
```

**业务逻辑**：
1. 检查库存是否充足
2. 使用Redis原子操作预扣库存
3. 记录库存流水（类型：PRE_DEDUCT）
4. 返回预扣结果

#### 5.2.4 确认扣减库存

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 确认扣减库存 |
| 接口路径 | **POST /api/products/stock/deduct** |
| 调用方 | 订单服务 |
| 接口描述 | 支付成功后确认扣减库存 |

> **设计说明**：虽然预扣库存和确认扣减的请求DTO结构相同，但通过**不同的接口路径**区分：
> - 预扣：`/stock/pre-deduct`（创建订单时调用）
> - 实扣：`/stock/deduct`（支付成功后调用）
> 
> 服务端根据接口路径判断操作类型，执行不同的业务逻辑。

**输入定义**：

**请求DTO类**：
```java
public class StockDeductRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;        // 商品ID，BIGINT，非空
    
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;      // 扣减数量，INT，非空，>=1
    
    @NotBlank(message = "订单号不能为空")
    @Pattern(regexp = "^ORD\\d{17}$", message = "订单号格式错误")
    private String orderNo;        // 订单编号，VARCHAR(50)，非空
}
```

**请求字段详细说明**：
| 字段名 | Java类型 | 数据库类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|:---|
| productId | Long | BIGINT | 非空 | 正整数 | 商品唯一标识 |
| quantity | Integer | INT | 非空 | >=1 | 确认扣减数量 |
| orderNo | String | VARCHAR(50) | 非空 | ORD+17位数字 | 订单编号 |

**请求示例**：
```json
{
  "productId": 1001,
  "quantity": 2,
  "orderNo": "ORD202602250001"
}
```

**输出定义**：

**真实返回类型**：`Result<Void>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;               // 数据对象（本接口T = Void，无数据）
    private Long timestamp;        // 时间戳（毫秒）
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<Void>                                                   │
│  (本接口返回类型：无数据，仅返回操作结果状态)                      │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                     │
│  message: "确认扣减成功"                                        │
│  timestamp: 1708867200000                                      │
│  data: null                                                    │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **Void** | 固定为null | - | **无返回数据** |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "确认扣减成功",
  "data": null,
  "timestamp": 1708867200000
}
```

#### 5.2.5 释放库存

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 释放库存 |
| 接口路径 | POST /api/products/stock/release |
| 调用方 | 订单服务 |
| 接口描述 | 订单取消或支付失败时释放预扣库存 |

**输入定义**：

**请求DTO类**：
```java
public class StockReleaseRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;        // 商品ID，BIGINT，非空
    
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;      // 释放数量，INT，非空，>=1
    
    @NotBlank(message = "订单号不能为空")
    @Pattern(regexp = "^ORD\\d{17}$", message = "订单号格式错误")
    private String orderNo;        // 订单编号，VARCHAR(50)，非空
}
```

**请求字段详细说明**：
| 字段名 | Java类型 | 数据库类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|:---|
| productId | Long | BIGINT | 非空 | 正整数 | 商品唯一标识 |
| quantity | Integer | INT | 非空 | >=1 | 释放库存数量 |
| orderNo | String | VARCHAR(50) | 非空 | ORD+17位数字 | 订单编号 |

**请求示例**：
```json
{
  "productId": 1001,
  "quantity": 2,
  "orderNo": "ORD202602250001"
}
```

**输出定义**：

**真实返回类型**：`Result<Void>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;               // 数据对象（本接口T = Void，无数据）
    private Long timestamp;        // 时间戳（毫秒）
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<Void>                                                   │
│  (本接口返回类型：无数据，仅返回操作结果状态)                      │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                     │
│  message: "释放库存成功"                                        │
│  timestamp: 1708867200000                                      │
│  data: null                                                    │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **Void** | 固定为null | - | **无返回数据** |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "释放库存成功",
  "data": null,
  "timestamp": 1708867200000
}
```

**业务逻辑**：
1. 根据订单号查找预扣记录
2. 释放预扣的库存
3. 记录库存流水（类型：RETURN）
4. 返回释放结果

#### 5.2.6 批量获取商品信息

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 批量获取商品信息 |
| 接口路径 | POST /api/products/batch/info |
| 调用方 | 订单服务 |
| 接口描述 | 根据商品ID列表批量获取商品信息 |

**输入定义**：

**请求DTO类**：
```java
@RequestBody
List<@NotNull Long> productIds;    // 商品ID列表，非空，最多100个ID
```

**请求字段详细说明**：
| 字段名 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| productIds | List\<Long\> | 非空，1-100个元素 | 每个元素为正整数 | 商品ID列表 |

**请求示例**：
```json
[1001, 1002, 1003]
```

**输出定义**：

**真实返回类型**：`Result<List<ProductSimpleDTO>>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;              // 数据对象（本接口T = List<ProductSimpleDTO>）
    private Long timestamp;        // 时间戳（毫秒）
}

// 简洁商品DTO（用于列表）
public class ProductSimpleDTO {
    private Long productId;        // 商品ID，BIGINT，非空
    private String name;           // 商品名称，VARCHAR(200)，非空
    private BigDecimal price;      // 商品价格，DECIMAL(10,2)，非空
    private Integer stock;         // 库存数量，INT，非空
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<List<ProductSimpleDTO>>                                  │
│  (本接口返回类型：商品列表)                                       │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                     │
│  message: "success"                                            │
│  timestamp: 1708867200000                                      │
│  data: List<ProductSimpleDTO> ──────────────────────────────┐  │
│         ┌─────────────────────────────────────────────────┐   │  │
│         │ [0] {productId:1, name:"iPhone 15", price:...}  │   │  │
│         │ [1] {productId:2, name:"iPhone 14", price:...}  │   │  │
│         └─────────────────────────────────────────────────┘   │  │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **List\<ProductSimpleDTO\>** | 可为空列表 | - | **商品信息列表** |
| data[].productId | Long | 非空 | 正整数 | 商品唯一标识 |
| data[].name | String | 非空 | 1-200字符 | 商品名称 |
| data[].price | BigDecimal | 非空 | >=0，最多2位小数 | 商品价格 |
| data[].stock | Integer | 非空 | >=0 | 库存数量 |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1001,
      "name": "iPhone 15 Pro",
      "price": 8999.00,
      "stock": 100
    },
    {
      "id": 1002,
      "name": "iPhone 15",
      "price": 6999.00,
      "stock": 50
    }
  ],
  "timestamp": 1708867200000
}
```

**Feign客户端定义**：
```java
@FeignClient(name = "product-service", path = "/api/products")
public interface ProductClient {
    
    @GetMapping("/{id}")
    Result<ProductDTO> getProductById(@PathVariable("id") Long id);
    
    @GetMapping("/{id}/stock")
    Result<Integer> getStock(@PathVariable("id") Long id);
    
    @PostMapping("/stock/pre-deduct")
    Result<Void> preDeductStock(@RequestBody StockDeductRequest request);
    
    @PostMapping("/stock/deduct")
    Result<Void> deductStock(@RequestBody StockDeductRequest request);
    
    @PostMapping("/stock/release")
    Result<Void> releaseStock(@RequestBody StockReleaseRequest request);
    
    @PostMapping("/batch/info")
    Result<List<ProductSimpleDTO>> batchGetProducts(@RequestBody List<Long> productIds);
}

### 5.3 订单服务对外接口

#### 5.3.1 创建订单

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 创建订单 |
| 接口路径 | POST /api/orders |
| 调用方 | 前端 |
| 接口描述 | 创建新订单，预扣库存，创建支付记录 |

**输入定义**：

**请求DTO类**：
```java
public class OrderCreateRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;                        // 用户ID，BIGINT，非空
    
    @NotNull(message = "地址ID不能为空")
    private Long addressId;                     // 收货地址ID，BIGINT，非空
    
    @NotEmpty(message = "订单项不能为空")
    @Size(max = 50, message = "订单项最多50个")
    private List<@Valid OrderItemRequest> items; // 订单项列表，非空，最多50项
    
    @Size(max = 200, message = "备注最多200字符")
    private String remark;                      // 订单备注，VARCHAR(200)，可为空
}

public class OrderItemRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;                     // 商品ID，BIGINT，非空
    
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    @Max(value = 99, message = "单个商品最多购买99件")
    private Integer quantity;                   // 购买数量，INT，非空，1-99
}
```

**请求字段详细说明**：
| 字段名 | Java类型 | 数据库类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|:---|
| userId | Long | BIGINT | 非空 | 正整数 | 用户唯一标识 |
| addressId | Long | BIGINT | 非空 | 正整数 | 收货地址ID |
| items | List\<OrderItemRequest\> | - | 非空，1-50项 | - | 订单商品列表 |
| items[].productId | Long | BIGINT | 非空 | 正整数 | 商品ID |
| items[].quantity | Integer | INT | 非空 | 1-99 | 购买数量 |
| remark | String | VARCHAR(200) | 可为空 | 最大200字符 | 订单备注 |

**请求示例**：
```json
{
  "userId": 1001,
  "addressId": 1,
  "items": [
    {
      "productId": 1001,
      "quantity": 2
    },
    {
      "productId": 1002,
      "quantity": 1
    }
  ],
  "remark": "备注信息"
}
```

**输出定义**：

**真实返回类型**：`Result<OrderCreateDTO>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;              // 数据对象（本接口T = OrderCreateDTO）
    private Long timestamp;        // 时间戳（毫秒）
}

// 订单创建结果DTO
public class OrderCreateDTO {
    private String orderNo;                     // 订单编号，VARCHAR(50)，非空，格式：ORD+17位数字
    private BigDecimal totalAmount;             // 订单总金额，DECIMAL(10,2)，非空
    private BigDecimal payAmount;               // 实付金额，DECIMAL(10,2)，非空
    private Integer status;                     // 订单状态，TINYINT，非空，0-待支付
    private String paymentNo;                   // 支付流水号，VARCHAR(50)，非空，格式：PAY+17位数字
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<OrderCreateDTO>                                          │
│  (本接口返回类型：订单创建结果)                                   │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                     │
│  message: "success"                                            │
│  timestamp: 1708867200000                                      │
│  data: OrderCreateDTO ─────────────────────────────────────┐   │
│         │ {orderNo:"ORD...", paymentNo:"PAY...", ...}     │   │
│         └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 500 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **OrderCreateDTO** | 可为null | - | **订单创建结果** |
| data.orderNo | String | 非空 | ORD+17位数字 | 订单唯一编号 |
| data.totalAmount | BigDecimal | 非空 | >=0.01，最多2位小数 | 订单总金额 |
| data.payAmount | BigDecimal | 非空 | >=0.01，最多2位小数 | 实付金额 |
| data.status | Integer | 非空 | 0 | 订单初始状态 |
| data.paymentNo | String | 非空 | PAY+17位数字 | 支付流水号 |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderNo": "ORD202602250001",
    "totalAmount": 17998.00,
    "payAmount": 17998.00,
    "status": 0,
    "paymentNo": "PAY202602250001"
  },
  "timestamp": 1708867200000
}
```

#### 5.3.2 获取订单详情

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 获取订单详情 |
| 接口路径 | GET /api/orders/{orderNo} |
| 调用方 | 支付服务、前端 |
| 接口描述 | 根据订单编号获取订单详细信息 |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| orderNo | String | VARCHAR(50) | 非空，格式：ORD+17位数字 | 订单编号 |

**输出定义**：

**真实返回类型**：`Result<OrderDTO>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;              // 数据对象（本接口T = OrderDTO）
    private Long timestamp;        // 时间戳（毫秒）
}

// 订单详情DTO
public class OrderDTO {
    private String orderNo;                     // 订单编号，VARCHAR(50)，非空
    private Long userId;                        // 用户ID，BIGINT，非空
    private BigDecimal totalAmount;             // 订单总金额，DECIMAL(10,2)，非空
    private BigDecimal payAmount;               // 实付金额，DECIMAL(10,2)，非空
    private Integer status;                     // 订单状态，TINYINT，非空
    private Integer paymentStatus;              // 支付状态，TINYINT，非空
    private String receiverName;                // 收货人姓名，VARCHAR(50)，非空
    private String receiverPhone;               // 收货人电话，VARCHAR(20)，非空
    private String receiverAddress;             // 收货地址，VARCHAR(200)，非空
    private LocalDateTime createTime;           // 创建时间，DATETIME，非空
    private LocalDateTime payTime;              // 支付时间，DATETIME，可为空
    private List<OrderItemDTO> items;           // 订单项列表，非空
}

// 订单项DTO
public class OrderItemDTO {
    private Long productId;                     // 商品ID，BIGINT，非空
    private String productName;                 // 商品名称，VARCHAR(200)，非空
    private String productImage;                // 商品图片，VARCHAR(255)，可为空
    private BigDecimal price;                   // 商品单价，DECIMAL(10,2)，非空
    private Integer quantity;                   // 购买数量，INT，非空
    private BigDecimal totalAmount;             // 小计金额，DECIMAL(10,2)，非空
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<OrderDTO>                                                │
│  (本接口返回类型：单个订单对象)                                   │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                     │
│  message: "success"                                            │
│  timestamp: 1708867200000                                      │
│  data: OrderDTO ───────────────────────────────────────────┐   │
│         │ {orderNo:"ORD...", items:[...], ...}              │   │
│         └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **OrderDTO** | 可为null | - | **订单详情对象** |
| data.orderNo | String | 非空 | ORD+17位数字 | 订单编号 |
| data.userId | Long | 非空 | 正整数 | 用户ID |
| data.totalAmount | BigDecimal | 非空 | >=0，最多2位小数 | 订单总金额 |
| data.payAmount | BigDecimal | 非空 | >=0，最多2位小数 | 实付金额 |
| data.status | Integer | 非空 | 0-5 | 订单状态 |
| data.paymentStatus | Integer | 非空 | 0-2 | 支付状态 |
| data.receiverName | String | 非空 | 1-50字符 | 收货人姓名 |
| data.receiverPhone | String | 非空 | 11位数字 | 收货人电话 |
| data.receiverAddress | String | 非空 | 1-200字符 | 完整收货地址 |
| data.createTime | LocalDateTime | 非空 | ISO-8601格式 | 订单创建时间 |
| data.payTime | LocalDateTime | 可为null | ISO-8601格式 | 支付完成时间 |
| data.items | List\<OrderItemDTO\> | 非空 | - | 订单商品列表 |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderNo": "ORD202602250001",
    "userId": 1001,
    "totalAmount": 17998.00,
    "payAmount": 17998.00,
    "status": 0,
    "paymentStatus": 0,
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "receiverAddress": "广东省深圳市南山区科技园路1号",
    "createTime": "2026-02-25T10:00:00",
    "payTime": null,
    "items": [
      {
        "productId": 1001,
        "productName": "iPhone 15 Pro",
        "productImage": "https://example.com/product.jpg",
        "price": 8999.00,
        "quantity": 2,
        "totalAmount": 17998.00
      }
    ]
  },
  "timestamp": 1708867200000
}
```

#### 5.3.3 更新订单状态

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 更新订单状态 |
| 接口路径 | PUT /api/orders/{orderNo}/status |
| 调用方 | 支付服务 |
| 接口描述 | 支付成功后更新订单状态 |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| orderNo | String | VARCHAR(50) | 非空，格式：ORD+17位数字 | 订单编号 |

**请求DTO类**：
```java
public class OrderStatusUpdateRequest {
    @NotNull(message = "订单状态不能为空")
    @Min(value = 0, message = "订单状态无效")
    @Max(value = 7, message = "订单状态无效")
    private Integer status;                     // 订单状态，TINYINT，非空，0-7
    
    @NotNull(message = "支付ID不能为空")
    private Long paymentId;                     // 支付ID，BIGINT，非空
    
    @NotNull(message = "支付时间不能为空")
    private LocalDateTime payTime;              // 支付时间，DATETIME，非空
}
```

**请求字段详细说明**：
| 字段名 | Java类型 | 数据库类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|:---|
| status | Integer | TINYINT | 非空 | 0-7 | 订单状态码 |
| paymentId | Long | BIGINT | 非空 | 正整数 | 支付记录ID |
| payTime | LocalDateTime | DATETIME | 非空 | ISO-8601格式 | 支付完成时间 |

**请求示例**：
```json
{
  "status": 1,
  "paymentId": 1001,
  "payTime": "2026-02-25T10:30:00"
}
```

**输出定义**：

**真实返回类型**：`Result<Void>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;              // 数据对象（本接口T = Void，无数据）
    private Long timestamp;        // 时间戳（毫秒）
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<Void>                                                   │
│  (本接口返回类型：无数据，仅返回操作结果状态)                      │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                     │
│  message: "success"                                            │
│  timestamp: 1708867200000                                      │
│  data: null                                                    │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **Void** | 固定为null | - | **无返回数据** |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null,
  "timestamp": 1708867200000
}
```

#### 5.3.4 支付成功回调

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 支付成功回调 |
| 接口路径 | POST /api/orders/{orderNo}/paid |
| 调用方 | 支付服务 |
| 接口描述 | 支付成功后通知订单服务更新状态并确认扣减库存 |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| orderNo | String | VARCHAR(50) | 非空，格式：ORD+17位数字 | 订单编号 |

**请求DTO类**：
```java
public class PaymentCallbackRequest {
    @NotBlank(message = "支付流水号不能为空")
    @Pattern(regexp = "^PAY\\d{17}$", message = "支付流水号格式错误")
    private String paymentNo;                   // 支付流水号，VARCHAR(50)，非空
    
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0")
    private BigDecimal amount;                  // 支付金额，DECIMAL(10,2)，非空
    
    @NotNull(message = "支付时间不能为空")
    private LocalDateTime payTime;              // 支付时间，DATETIME，非空
    
    @NotNull(message = "支付方式不能为空")
    @Min(value = 1, message = "支付方式无效")
    @Max(value = 2, message = "支付方式无效")
    private Integer paymentMethod;              // 支付方式，TINYINT，非空，1-微信，2-支付宝
}
```

**请求字段详细说明**：
| 字段名 | Java类型 | 数据库类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|:---|
| paymentNo | String | VARCHAR(50) | 非空 | PAY+17位数字 | 支付流水号 |
| amount | BigDecimal | DECIMAL(10,2) | 非空 | >=0.01 | 支付金额 |
| payTime | LocalDateTime | DATETIME | 非空 | ISO-8601格式 | 支付完成时间 |
| paymentMethod | Integer | TINYINT | 非空 | 1, 2 | 支付方式 |

**请求示例**：
```json
{
  "paymentNo": "PAY202602250001",
  "amount": 17998.00,
  "payTime": "2026-02-25T10:30:00",
  "paymentMethod": 1
}
```

**输出定义**：

**真实返回类型**：`Result<Void>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;              // 数据对象（本接口T = Void，无数据）
    private Long timestamp;        // 时间戳（毫秒）
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<Void>                                                   │
│  (本接口返回类型：无数据，仅返回操作结果状态)                      │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                     │
│  message: "success"                                            │
│  timestamp: 1708867200000                                      │
│  data: null                                                    │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **Void** | 固定为null | - | **无返回数据** |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null,
  "timestamp": 1708867200000
}
```

#### 5.3.5 支付失败回调

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 支付失败回调 |
| 接口路径 | POST /api/orders/{orderNo}/payment-failed |
| 调用方 | 支付服务 |
| 接口描述 | 支付失败后通知订单服务取消订单并释放库存 |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| orderNo | String | VARCHAR(50) | 非空，格式：ORD+17位数字 | 订单编号 |

**请求DTO类**：
```java
public class PaymentFailedRequest {
    @NotBlank(message = "支付流水号不能为空")
    @Pattern(regexp = "^PAY\\d{17}$", message = "支付流水号格式错误")
    private String paymentNo;                   // 支付流水号，VARCHAR(50)，非空
    
    @NotBlank(message = "失败原因不能为空")
    @Size(max = 200, message = "失败原因最多200字符")
    private String failReason;                  // 失败原因，VARCHAR(200)，非空
    
    @NotNull(message = "失败时间不能为空")
    private LocalDateTime failTime;             // 失败时间，DATETIME，非空
}
```

**请求字段详细说明**：
| 字段名 | Java类型 | 数据库类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|:---|
| paymentNo | String | VARCHAR(50) | 非空 | PAY+17位数字 | 支付流水号 |
| failReason | String | VARCHAR(200) | 非空 | 1-200字符 | 支付失败原因 |
| failTime | LocalDateTime | DATETIME | 非空 | ISO-8601格式 | 支付失败时间 |

**请求示例**：
```json
{
  "paymentNo": "PAY202602250001",
  "failReason": "用户取消支付",
  "failTime": "2026-02-25T10:30:00"
}
```

**输出定义**：

**真实返回类型**：`Result<Void>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;              // 数据对象（本接口T = Void，无数据）
    private Long timestamp;        // 时间戳（毫秒）
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<Void>                                                   │
│  (本接口返回类型：无数据，仅返回操作结果状态)                      │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                     │
│  message: "success"                                            │
│  timestamp: 1708867200000                                      │
│  data: null                                                    │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **Void** | 固定为null | - | **无返回数据** |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null,
  "timestamp": 1708867200000
}
```

**业务逻辑**：
1. 验证订单状态为"待支付"
2. 更新订单状态为"已取消"
3. 释放预扣库存
4. 记录取消原因

#### 5.3.6 支付超时回调

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 支付超时回调 |
| 接口路径 | POST /api/orders/{orderNo}/payment-timeout |
| 调用方 | 支付服务 |
| 接口描述 | 支付超时后通知订单服务取消订单并释放库存 |

**输入定义**：

**Path参数**：
| 参数名 | Java类型 | 数据库类型 | 约束 | 说明 |
|:---|:---|:---|:---|:---|
| orderNo | String | VARCHAR(50) | 非空，格式：ORD+17位数字 | 订单编号 |

**请求DTO类**：
```java
public class PaymentTimeoutRequest {
    @NotBlank(message = "支付流水号不能为空")
    @Pattern(regexp = "^PAY\\d{17}$", message = "支付流水号格式错误")
    private String paymentNo;                   // 支付流水号，VARCHAR(50)，非空
    
    @NotNull(message = "超时分钟数不能为空")
    @Min(value = 1, message = "超时分钟数必须大于0")
    private Integer timeoutMinutes;             // 超时分钟数，INT，非空，>=1
    
    @NotNull(message = "超时时间不能为空")
    private LocalDateTime timeoutTime;          // 超时时间，DATETIME，非空
}
```

**请求字段详细说明**：
| 字段名 | Java类型 | 数据库类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|:---|
| paymentNo | String | VARCHAR(50) | 非空 | PAY+17位数字 | 支付流水号 |
| timeoutMinutes | Integer | INT | 非空 | >=1 | 超时分钟数 |
| timeoutTime | LocalDateTime | DATETIME | 非空 | ISO-8601格式 | 超时时间点 |

**请求示例**：
```json
{
  "paymentNo": "PAY202602250001",
  "timeoutMinutes": 30,
  "timeoutTime": "2026-02-25T11:00:00"
}
```

**输出定义**：

**响应DTO类**：
```java
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
}
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | Void | 可为null | - | 无返回数据 |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null,
  "timestamp": 1708867200000
}
```

**业务逻辑**：
1. 验证订单状态为"待支付"
2. 更新订单状态为"已取消"
3. 释放预扣库存
4. 记录取消原因为"支付超时"

**Feign客户端定义**：
```java
@FeignClient(name = "order-service", path = "/api/orders")
public interface OrderClient {
    
    @GetMapping("/{orderNo}")
    Result<OrderDTO> getOrderByNo(@PathVariable("orderNo") String orderNo);
    
    @PutMapping("/{orderNo}/status")
    Result<Void> updateOrderStatus(@PathVariable("orderNo") String orderNo, 
                                    @RequestBody OrderStatusUpdateRequest request);
    
    @PostMapping("/{orderNo}/paid")
    Result<Void> orderPaidCallback(@PathVariable("orderNo") String orderNo,
                                    @RequestBody PaymentCallbackRequest request);
    
    @PostMapping("/{orderNo}/payment-failed")
    Result<Void> orderPaymentFailedCallback(@PathVariable("orderNo") String orderNo,
                                             @RequestBody PaymentFailedRequest request);
    
    @PostMapping("/{orderNo}/payment-timeout")
    Result<Void> orderPaymentTimeoutCallback(@PathVariable("orderNo") String orderNo,
                                              @RequestBody PaymentTimeoutRequest request);
}
  "message": "success",
  "data": null
}
```

**业务逻辑**：
1. 验证订单状态为"待支付"
2. 更新订单状态为"已取消"
3. 释放预扣库存
4. 记录取消原因为"支付超时"

**Feign客户端定义**：
```java
@FeignClient(name = "order-service", path = "/api/orders")
public interface OrderClient {
    
    @GetMapping("/{orderNo}")
    Result<OrderDTO> getOrderByNo(@PathVariable("orderNo") String orderNo);
    
    @PutMapping("/{orderNo}/status")
    Result<Void> updateOrderStatus(@PathVariable("orderNo") String orderNo, 
                                    @RequestBody OrderStatusUpdateRequest request);
    
    @PostMapping("/{orderNo}/paid")
    Result<Void> orderPaidCallback(@PathVariable("orderNo") String orderNo,
                                    @RequestBody PaymentCallbackRequest request);
    
    @PostMapping("/{orderNo}/payment-failed")
    Result<Void> orderPaymentFailedCallback(@PathVariable("orderNo") String orderNo,
                                             @RequestBody PaymentFailedRequest request);
    
    @PostMapping("/{orderNo}/payment-timeout")
    Result<Void> orderPaymentTimeoutCallback(@PathVariable("orderNo") String orderNo,
                                              @RequestBody PaymentTimeoutRequest request);
}
```

### 5.4 支付服务对外接口

#### 5.4.1 创建支付

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 创建支付 |
| 接口路径 | POST /api/payments |
| 调用方 | 订单服务 |
| 接口描述 | 创建支付记录，生成支付流水号 |

**请求参数**：
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|:---|:---|:---|:---|:---|
| orderNo | String | Body | 是 | 订单编号 |
| userId | Long | Body | 是 | 用户ID |
| amount | BigDecimal | Body | 是 | 支付金额 |
| paymentMethod | Integer | Body | 是 | 支付方式：1-微信，2-支付宝 |

**请求示例**：
```json
{
  "orderNo": "ORD202602250001",
  "userId": 1001,
  "amount": 17998.00,
  "paymentMethod": 1
}
```

**输出定义**：

**真实返回类型**：`Result<PaymentCreateDTO>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;          // 响应码
    private String message;        // 响应消息
    private T data;              // 数据对象（本接口T = PaymentCreateDTO）
    private Long timestamp;        // 时间戳（毫秒）
}

// 支付创建结果DTO
public class PaymentCreateDTO {
    private String paymentNo;       // 支付流水号，VARCHAR(50)，非空
    private String orderNo;         // 订单编号，VARCHAR(50)，非空
    private BigDecimal amount;     // 支付金额，DECIMAL(10,2)，非空
    private Integer status;        // 支付状态，TINYINT，非空，0-待支付
}
```

**响应结构说明**：
```
┌─────────────────────────────────────────────────────────────────┐
│  Result<PaymentCreateDTO>                                        │
│  (本接口返回类型：支付创建结果)                                  │
├─────────────────────────────────────────────────────────────────┤
│  code: 200                                                     │
│  message: "success"                                            │
│  timestamp: 1708867200000                                      │
│  data: PaymentCreateDTO ─────────────────────────────────┐   │
│         │ {paymentNo:"PAY...", orderNo:"ORD...", ...}     │   │
│         └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 500 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **PaymentCreateDTO** | 可为null | - | **支付创建结果** |
| data.paymentNo | String | 非空 | PAY+17位数字 | 支付流水号 |
| data.orderNo | String | 非空 | ORD+17位数字 | 订单编号 |
| data.amount | BigDecimal | 非空 | >=0，最多2位小数 | 支付金额 |
| data.status | Integer | 非空 | 0 | 支付状态 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentNo": "PAY202602250001",
    "orderNo": "ORD202602250001",
    "amount": 17998.00,
    "status": 0
  }
}
```

#### 5.4.2 查询支付状态

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 查询支付状态 |
| 接口路径 | GET /api/payments/{paymentNo} |
| 调用方 | 订单服务、前端 |
| 接口描述 | 根据支付流水号查询支付状态 |

**请求参数**：
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|:---|:---|:---|:---|:---|
| paymentNo | String | Path | 是 | 支付流水号 |

**输出定义**：

**真实返回类型**：`Result<PaymentDTO>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
}

// 支付信息DTO
public class PaymentDTO {
    private String paymentNo;       // 支付流水号
    private String orderNo;         // 订单编号
    private BigDecimal amount;     // 支付金额
    private Integer status;        // 支付状态
    private LocalDateTime payTime; // 支付时间
}
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **PaymentDTO** | 可为null | - | **支付信息** |
| data.paymentNo | String | 非空 | PAY+17位数字 | 支付流水号 |
| data.orderNo | String | 非空 | ORD+17位数字 | 订单编号 |
| data.amount | BigDecimal | 非空 | >=0，最多2位小数 | 支付金额 |
| data.status | Integer | 非空 | 0, 1, 2, 3 | 支付状态 |
| data.payTime | LocalDateTime | 可为null | ISO-8601格式 | 支付时间 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentNo": "PAY202602250001",
    "orderNo": "ORD202602250001",
    "amount": 17998.00,
    "status": 1,
    "payTime": "2026-02-25T10:30:00"
  }
}
```

#### 5.4.3 申请退款

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 申请退款 |
| 接口路径 | POST /api/payments/refund |
| 调用方 | 订单服务 |
| 接口描述 | 申请退款，调用第三方退款接口 |

**请求参数**：
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|:---|:---|:---|:---|:---|
| paymentNo | String | Body | 是 | 支付流水号 |
| orderNo | String | Body | 是 | 订单编号 |
| amount | BigDecimal | Body | 是 | 退款金额 |
| reason | String | Body | 是 | 退款原因 |

**请求示例**：
```json
{
  "paymentNo": "PAY202602250001",
  "orderNo": "ORD202602250001",
  "amount": 17998.00,
  "reason": "用户取消订单"
}
```

**输出定义**：

**真实返回类型**：`Result<RefundDTO>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
}

// 退款结果DTO
public class RefundDTO {
    private String refundNo;      // 退款流水号
    private Integer status;       // 退款状态
}
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 500 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **RefundDTO** | 可为null | - | **退款结果** |
| data.refundNo | String | 非空 | REF+17位数字 | 退款流水号 |
| data.status | Integer | 非空 | 0, 1, 2 | 退款状态 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "refundNo": "REF202602250001",
    "status": 0
  }
}
```

#### 5.4.4 关闭支付

**接口信息**：
| 项目 | 内容 |
|:---|:---|
| 接口名称 | 关闭支付 |
| 接口路径 | POST /api/payments/{paymentNo}/close |
| 调用方 | 订单服务 |
| 接口描述 | 关闭支付记录（订单取消时调用） |

**请求参数**：
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|:---|:---|:---|:---|:---|
| paymentNo | String | Path | 是 | 支付流水号 |

**输出定义**：

**真实返回类型**：`Result<Void>`

**响应DTO类**：
```java
// 统一响应包装类
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
}
```

**响应字段详细说明**：
| 字段路径 | Java类型 | 约束 | 取值范围 | 说明 |
|:---|:---|:---|:---|:---|
| code | Integer | 非空 | 200, 400, 404 | 响应状态码 |
| message | String | 非空 | 最大200字符 | 响应消息 |
| data | **Void** | 固定为null | - | **无返回数据** |
| timestamp | Long | 非空 | 13位时间戳 | 响应时间戳 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**Feign客户端定义**：
```java
@FeignClient(name = "payment-service", path = "/api/payments")
public interface PaymentClient {
    
    @PostMapping
    Result<PaymentDTO> createPayment(@RequestBody PaymentCreateRequest request);
    
    @GetMapping("/{paymentNo}")
    Result<PaymentDTO> getPaymentByNo(@PathVariable("paymentNo") String paymentNo);
    
    @PostMapping("/refund")
    Result<RefundDTO> createRefund(@RequestBody RefundCreateRequest request);
    
    @PostMapping("/{paymentNo}/close")
    Result<Void> closePayment(@PathVariable("paymentNo") String paymentNo);
}
```

## 6. 核心业务流程串联

### 6.1 完整下单流程

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    完整下单流程                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

前端                    订单服务                 用户服务                商品服务               支付服务
 │                         │                       │                      │                     │
 │  1.创建订单请求          │                       │                      │                     │
 │────────────────────────▶│                       │                      │                     │
 │                         │                       │                      │                     │
 │                         │  2.获取用户信息         │                      │                     │
 │                         │──────────────────────▶│                      │                     │
 │                         │                       │                      │                     │
 │                         │  3.返回用户信息         │                      │                     │
 │                         │◀──────────────────────│                      │                     │
 │                         │                       │                      │                     │
 │                         │  4.获取收货地址         │                      │                     │
 │                         │──────────────────────▶│                      │                     │
 │                         │                       │                      │                     │
 │                         │  5.返回地址信息         │                      │                     │
 │                         │◀──────────────────────│                      │                     │
 │                         │                       │                      │                     │
 │                         │  6.批量获取商品信息      │                      │                     │
 │                         │──────────────────────────────────────────────▶│                     │
 │                         │                       │                      │                     │
 │                         │  7.返回商品信息         │                      │                     │
 │                         │◀──────────────────────────────────────────────│                     │
 │                         │                       │                      │                     │
 │                         │  8.预扣库存            │                      │                     │
 │                         │──────────────────────────────────────────────▶│                     │
 │                         │                       │                      │                     │
 │                         │  9.预扣成功            │                      │                     │
 │                         │◀──────────────────────────────────────────────│                     │
 │                         │                       │                      │                     │
 │                         │  10.创建支付记录        │                      │                     │
 │                         │────────────────────────────────────────────────────────────────────▶│
 │                         │                       │                      │                     │
 │                         │  11.返回支付信息        │                      │                     │
 │                         │◀────────────────────────────────────────────────────────────────────│
 │                         │                       │                      │                     │
 │                         │  12.创建订单           │                      │                     │
 │                         │                       │                      │                     │
 │  13.返回订单信息         │                       │                      │                     │
 │◀────────────────────────│                       │                      │                     │
 │                         │                       │                      │                     │
 │  14.发起支付请求         │                       │                      │                     │
 │────────────────────────────────────────────────────────────────────────────────────────────▶│
 │                         │                       │                      │                     │
 │  15.返回支付链接         │                       │                      │                     │
 │◀────────────────────────────────────────────────────────────────────────────────────────────│
 │                         │                       │                      │                     │
```

### 6.2 支付成功回调流程

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                  支付成功回调流程                                    │
└─────────────────────────────────────────────────────────────────────────────────────┘

第三方支付              支付服务                 订单服务                商品服务
    │                     │                       │                      │
    │  1.支付成功回调      │                       │                      │
    │────────────────────▶│                       │                      │
    │                     │                       │                      │
    │                     │  2.验证签名            │                      │
    │                     │                       │                      │
    │                     │  3.更新支付状态        │                      │
    │                     │                       │                      │
    │                     │  4.通知订单服务        │                      │
    │                     │──────────────────────▶│                      │
    │                     │                       │                      │
    │                     │                       │  5.更新订单状态       │
    │                     │                       │                      │
    │                     │                       │  6.确认扣减库存       │
    │                     │                       │─────────────────────▶│
    │                     │                       │                      │
    │                     │                       │  7.扣减成功           │
    │                     │                       │◀─────────────────────│
    │                     │                       │                      │
    │                     │  8.返回处理结果        │                      │
    │                     │◀──────────────────────│                      │
    │                     │                       │                      │
    │  9.返回成功响应      │                       │                      │
    │◀────────────────────│                       │                      │
    │                     │                       │                      │
```

### 6.3 订单取消流程

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    订单取消流程                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

前端                    订单服务                 商品服务               支付服务
 │                         │                       │                      │
 │  1.取消订单请求          │                       │                      │
 │────────────────────────▶│                       │                      │
 │                         │                       │                      │
 │                         │  2.检查订单状态        │                      │
 │                         │                       │                      │
 │                         │  3.判断是否需要退款    │                      │
 │                         │                       │                      │
 │                         │  [如果已支付]          │                      │
 │                         │  4.申请退款            │                      │
 │                         │─────────────────────────────────────────────▶│
 │                         │                       │                      │
 │                         │  5.返回退款结果        │                      │
 │                         │◀─────────────────────────────────────────────│
 │                         │                       │                      │
 │                         │  6.释放库存            │                      │
 │                         │──────────────────────▶│                      │
 │                         │                       │                      │
 │                         │  7.释放成功            │                      │
 │                         │◀──────────────────────│                      │
 │                         │                       │                      │
 │                         │  8.更新订单状态        │                      │
 │                         │                       │                      │
 │  9.返回取消结果          │                       │                      │
 │◀────────────────────────│                       │                      │
 │                         │                       │                      │
```

### 6.4 支付失败流程（重要！）

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    支付失败流程                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

第三方支付              支付服务                 订单服务                商品服务
    │                     │                       │                      │
    │  1.支付失败回调      │                       │                      │
    │────────────────────▶│                       │                      │
    │                     │                       │                      │
    │                     │  2.验证签名            │                      │
    │                     │                       │                      │
    │                     │  3.更新支付状态为失败  │                      │
    │                     │                       │                      │
    │                     │  4.通知订单服务取消订单 │                      │
    │                     │──────────────────────▶│                      │
    │                     │                       │                      │
    │                     │                       │  5.更新订单状态为取消 │
    │                     │                       │                      │
    │                     │                       │  6.释放预扣库存       │
    │                     │                       │─────────────────────▶│
    │                     │                       │                      │
    │                     │                       │  7.释放成功           │
    │                     │                       │◀─────────────────────│
    │                     │                       │                      │
    │                     │  8.返回处理结果        │                      │
    │                     │◀──────────────────────│                      │
    │                     │                       │                      │
    │  9.返回成功响应      │                       │                      │
    │◀────────────────────│                       │                      │
    │                     │                       │                      │
```

### 6.5 支付超时自动取消流程（重要！）

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                  支付超时自动取消流程                                │
└─────────────────────────────────────────────────────────────────────────────────────┘

定时任务                支付服务                 订单服务                商品服务
    │                     │                       │                      │
    │  1.扫描超时支付订单  │                       │                      │
    │────────────────────▶│                       │                      │
    │                     │                       │                      │
    │                     │  2.查询超时支付记录    │                      │
    │                     │  (超过30分钟未支付)    │                      │
    │                     │                       │                      │
    │                     │  3.更新支付状态为超时  │                      │
    │                     │                       │                      │
    │                     │  4.通知订单服务取消订单 │                      │
    │                     │──────────────────────▶│                      │
    │                     │                       │                      │
    │                     │                       │  5.检查订单状态       │
    │                     │                       │  (确保未支付)         │
    │                     │                       │                      │
    │                     │                       │  6.更新订单状态为取消 │
    │                     │                       │                      │
    │                     │                       │  7.释放预扣库存       │
    │                     │                       │─────────────────────▶│
    │                     │                       │                      │
    │                     │                       │  8.释放成功           │
    │                     │                       │◀─────────────────────│
    │                     │                       │                      │
    │                     │  9.返回处理结果        │                      │
    │                     │◀──────────────────────│                      │
    │                     │                       │                      │
    │  10.返回处理结果     │                       │                      │
    │◀────────────────────│                       │                      │
    │                     │                       │                      │
```

**支付超时配置说明**：
- 默认超时时间：30分钟（可配置）
- 扫描频率：每分钟扫描一次
- 超时处理：自动取消订单 + 释放库存 + 关闭支付

## 7. 自顶而下设计指南

### 7.1 设计原则

#### 7.1.1 服务分层原则
```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    服务分层设计原则                                  │
└─────────────────────────────────────────────────────────────────────────────────────┘

【第一层：基础服务层】
├── 用户服务 (user-service)
│   └── 职责：用户数据管理、认证授权
│   └── 特点：不依赖其他业务服务，提供基础用户数据
│   └── 设计优先级：最高
│
└── 商品服务 (product-service)
    └── 职责：商品数据管理、库存管理
    └── 特点：不依赖其他业务服务，提供基础商品数据
    └── 设计优先级：最高

【第二层：核心服务层】
├── 订单服务 (order-service)
│   └── 职责：订单编排、业务流程协调
│   └── 特点：依赖基础服务，是业务流程的核心
│   └── 设计优先级：高
│
└── 支付服务 (payment-service)
    └── 职责：支付处理、退款处理
    └── 特点：依赖订单服务，处理资金流转
    └── 设计优先级：高

【设计顺序】
1. 先设计基础服务（用户服务、商品服务）
2. 再设计核心服务（订单服务、支付服务）
3. 最后设计服务间接口和调用关系
```

#### 7.1.2 接口设计原则
```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    接口设计原则                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

【RESTful API设计规范】
├── 资源命名：使用名词复数形式，如 /users, /products, /orders
├── HTTP方法：
│   ├── GET：查询资源
│   ├── POST：创建资源
│   ├── PUT：更新资源（全量更新）
│   ├── PATCH：更新资源（部分更新）
│   └── DELETE：删除资源
├── 状态码：
│   ├── 200：成功
│   ├── 201：创建成功
│   ├── 400：请求参数错误
│   ├── 401：未授权
│   ├── 403：禁止访问
│   ├── 404：资源不存在
│   └── 500：服务器内部错误
└── 响应格式：
    {
      "code": 200,
      "message": "success",
      "data": { ... },
      "timestamp": 1708867200000
    }

【服务间接口设计规范】
├── 使用DTO传输数据，避免暴露实体类
├── 接口版本化：/api/v1/users, /api/v2/users
├── 幂等性设计：关键接口支持幂等调用
├── 超时设置：合理设置Feign调用超时时间
└── 熔断降级：配置Sentinel熔断规则
```

### 7.2 开发顺序建议

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    开发顺序建议                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

【第一阶段：基础服务开发】（预计2周）
├── Week 1: 用户服务
│   ├── Day 1-2: 数据库设计、实体类定义
│   ├── Day 3-4: 用户注册、登录接口开发
│   ├── Day 5: 用户信息管理接口开发
│   └── Day 6-7: 收货地址管理、单元测试
│
└── Week 2: 商品服务
    ├── Day 1-2: 数据库设计、实体类定义
    ├── Day 3-4: 商品CRUD接口开发
    ├── Day 5: 库存管理接口开发
    └── Day 6-7: 商品搜索、单元测试

【第二阶段：核心服务开发】（预计2周）
├── Week 3: 订单服务
│   ├── Day 1-2: 数据库设计、实体类定义
│   ├── Day 3-4: 订单创建接口开发（集成用户、商品服务）
│   ├── Day 5: 订单查询、状态管理接口开发
│   └── Day 6-7: 订单取消、退款流程、单元测试
│
└── Week 4: 支付服务
    ├── Day 1-2: 数据库设计、实体类定义
    ├── Day 3-4: 支付创建、支付执行接口开发
    ├── Day 5: 支付回调、退款接口开发
    └── Day 6-7: 集成测试、压力测试

【第三阶段：集成与优化】（预计1周）
├── Day 1-2: 服务间集成测试
├── Day 3-4: 性能优化、问题修复
└── Day 5: 文档完善、代码审查
```

### 7.3 数据库设计指南

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    数据库设计指南                                    │
└─────────────────────────────────────────────────────────────────────────────────────┘

【用户服务数据库 (user_db)】
├── 用户表 (user)
│   ├── id: BIGINT PRIMARY KEY AUTO_INCREMENT
│   ├── username: VARCHAR(50) UNIQUE NOT NULL
│   ├── password: VARCHAR(100) NOT NULL
│   ├── phone: VARCHAR(20) UNIQUE
│   ├── email: VARCHAR(100) UNIQUE
│   ├── nickname: VARCHAR(50)
│   ├── avatar: VARCHAR(255)
│   ├── status: TINYINT DEFAULT 1
│   ├── create_time: DATETIME
│   └── update_time: DATETIME
│
└── 收货地址表 (user_address)
    ├── id: BIGINT PRIMARY KEY AUTO_INCREMENT
    ├── user_id: BIGINT NOT NULL
    ├── receiver_name: VARCHAR(50) NOT NULL
    ├── receiver_phone: VARCHAR(20) NOT NULL
    ├── province: VARCHAR(50) NOT NULL
    ├── city: VARCHAR(50) NOT NULL
    ├── district: VARCHAR(50) NOT NULL
    ├── detail_address: VARCHAR(200) NOT NULL
    ├── is_default: TINYINT DEFAULT 0
    ├── create_time: DATETIME
    └── update_time: DATETIME

【商品服务数据库 (product_db)】
├── 商品表 (product)
│   ├── id: BIGINT PRIMARY KEY AUTO_INCREMENT
│   ├── name: VARCHAR(200) NOT NULL
│   ├── description: TEXT
│   ├── price: DECIMAL(10,2) NOT NULL
│   ├── original_price: DECIMAL(10,2)
│   ├── stock: INT DEFAULT 0
│   ├── sales: INT DEFAULT 0
│   ├── category_id: BIGINT
│   ├── main_image: VARCHAR(255)
│   ├── sub_images: JSON
│   ├── status: TINYINT DEFAULT 1
│   ├── create_time: DATETIME
│   └── update_time: DATETIME
│
├── 商品分类表 (category)
│   ├── id: BIGINT PRIMARY KEY AUTO_INCREMENT
│   ├── name: VARCHAR(50) NOT NULL
│   ├── parent_id: BIGINT DEFAULT 0
│   ├── level: TINYINT DEFAULT 1
│   ├── sort_order: INT DEFAULT 0
│   ├── create_time: DATETIME
│   └── update_time: DATETIME
│
└── 库存流水表 (stock_log)
    ├── id: BIGINT PRIMARY KEY AUTO_INCREMENT
    ├── product_id: BIGINT NOT NULL
    ├── quantity: INT NOT NULL
    ├── type: VARCHAR(20) NOT NULL
    ├── order_no: VARCHAR(50)
    ├── create_time: DATETIME
    └── INDEX idx_product_id (product_id)
    └── INDEX idx_order_no (order_no)

【订单服务数据库 (order_db)】
├── 订单表 (orders)
│   ├── id: BIGINT PRIMARY KEY AUTO_INCREMENT
│   ├── order_no: VARCHAR(50) UNIQUE NOT NULL
│   ├── user_id: BIGINT NOT NULL
│   ├── total_amount: DECIMAL(10,2) NOT NULL
│   ├── pay_amount: DECIMAL(10,2) NOT NULL
│   ├── freight_amount: DECIMAL(10,2) DEFAULT 0
│   ├── discount_amount: DECIMAL(10,2) DEFAULT 0
│   ├── status: TINYINT DEFAULT 0
│   ├── receiver_name: VARCHAR(50) NOT NULL
│   ├── receiver_phone: VARCHAR(20) NOT NULL
│   ├── receiver_address: VARCHAR(200) NOT NULL
│   ├── payment_id: BIGINT
│   ├── payment_status: TINYINT DEFAULT 0
│   ├── create_time: DATETIME
│   ├── pay_time: DATETIME
│   ├── delivery_time: DATETIME
│   ├── receive_time: DATETIME
│   └── INDEX idx_user_id (user_id)
    └── INDEX idx_order_no (order_no)
│
└── 订单项表 (order_item)
    ├── id: BIGINT PRIMARY KEY AUTO_INCREMENT
    ├── order_id: BIGINT NOT NULL
    ├── product_id: BIGINT NOT NULL
    ├── product_name: VARCHAR(200) NOT NULL
    ├── product_image: VARCHAR(255)
    ├── price: DECIMAL(10,2) NOT NULL
    ├── quantity: INT NOT NULL
    ├── total_amount: DECIMAL(10,2) NOT NULL
    └── INDEX idx_order_id (order_id)

【支付服务数据库 (payment_db)】
├── 支付记录表 (payment)
│   ├── id: BIGINT PRIMARY KEY AUTO_INCREMENT
│   ├── payment_no: VARCHAR(50) UNIQUE NOT NULL
│   ├── order_no: VARCHAR(50) NOT NULL
│   ├── user_id: BIGINT NOT NULL
│   ├── amount: DECIMAL(10,2) NOT NULL
│   ├── payment_method: TINYINT NOT NULL
│   ├── status: TINYINT DEFAULT 0
│   ├── third_party_no: VARCHAR(100)
│   ├── pay_time: DATETIME
│   ├── create_time: DATETIME
│   └── INDEX idx_order_no (order_no)
    └── INDEX idx_payment_no (payment_no)
│
└── 退款记录表 (refund)
    ├── id: BIGINT PRIMARY KEY AUTO_INCREMENT
    ├── refund_no: VARCHAR(50) UNIQUE NOT NULL
    ├── payment_no: VARCHAR(50) NOT NULL
    ├── order_no: VARCHAR(50) NOT NULL
    ├── amount: DECIMAL(10,2) NOT NULL
    ├── reason: VARCHAR(200)
    ├── status: TINYINT DEFAULT 0
    ├── refund_time: DATETIME
    ├── create_time: DATETIME
    └── INDEX idx_order_no (order_no)
```

### 7.4 配置管理指南

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    配置管理指南                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

【Nacos配置结构】
├── 命名空间 (namespace)
│   ├── dev (开发环境)
│   ├── test (测试环境)
│   └── prod (生产环境)
│
├── 配置分组 (group)
│   ├── DEFAULT_GROUP (默认分组)
│   └── DATABASE_GROUP (数据库配置)
│
└── 配置文件
    ├── application.yml (公共配置)
    ├── user-service.yml (用户服务配置)
    ├── product-service.yml (商品服务配置)
    ├── order-service.yml (订单服务配置)
    └── payment-service.yml (支付服务配置)

【公共配置示例 (application.yml)】
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 600000
      max-lifetime: 1800000
      connection-timeout: 30000
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 3000
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.pdd.*.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

【服务配置示例 (order-service.yml)】
spring:
  application:
    name: order-service
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:3306/order_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USER:root}
    password: ${MYSQL_PASSWORD:root}

server:
  port: 8083

feign:
  client:
    config:
      default:
        connect-timeout: 5000
        read-timeout: 5000
      user-service:
        connect-timeout: 3000
        read-timeout: 3000
      product-service:
        connect-timeout: 3000
        read-timeout: 3000
      payment-service:
        connect-timeout: 3000
        read-timeout: 3000

seata:
  enabled: true
  tx-service-group: my_tx_group
  service:
    vgroup-mapping:
      my_tx_group: default
  registry:
    type: nacos
    nacos:
      server-addr: ${NACOS_SERVER:localhost:8848}
```

## 8. 测试策略

### 8.1 单元测试

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    单元测试策略                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

【测试框架】
├── JUnit 5：单元测试框架
├── Mockito：Mock框架
├── Spring Boot Test：Spring集成测试
└── H2 Database：内存数据库

【测试覆盖率要求】
├── 实体类：不需要测试
├── 工具类：100%覆盖率
├── Service层：≥80%覆盖率
├── Controller层：≥70%覆盖率
└── 总体覆盖率：≥75%

【测试示例】
@SpringBootTest
class OrderServiceTest {
    
    @MockBean
    private UserClient userClient;
    
    @MockBean
    private ProductClient productClient;
    
    @MockBean
    private PaymentClient paymentClient;
    
    @Autowired
    private OrderService orderService;
    
    @Test
    void testCreateOrder_Success() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(1001L);
        request.setAddressId(1L);
        request.setItems(Arrays.asList(
            new OrderItemRequest(1001L, 2)
        ));
        
        // Mock user service
        UserDTO user = new UserDTO();
        user.setId(1001L);
        user.setStatus(1);
        when(userClient.getUserById(1001L)).thenReturn(Result.success(user));
        
        // Mock address
        AddressDTO address = new AddressDTO();
        address.setId(1L);
        address.setReceiverName("张三");
        when(userClient.getAddressById(1L)).thenReturn(Result.success(address));
        
        // Mock product service
        ProductDTO product = new ProductDTO();
        product.setId(1001L);
        product.setPrice(new BigDecimal("8999.00"));
        product.setStock(100);
        when(productClient.getProductById(1001L)).thenReturn(Result.success(product));
        when(productClient.preDeductStock(any())).thenReturn(Result.success(null));
        
        // Mock payment service
        PaymentDTO payment = new PaymentDTO();
        payment.setPaymentNo("PAY202602250001");
        when(paymentClient.createPayment(any())).thenReturn(Result.success(payment));
        
        // When
        Result<OrderDTO> result = orderService.createOrder(request);
        
        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(productClient, times(1)).preDeductStock(any());
        verify(paymentClient, times(1)).createPayment(any());
    }
}
```

### 8.2 集成测试

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    集成测试策略                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

【测试环境】
├── Docker Compose：启动所有服务
├── TestContainers：容器化测试
└── WireMock：模拟第三方服务

【测试场景】
├── 订单创建流程：验证用户、商品、支付服务集成
├── 支付回调流程：验证支付、订单、商品服务集成
├── 订单取消流程：验证订单、商品、支付服务集成
└── 退款流程：验证支付、订单服务集成

【测试示例】
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.0")
        .withExposedPorts(6379);
    
    @Container
    static GenericContainer<?> nacos = new GenericContainer<>("nacos/nacos-server:2.2.0")
        .withExposedPorts(8848);
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testCreateOrder_Integration() {
        // 1. 创建用户
        UserRegisterRequest userRequest = new UserRegisterRequest();
        userRequest.setUsername("testuser");
        userRequest.setPassword("password123");
        userRequest.setPhone("13800138000");
        
        ResponseEntity<Result> userResponse = restTemplate.postForEntity(
            "/api/users/register", userRequest, Result.class);
        assertEquals(200, userResponse.getBody().getCode());
        
        // 2. 创建商品
        ProductCreateRequest productRequest = new ProductCreateRequest();
        productRequest.setName("测试商品");
        productRequest.setPrice(new BigDecimal("100.00"));
        productRequest.setStock(100);
        
        ResponseEntity<Result> productResponse = restTemplate.postForEntity(
            "/api/products", productRequest, Result.class);
        assertEquals(200, productResponse.getBody().getCode());
        
        // 3. 创建订单
        OrderCreateRequest orderRequest = new OrderCreateRequest();
        orderRequest.setUserId(1L);
        orderRequest.setItems(Arrays.asList(
            new OrderItemRequest(1L, 2)
        ));
        
        ResponseEntity<Result> orderResponse = restTemplate.postForEntity(
            "/api/orders", orderRequest, Result.class);
        assertEquals(200, orderResponse.getBody().getCode());
        
        // 4. 验证库存扣减
        ResponseEntity<Result> stockResponse = restTemplate.getForEntity(
            "/api/products/1/stock", Result.class);
        assertEquals(98, stockResponse.getBody().getData());
    }
}
```

## 9. 部署架构

### 9.1 Docker Compose部署

```yaml
version: '3.8'

services:
  # MySQL数据库
  mysql:
    image: mysql:8.0
    container_name: mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: user_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init:/docker-entrypoint-initdb.d
    networks:
      - microservices-network

  # Redis缓存
  redis:
    image: redis:7.0
    container_name: redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - microservices-network

  # Nacos注册中心
  nacos:
    image: nacos/nacos-server:2.2.0
    container_name: nacos
    environment:
      MODE: standalone
      SPRING_DATASOURCE_PLATFORM: mysql
      MYSQL_SERVICE_HOST: mysql
      MYSQL_SERVICE_PORT: 3306
      MYSQL_SERVICE_DB_NAME: nacos
      MYSQL_SERVICE_USER: root
      MYSQL_SERVICE_PASSWORD: root
    ports:
      - "8848:8848"
    depends_on:
      - mysql
    networks:
      - microservices-network

  # 用户服务
  user-service:
    build: ./user-service
    container_name: user-service
    environment:
      SPRING_PROFILES_ACTIVE: dev
      NACOS_SERVER: nacos:8848
      MYSQL_HOST: mysql
      REDIS_HOST: redis
    ports:
      - "8081:8081"
    depends_on:
      - mysql
      - redis
      - nacos
    networks:
      - microservices-network

  # 商品服务
  product-service:
    build: ./product-service
    container_name: product-service
    environment:
      SPRING_PROFILES_ACTIVE: dev
      NACOS_SERVER: nacos:8848
      MYSQL_HOST: mysql
      REDIS_HOST: redis
    ports:
      - "8082:8082"
    depends_on:
      - mysql
      - redis
      - nacos
    networks:
      - microservices-network

  # 订单服务
  order-service:
    build: ./order-service
    container_name: order-service
    environment:
      SPRING_PROFILES_ACTIVE: dev
      NACOS_SERVER: nacos:8848
      MYSQL_HOST: mysql
      REDIS_HOST: redis
    ports:
      - "8083:8083"
    depends_on:
      - mysql
      - redis
      - nacos
      - user-service
      - product-service
    networks:
      - microservices-network

  # 支付服务
  payment-service:
    build: ./payment-service
    container_name: payment-service
    environment:
      SPRING_PROFILES_ACTIVE: dev
      NACOS_SERVER: nacos:8848
      MYSQL_HOST: mysql
      REDIS_HOST: redis
    ports:
      - "8084:8084"
    depends_on:
      - mysql
      - redis
      - nacos
      - order-service
    networks:
      - microservices-network

volumes:
  mysql_data:
  redis_data:

networks:
  microservices-network:
    driver: bridge
```

### 9.2 Kubernetes部署

```yaml
# user-service-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
  namespace: microservices
spec:
  replicas: 2
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: registry.example.com/user-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: NACOS_SERVER
          value: "nacos-service:8848"
        - name: MYSQL_HOST
          value: "mysql-service"
        - name: REDIS_HOST
          value: "redis-service"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 5

---
apiVersion: v1
kind: Service
metadata:
  name: user-service
  namespace: microservices
spec:
  selector:
    app: user-service
  ports:
  - port: 8081
    targetPort: 8081
  type: ClusterIP
```

## 10. 监控与运维

### 10.1 监控指标

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    监控指标体系                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘

【系统指标】
├── CPU使用率
├── 内存使用率
├── 磁盘使用率
├── 网络流量
└── 进程数

【应用指标】
├── JVM指标
│   ├── 堆内存使用率
│   ├── 非堆内存使用率
│   ├── GC次数和时间
│   └── 线程数
│
├── HTTP指标
│   ├── 请求数 (QPS)
│   ├── 响应时间 (P50, P95, P99)
│   ├── 错误率
│   └── 并发数
│
└── 业务指标
    ├── 订单创建数
    ├── 支付成功数
    ├── 支付失败数
    └── 退款数

【服务间调用指标】
├── Feign调用次数
├── Feign调用成功率
├── Feign调用响应时间
└── 熔断次数

【告警规则】
├── CPU使用率 > 80% 持续5分钟
├── 内存使用率 > 85% 持续5分钟
├── HTTP错误率 > 5% 持续3分钟
├── 响应时间 P99 > 3秒 持续5分钟
└── 服务实例下线
```

### 10.2 日志规范

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    日志规范                                          │
└─────────────────────────────────────────────────────────────────────────────────────┘

【日志级别】
├── ERROR：错误日志，需要立即处理
├── WARN：警告日志，需要关注
├── INFO：重要业务日志
├── DEBUG：调试日志
└── TRACE：详细追踪日志

【日志格式】
{
  "timestamp": "2026-02-25T10:30:00.000+08:00",
  "level": "INFO",
  "service": "order-service",
  "traceId": "a1b2c3d4e5f6",
  "spanId": "1234567890",
  "class": "com.pdd.order.service.OrderService",
  "method": "createOrder",
  "message": "订单创建成功",
  "context": {
    "userId": 1001,
    "orderNo": "ORD202602250001",
    "amount": 17998.00
  }
}

【日志输出规范】
├── 使用JSON格式输出
├── 包含traceId和spanId用于链路追踪
├── 敏感信息脱敏（密码、手机号等）
├── 异常日志包含完整堆栈信息
└── 业务关键节点记录日志
```

## 11. 总结

### 11.1 服务关系总结

| 服务 | 角色 | 依赖服务 | 被依赖服务 | 核心职责 |
|:---|:---|:---|:---|:---|
| 用户服务 | 基础服务 | 无 | 订单服务 | 用户管理、认证授权 |
| 商品服务 | 基础服务 | 无 | 订单服务 | 商品管理、库存管理 |
| 订单服务 | 核心服务 | 用户、商品、支付 | 支付服务 | 订单编排、流程协调 |
| 支付服务 | 核心服务 | 订单（双向依赖） | 订单服务 | 支付处理、退款处理、支付状态通知 |

**重要说明：订单服务与支付服务的双向依赖关系**

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              订单服务 ⇄ 支付服务 双向依赖                             │
└─────────────────────────────────────────────────────────────────────────────────────┘

订单服务 ──────────────────────────────────────────────────────────────────────▶ 支付服务
    │                                                                              │
    │  【订单服务调用支付服务】                                                      │
    │  ├── 创建支付记录 (POST /api/payments)                                        │
    │  ├── 查询支付状态 (GET /api/payments/{paymentNo})                             │
    │  └── 申请退款 (POST /api/payments/refund)                                     │
    │                                                                              │
    │◀─────────────────────────────────────────────────────────────────────────────│
    │                                                                              │
    │  【支付服务调用订单服务】                                                      │
    │  ├── 支付成功通知 (POST /api/orders/{orderNo}/paid)                           │
    │  ├── 支付失败通知 (POST /api/orders/{orderNo}/payment-failed) ← 重要！        │
    │  └── 支付超时通知 (POST /api/orders/{orderNo}/payment-timeout) ← 重要！       │
    │                                                                              │
    └──────────────────────────────────────────────────────────────────────────────┘

【双向依赖的业务意义】
1. 支付成功 → 订单状态更新为"已支付"，确认扣减库存
2. 支付失败 → 订单状态更新为"已取消"，释放预扣库存（关键！）
3. 支付超时 → 订单状态更新为"已取消"，释放预扣库存（关键！）

【为什么需要双向依赖？】
- 订单服务需要创建支付记录，所以依赖支付服务
- 支付服务在支付状态变更时需要通知订单服务，所以依赖订单服务
- 这种双向依赖是业务流程完整性的必要设计
- 通过Feign客户端实现服务间调用，保证数据一致性
```

### 11.2 关键设计决策

1. **服务分层**：基础服务与核心服务分离，降低耦合度
2. **接口设计**：RESTful API + Feign客户端，统一调用方式
3. **数据隔离**：每个服务独立数据库，避免数据耦合
4. **分布式事务**：使用Seata保证数据一致性
5. **服务治理**：使用Nacos进行服务注册发现和配置管理
6. **监控告警**：完善的监控体系，及时发现和解决问题
7. **支付状态同步**：支付服务主动通知订单服务状态变更，确保订单与支付状态一致

### 11.3 后续优化方向

1. **性能优化**：引入缓存、异步处理、消息队列
2. **安全增强**：完善认证授权、数据加密、接口签名
3. **功能扩展**：增加营销服务、搜索服务、推荐服务
4. **运维自动化**：完善CI/CD流水线、自动化测试
5. **容灾能力**：多机房部署、数据备份、故障演练
6. **异步解耦**：考虑使用消息队列解耦支付服务与订单服务的双向依赖

---

**文档版本**：v1.0
**创建日期**：2026-02-25
**最后更新**：2026-02-25
**维护团队**：架构组
**适用项目**：电商平台微服务系统