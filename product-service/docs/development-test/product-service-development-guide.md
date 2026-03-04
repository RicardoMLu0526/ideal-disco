# 产品服务开发指南

## 1. 开发环境搭建

### 1.1 环境要求

| 组件 | 版本 | 用途 |
|-----|------|------|
| JDK | 1.8+ | 运行环境 |
| Maven | 3.6+ | 构建工具 |
| MySQL | 5.7+ | 数据库 |
| Redis | 5.0+ | 缓存 |
| Elasticsearch | 7.0+ | 搜索引擎 |
| Dubbo | 2.7+ | RPC 框架 |
| RocketMQ | 4.5+ | 消息队列 |

### 1.2 项目结构

```
product-service/
├── src/
│   ├── main/
│   │   ├── java/com/pdd/product/  # 源代码
│   │   ├── resources/            # 资源文件
│   │   └── mapper/               # MyBatis 映射文件
│   └── test/                     # 测试代码
├── docs/                        # 文档
├── build.gradle                 # 构建配置
└── settings.gradle              # 项目设置
```

### 1.3 配置文件

主要配置文件：

- `application.yml`：主配置文件
- `application-dev.yml`：开发环境配置
- `application-test.yml`：测试环境配置
- `application-prod.yml`：生产环境配置

## 2. 开发流程

### 2.1 代码规范

- 遵循 Java 代码规范
- 使用 Lombok 简化代码
- 使用 MyBatis-Plus 简化数据库操作
- 遵循 RESTful API 设计规范

### 2.2 开发步骤

1. **需求分析**：理解业务需求
2. **设计**：设计接口和数据结构
3. **编码**：实现业务逻辑
4. **测试**：编写单元测试和集成测试
5. **提交**：提交代码到版本控制系统
6. **部署**：部署到测试环境
7. **验证**：验证功能是否符合需求

### 2.3 分支管理

- `master`：主分支，用于发布生产版本
- `develop`：开发分支，用于集成开发
- `feature/*`：特性分支，用于开发新功能
- `bugfix/*`：修复分支，用于修复 bug

## 3. 核心功能开发

### 3.1 产品管理

#### 3.1.1 产品创建

**接口**：`POST /api/product/create`

**请求参数**：

```json
{
  "name": "产品名称",
  "description": "产品描述",
  "categoryId": 1,
  "price": 99.99,
  "stock": 100,
  "status": 1,
  "skus": [
    {
      "skuName": "SKU 名称",
      "skuPrice": 99.99,
      "skuStock": 50,
      "attributes": {
        "color": "红色",
        "size": "M"
      }
    }
  ],
  "images": [
    {
      "url": "图片 URL",
      "type": 1
    }
  ]
}
```

**实现步骤**：

1. 验证参数
2. 创建产品主记录
3. 创建 SKU 记录
4. 创建图片记录
5. 同步到 Elasticsearch
6. 缓存产品信息

#### 3.1.2 产品更新

**接口**：`PUT /api/product/update`

**请求参数**：

```json
{
  "id": 1,
  "name": "产品名称",
  "description": "产品描述",
  "categoryId": 1,
  "price": 99.99,
  "status": 1,
  "skus": [
    {
      "id": 1,
      "skuName": "SKU