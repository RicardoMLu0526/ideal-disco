# 微服务接口设计文档

## 1. 概述

本文档基于电商系统的微服务架构设计，包含用户服务、商品服务、订单服务和支付服务的完整接口设计。所有接口采用RESTful风格，统一响应格式，确保系统的一致性和可维护性。

## 2. 技术栈

| 类别 | 技术 | 版本 | 说明 |
| :--- | :--- | :--- | :--- |
| 基础框架 | Spring Boot | 3.2.0 | 应用基础框架 |
| 微服务框架 | Spring Cloud Alibaba | 2022.0.0.0 | 微服务生态 |
| 注册中心 | Nacos | 2.2.0 | 服务注册与发现 |
| 配置中心 | Nacos Config | 2.2.0 | 分布式配置管理 |
| 分布式事务 | Seata | 1.6.0 | 分布式事务管理 |
| 限流熔断 | Sentinel | 1.8.6 | 服务限流与熔断 |
| 数据库 | MySQL | 8.0 | 持久化存储 |
| 缓存 | Redis | 7.0+ | 缓存与预扣库存 |
| 搜索引擎 | Elasticsearch | 8.0+ | 商品搜索 |
| 认证 | Spring Security | 6.2.0 | 安全认证框架 |

## 3. 统一响应格式

所有接口返回统一的响应格式：

```json
{
  "code": 200,           // 状态码
  "message": "success",  // 响应消息
  "data": {...}          // 响应数据
}
```

## 4. 用户服务接口

### 4.1 认证相关接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/auth/register` | `POST` | 用户注册 | `{"username": "...", "password": "...", "phone": "..."}` | `{"code": 200, "message": "success", "data": {"userId": 1, "token": "..."}}` |
| `/api/auth/login` | `POST` | 用户登录 | `{"username": "...", "password": "..."}` | `{"code": 200, "message": "success", "data": {"userId": 1, "token": "...", "userInfo": {...}}}` |
| `/api/auth/logout` | `POST` | 用户登出 | N/A | `{"code": 200, "message": "success"}` |
| `/api/auth/refresh` | `POST` | 刷新令牌 | `{"token": "..."}` | `{"code": 200, "message": "success", "data": {"token": "..."}}` |

### 4.2 用户管理接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/users` | `GET` | 获取用户列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/users/{id}` | `GET` | 获取用户详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{id}` | `PUT` | 更新用户信息 | `{"nickname": "...", "avatar": "..."}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{id}` | `DELETE` | 删除用户 | N/A | `{"code": 200, "message": "success"}` |
| `/api/users/{id}/status` | `PUT` | 修改用户状态 | `{"status": 1}` | `{"code": 200, "message": "success"}` |

### 4.3 地址管理接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/users/{userId}/addresses` | `GET` | 获取用户地址列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/users/{userId}/addresses` | `POST` | 添加收货地址 | `{"name": "...", "phone": "...", "province": "...", "city": "...", "district": "...", "detail": "...", "isDefault": true}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{userId}/addresses/{id}` | `GET` | 获取地址详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{userId}/addresses/{id}` | `PUT` | 更新地址信息 | `{"name": "...", "phone": "...", "province": "...", "city": "...", "district": "...", "detail": "..."}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/users/{userId}/addresses/{id}` | `DELETE` | 删除收货地址 | N/A | `{"code": 200, "message": "success"}` |
| `/api/users/{userId}/addresses/{id}/default` | `PUT` | 设置默认地址 | N/A | `{"code": 200, "message": "success"}` |

## 5. 商品服务接口

### 5.1 商品管理接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/products` | `GET` | 获取商品列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/products/{id}` | `GET` | 获取商品详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/products` | `POST` | 创建商品 | `{"name": "...", "description": "...", "price": 99.99, "stock": 100}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/products/{id}` | `PUT` | 更新商品 | `{"name": "...", "price": 89.99}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/products/{id}` | `DELETE` | 删除商品 | N/A | `{"code": 200, "message": "success"}` |
| `/api/products/{id}/status` | `PUT` | 修改商品状态 | `{"status": 1}` | `{"code": 200, "message": "success"}` |
| `/api/products/{id}/stock` | `GET` | 获取商品库存 | N/A | `{"code": 200, "message": "success", "data": {"stock": 100}}` |

### 5.2 分类管理接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/categories` | `GET` | 获取分类列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/categories/{id}` | `GET` | 获取分类详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/categories` | `POST` | 创建分类 | `{"name": "...", "parentId": 0, "level": 1}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/categories/{id}` | `PUT` | 更新分类 | `{"name": "...", "sort": 10}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/categories/{id}` | `DELETE` | 删除分类 | N/A | `{"code": 200, "message": "success"}` |

### 5.3 搜索相关接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/search` | `GET` | 商品搜索 | `?keyword=手机&page=1&size=10` | `{"code": 200, "message": "success", "data": {"total": 100, "products": [{...}, {...}]}}` |
| `/api/search/suggest` | `GET` | 搜索建议 | `?keyword=手` | `{"code": 200, "message": "success", "data": ["手机", "手表", "手环"]}` |
| `/api/search/hot` | `GET` | 热门搜索 | N/A | `{"code": 200, "message": "success", "data": ["手机", "电脑", "耳机"]}` |
| `/api/search/history` | `GET` | 搜索历史 | N/A | `{"code": 200, "message": "success", "data": ["手机", "电脑"]}` |

## 6. 订单服务接口

### 6.1 订单管理接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/orders` | `GET` | 获取订单列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/orders/{id}` | `GET` | 获取订单详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/orders` | `POST` | 创建订单 | `{"userId": 1, "items": [{"productId": 1, "quantity": 2}], "addressId": 1}` | `{"code": 200, "message": "success", "data": {"orderId": 1, "orderNo": "20260224000001"}}` |
| `/api/orders/{id}` | `PUT` | 更新订单 | `{"orderStatus": 1}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/orders/{id}` | `DELETE` | 删除订单 | N/A | `{"code": 200, "message": "success"}` |
| `/api/orders/{id}/cancel` | `POST` | 取消订单 | N/A | `{"code": 200, "message": "success"}` |
| `/api/orders/{id}/pay` | `POST` | 支付订单 | `{"paymentMethod": "wechat"}` | `{"code": 200, "message": "success", "data": {"paymentUrl": "..."}}` |
| `/api/orders/{id}/confirm` | `POST` | 确认收货 | N/A | `{"code": 200, "message": "success"}` |

### 6.2 订单项管理接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/orders/{orderId}/items` | `GET` | 获取订单项列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/orders/{orderId}/items/{id}` | `GET` | 获取订单项详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |

## 7. 支付服务接口

### 7.1 支付管理接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/payments` | `POST` | 创建支付 | `{"orderId": 1, "amount": 99.99, "paymentMethod": "wechat"}` | `{"code": 200, "message": "success", "data": {"paymentId": 1, "paymentNo": "P20260224000001", "paymentUrl": "..."}}` |
| `/api/payments/{id}` | `GET` | 获取支付详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/payments/{id}/status` | `GET` | 获取支付状态 | N/A | `{"code": 200, "message": "success", "data": {"status": 1, "message": "已支付"}}` |
| `/api/payments/{id}/close` | `POST` | 关闭支付 | N/A | `{"code": 200, "message": "success"}` |
| `/api/payments/notify/wechat` | `POST` | 微信支付回调 | XML格式 | `{"code": "SUCCESS", "message": "成功"}` |
| `/api/payments/notify/alipay` | `POST` | 支付宝回调 | 表单格式 | `success` |

### 7.2 退款管理接口

| API路径 | 方法 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- |
| `/api/refunds` | `POST` | 创建退款 | `{"paymentId": 1, "refundAmount": 99.99, "refundReason": "商品质量问题"}` | `{"code": 200, "message": "success", "data": {"refundId": 1, "refundNo": "R20260224000001"}}` |
| `/api/refunds/{id}` | `GET` | 获取退款详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/refunds/{id}/status` | `GET` | 获取退款状态 | N/A | `{"code": 200, "message": "success", "data": {"status": 1, "message": "退款成功"}}` |
| `/api/refunds/notify/wechat` | `POST` | 微信退款回调 | XML格式 | `{"code": "SUCCESS", "message": "成功"}` |
| `/api/refunds/notify/alipay` | `POST` | 支付宝退款回调 | 表单格式 | `success` |

## 8. 服务集成

### 8.1 服务注册与发现
- 使用Nacos作为服务注册中心
- 所有服务启动时自动注册到Nacos
- 服务间通过服务名进行调用

### 8.2 API网关集成
- API网关通过Nacos发现所有服务
- 统一处理认证、限流、日志等横切关注点
- 路由规则配置：
  - `/user/**` → 用户服务
  - `/product/**` → 商品服务
  - `/order/**` → 订单服务
  - `/payment/**` → 支付服务

### 8.3 服务间调用
- 使用Feign进行服务间调用
- 使用Sentinel进行服务限流与熔断
- 使用SkyWalking进行链路追踪

## 9. 安全设计

### 9.1 认证与授权
- 使用JWT进行无状态认证
- 密码使用BCrypt加密存储
- 基于角色的权限控制

### 9.2 数据安全
- 使用HTTPS确保数据传输安全
- 对敏感数据进行脱敏处理
- 防止SQL注入、XSS攻击、CSRF攻击

### 9.3 支付安全
- 严格验证第三方支付平台的回调签名
- 确保支付操作的幂等性
- 详细记录支付日志，便于审计

## 10. 性能优化

### 10.1 缓存策略
- 使用Redis缓存热点数据
- 使用Redis预扣库存，提高并发性能
- 合理设置缓存过期时间

### 10.2 数据库优化
- 合理设计数据库索引
- 使用MyBatis-Plus的批量操作
- 考虑分库分表处理大数据量

### 10.3 搜索优化
- 使用Elasticsearch优化商品搜索
- 合理设计Elasticsearch索引
- 实现搜索结果分页

## 11. 可靠性设计

### 11.1 分布式事务
- 使用Seata处理跨服务的分布式事务
- 确保订单创建、库存扣减、支付记录的一致性

### 11.2 服务容错
- 使用Sentinel进行服务限流与熔断
- 实现服务降级策略
- 部署多个服务实例实现高可用

### 11.3 异常处理
- 统一异常处理机制
- 详细记录异常日志
- 对关键操作进行监控和告警

## 12. 部署方案

### 12.1 容器化部署
- 使用Docker容器化所有服务
- 使用Docker Compose实现多服务编排
- 配置Docker网络实现服务间通信

### 12.2 集群部署
- 部署多个服务实例实现高可用
- 通过Nacos实现服务发现和负载均衡
- 配置合理的健康检查机制

### 12.3 监控与告警
- 使用Prometheus监控服务状态
- 使用Grafana展示监控指标
- 配置合理的告警规则

## 13. 文档版本

| 版本 | 编写日期 | 编写人 | 说明 |
| :--- | :--- | :--- | :--- |
| v1.0 | 2026-02-25 | System Designer | 初始版本 |

---

**注**：本文档基于提供的服务设计文档整理，包含了所有核心接口的设计和实现方案。在实际开发中，应根据具体业务需求进行适当调整和扩展。