# 微服务部署与Higress集成操作指南

本文档详细描述在编写微服务后，如何将其与本地Nacos和远程Higress网关集成，实现完整的服务调用链路。

## 一、前提条件

- ✅ 本地Nacos已运行（端口8848）
- ✅ FRP已配置并运行（本地Nacos已暴露到公网6199端口）
- ✅ 远程Higress已配置Nacos服务发现（地址：123.207.39.47:6199）

## 二、微服务编写完成后操作步骤

### 步骤1：配置微服务注册到本地Nacos

#### 1. 修改微服务配置文件
编辑微服务的 `application.yml` 文件：

```yaml
spring:
  application:
    name: demo-user-service  # 微服务名称
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # 本地Nacos地址
        namespace: public  # 命名空间
        group: DEFAULT_GROUP  # 服务分组
```

#### 2. 启动微服务

```bash
# 进入项目目录
cd /home/plus/learn/javaDemo

# 启动微服务
./gradlew bootRun --project-dir demo-user-service
```

#### 3. 验证服务注册
访问本地Nacos控制台 `http://localhost:8848/nacos`，在**服务管理** → **服务列表**中确认服务已注册。

### 步骤2：在Higress中配置路由

#### 1. 登录Higress控制台
访问 `http://123.207.39.47:18080/route` 登录Higress控制台。

#### 2. 查看服务列表
- 点击左侧菜单的**服务列表**
- 确认能看到已注册的微服务（如 `demo-user-service`）

#### 3. 创建路由规则
- 点击左侧菜单的**路由配置**
- 点击**创建路由**按钮
- 填写以下信息：
  | 配置项 | 值 |
  |-------|-----|
  | 路由名称 | user-service-route |
  | 域名 | 留空或填写访问域名 |
  | 路径 | /user/** |
  | 目标服务 | 服务发现 → nacos-discovery → demo-user-service |
  | 后端路径 | /** |
- 点击**确定**保存路由

### 步骤3：测试完整链路

#### 1. 测试服务发现
在Higress控制台的**服务列表**中，确认服务状态为**在线**。

#### 2. 测试路由访问
通过Higress访问本地微服务：

```bash
# 测试微服务API
curl http://123.207.39.47:80/user/api/getUser

# 预期响应示例
# {"code": 200, "message": "success", "data": {"id": 1, "name": "test"}}
```

#### 3. 测试完整链路
验证完整的调用链路：
- ✅ 本地微服务 → 注册到本地Nacos
- ✅ 本地Nacos → 通过FRP暴露到公网
- ✅ 远程Higress → 通过公网访问本地Nacos
- ✅ 外部请求 → 通过Higress访问本地微服务

## 三、多微服务部署

如果有多个微服务（如订单服务、产品服务等），重复以下步骤：

### 1. 为每个微服务配置Nacos注册

```yaml
spring:
  application:
    name: demo-order-service  # 订单服务
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: public
        group: DEFAULT_GROUP
```

### 2. 为每个微服务创建Higress路由

- **订单服务路由**：路径 `/order/**` → 目标服务 `demo-order-service`
- **产品服务路由**：路径 `/product/**` → 目标服务 `demo-product-service`

### 3. 启动所有微服务

```bash
# 启动订单服务
./gradlew bootRun --project-dir demo-order-service

# 启动产品服务
./gradlew bootRun --project-dir demo-product-service
```

## 四、服务间调用配置

### 1. 微服务间通过服务名调用

在微服务代码中，使用服务名进行调用：

```java
@RestController
public class OrderController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @GetMapping("/api/getOrder")
    public String getOrder() {
        // 通过服务名调用用户服务
        String userInfo = restTemplate.getForObject(
            "http://demo-user-service/user/api/getUser", 
            String.class
        );
        return "Order Service: " + userInfo;
    }
}
```

### 2. 配置RestTemplate

在微服务启动类中添加：

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

## 五、常见问题排查

### 问题1：微服务无法注册到Nacos

**排查步骤**：
1. 检查Nacos是否运行：`curl http://localhost:8848/nacos`
2. 检查微服务配置是否正确
3. 查看微服务日志：`./gradlew bootRun --project-dir demo-user-service | grep nacos`

### 问题2：Higress无法发现微服务

**排查步骤**：
1. 检查FRP映射：`curl http://123.207.39.47:6199/nacos`
2. 检查Higress服务发现配置
3. 查看Higress日志：登录控制台 → 系统配置 → 日志管理

### 问题3：路由访问失败

**排查步骤**：
1. 检查Higress路由配置
2. 测试直接访问微服务：`curl http://localhost:8080/user/api/getUser`
3. 查看Higress路由日志：登录控制台 → 路由配置 → 查看日志

### 问题4：服务间调用失败

**排查步骤**：
1. 检查服务名是否正确
2. 确认`@LoadBalanced`注解已添加
3. 查看微服务间调用日志

## 六、性能优化建议

### 1. 本地开发优化
- 使用热部署减少重启时间
- 配置IDE的自动编译功能

### 2. Nacos优化
- 开发环境关闭认证：`nacos.core.auth.enabled=false`
- 调整JVM参数：`-Xms512m -Xmx512m`

### 3. FRP优化
- 配置压缩：`compress = true`
- 调整心跳间隔：`heartbeat_interval = 30`

### 4. Higress优化
- 启用缓存：在路由配置中启用响应缓存
- 配置连接池：调整后端服务连接池大小

## 七、生产环境部署建议

### 1. 服务注册与发现
- 使用Nacos集群（至少3节点）
- 配置命名空间隔离不同环境

### 2. 网关配置
- 使用Higress集群
- 配置HTTPS
- 启用WAF防护

### 3. 监控与告警
- 集成Prometheus监控
- 配置服务健康检查
- 设置告警阈值

## 八、完整测试流程

### 测试1：本地微服务启动
```bash
./gradlew bootRun --project-dir demo-user-service
# 预期：服务启动成功，注册到Nacos
```

### 测试2：服务发现验证
```bash
curl http://123.207.39.47:6199/nacos/v1/ns/instance/list?serviceName=demo-user-service
# 预期：返回服务实例信息
```

### 测试3：Higress路由访问
```bash
curl http://123.207.39.47:80/user/api/getUser
# 预期：返回微服务响应
```

### 测试4：服务间调用
```bash
curl http://123.207.39.47:80/order/api/getOrder
# 预期：返回包含用户服务信息的订单响应
```

## 九、技术栈总结

| 组件 | 版本/配置 | 作用 |
|------|-----------|------|
| Spring Boot | 4.0.2 | 微服务基础框架 |
| Spring Cloud | 2024.0.0 | 微服务生态 |
| Nacos | 2.2.3 | 服务注册与发现 |
| FRP | 0.50.0 | 本地服务公网暴露 |
| Higress | 2.1.11 | 云原生网关 |

---

**文档更新时间**：2026-02-25
**适用场景**：本地开发环境与远程网关集成
**注意事项**：生产环境请使用更稳定的网络连接方案
