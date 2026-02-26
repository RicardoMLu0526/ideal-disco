# 商品服务设计文档

## 1. 服务概述

商品服务是电商系统的核心服务之一，负责商品的全生命周期管理，包括商品的创建、查询、更新、删除，以及商品库存管理、分类管理等功能。该服务采用微服务架构设计，与其他服务通过RESTful API进行通信。

## 2. 技术栈

| 类别 | 技术 | 版本 | 说明 |
| :--- | :--- | :--- | :--- |
| 基础框架 | Spring Boot | 4.0.3 | 应用基础框架 |
| 微服务框架 | Spring Cloud Alibaba | 2023.0.1.0 | 微服务生态 |
| 注册中心 | Nacos | 2.2.0 | 服务注册与发现 |
| 配置中心 | Nacos Config | 2.2.0 | 分布式配置管理 |
| 限流熔断 | Sentinel | 1.8.6 | 服务限流与熔断 |
| 数据库 | MySQL | 8.0 | 持久化存储 |
| 缓存 | Redis | 7.0+ | 缓存与防超卖 |
| 搜索引擎 | Elasticsearch | 8.0+ | 商品搜索 |
| ORM框架 | MyBatis-Plus | 3.5.0 | 数据库操作 |
| 日志 | Logback + SkyWalking | - | 日志收集与链路追踪 |

## 3. 目录结构

```plaintext
product-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── pdd/
│   │   │           └── product/
│   │   │               ├── ProductServiceApplication.java   # 应用启动类
│   │   │               ├── config/                           # 配置类
│   │   │               │   ├── RedisConfig.java              # Redis配置
│   │   │               │   ├── ElasticsearchConfig.java       # ES配置
│   │   │               │   └── SentinelConfig.java           # Sentinel配置
│   │   │               ├── controller/                       # 控制器
│   │   │               │   ├── ProductController.java        # 商品管理
│   │   │               │   ├── CategoryController.java       # 分类管理
│   │   │               │   └── SearchController.java         # 搜索管理
│   │   │               ├── dto/                              # 数据传输对象
│   │   │               │   ├── ProductDTO.java               # 商品DTO
│   │   │               │   ├── CategoryDTO.java              # 分类DTO
│   │   │               │   └── SearchDTO.java                # 搜索DTO
│   │   │               ├── entity/                           # 实体类
│   │   │               │   ├── Product.java                  # 商品实体
│   │   │               │   ├── Category.java                 # 分类实体
│   │   │               │   └── ProductSku.java               # 商品SKU
│   │   │               ├── mapper/                           # MyBatis映射
│   │   │               │   ├── ProductMapper.java             # 商品映射
│   │   │               │   ├── CategoryMapper.java            # 分类映射
│   │   │               │   └── ProductSkuMapper.java          # SKU映射
│   │   │               ├── service/                          # 业务逻辑
│   │   │               │   ├── ProductService.java            # 商品服务
│   │   │               │   ├── CategoryService.java           # 分类服务
│   │   │               │   ├── SearchService.java             # 搜索服务
│   │   │               │   └── InventoryService.java          # 库存服务
│   │   │               ├── util/                             # 工具类
│   │   │               │   └── RedisLockUtil.java            # Redis分布式锁
│   │   │               └── vo/                               # 视图对象
│   │   │                   ├── ProductVO.java                 # 商品VO
│   │   │                   └── SearchResultVO.java            # 搜索结果VO
│   │   └── resources/
│   │       ├── application.yml                              # 应用配置
│   │       ├── application-dev.yml                          # 开发环境配置
│   │       └── mapper/                                       # MyBatis映射文件
│   │           ├── ProductMapper.xml                        # 商品映射文件
│   │           ├── CategoryMapper.xml                       # 分类映射文件
│   │           └── ProductSkuMapper.xml                     # SKU映射文件
│   └── test/                                                 # 测试代码
├── pom.xml                                                   # Maven依赖
└── Dockerfile                                                # Docker构建文件
```

## 4. 核心功能

### 4.1 商品管理

- **商品CRUD**：商品的创建、查询、更新、删除
- **商品SKU管理**：商品规格、价格、库存管理
- **商品分类**：商品分类的创建、查询、更新、删除
- **商品上下架**：商品的上架、下架操作

### 4.2 库存管理

- **库存查询**：实时查询商品库存
- **库存扣减**：下单时扣减库存
- **库存回滚**：订单取消时回滚库存
- **防超卖**：使用Redis分布式锁防止超卖

### 4.3 搜索功能

- **商品搜索**：基于Elasticsearch的全文搜索
- **搜索建议**：根据用户输入提供搜索建议
- **热门搜索**：统计热门搜索词
- **搜索历史**：记录用户搜索历史

### 4.4 限流熔断

- **接口限流**：使用Sentinel对接口进行限流
- **服务熔断**：当服务异常时进行熔断保护

## 5. 数据库设计

### 5.1 商品表 (`product`)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 商品ID |
| `name` | `VARCHAR(200)` | `NOT NULL` | 商品名称 |
| `description` | `TEXT` | | 商品描述 |
| `category_id` | `BIGINT` | `NOT NULL` | 分类ID |
| `brand_id` | `BIGINT` | | 品牌ID |
| `main_image` | `VARCHAR(255)` | | 主图URL |
| `price` | `DECIMAL(10,2)` | `NOT NULL` | 价格 |
| `stock` | `INT` | `DEFAULT 0` | 库存 |
| `status` | `TINYINT` | `DEFAULT 1` | 状态（1:上架,0:下架） |
| `sales` | `INT` | `DEFAULT 0` | 销量 |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 5.2 商品SKU表 (`product_sku`)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | SKU ID |
| `product_id` | `BIGINT` | `NOT NULL` | 商品ID |
| `sku_code` | `VARCHAR(50)` | `UNIQUE NOT NULL` | SKU编码 |
| `attributes` | `JSON` | `NOT NULL` | SKU属性（JSON格式） |
| `price` | `DECIMAL(10,2)` | `NOT NULL` | SKU价格 |
| `stock` | `INT` | `DEFAULT 0` | SKU库存 |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 5.3 分类表 (`category`)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 分类ID |
| `name` | `VARCHAR(50)` | `NOT NULL` | 分类名称 |
| `parent_id` | `BIGINT` | `DEFAULT 0` | 父分类ID |
| `level` | `TINYINT` | `DEFAULT 1` | 分类级别 |
| `sort` | `INT` | `DEFAULT 0` | 排序值 |
| `icon` | `VARCHAR(255)` | | 分类图标 |
| `status` | `TINYINT` | `DEFAULT 1` | 状态（1:启用,0:禁用） |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

## 6. API设计

### 6.1 商品相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/products` | `GET` | `ProductController` | 获取商品列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/products/{id}` | `GET` | `ProductController` | 获取商品详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/products` | `POST` | `ProductController` | 创建商品 | `{"name": "...", "description": "...", "price": 99.99, "stock": 100}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/products/{id}` | `PUT` | `ProductController` | 更新商品 | `{"name": "...", "price": 89.99}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/products/{id}` | `DELETE` | `ProductController` | 删除商品 | N/A | `{"code": 200, "message": "success"}` |
| `/api/products/{id}/status` | `PUT` | `ProductController` | 修改商品状态 | `{"status": 1}` | `{"code": 200, "message": "success"}` |
| `/api/products/{id}/stock` | `GET` | `ProductController` | 获取商品库存 | N/A | `{"code": 200, "message": "success", "data": {"stock": 100}}` |

### 6.2 分类相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/categories` | `GET` | `CategoryController` | 获取分类列表 | N/A | `{"code": 200, "message": "success", "data": [{...}, {...}]}` |
| `/api/categories/{id}` | `GET` | `CategoryController` | 获取分类详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/categories` | `POST` | `CategoryController` | 创建分类 | `{"name": "...", "parentId": 0, "level": 1}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/categories/{id}` | `PUT` | `CategoryController` | 更新分类 | `{"name": "...", "sort": 10}` | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/categories/{id}` | `DELETE` | `CategoryController` | 删除分类 | N/A | `{"code": 200, "message": "success"}` |

### 6.3 搜索相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/search` | `GET` | `SearchController` | 商品搜索 | `?keyword=手机&page=1&size=10` | `{"code": 200, "message": "success", "data": {"total": 100, "products": [{...}, {...}]}}` |
| `/api/search/suggest` | `GET` | `SearchController` | 搜索建议 | `?keyword=手` | `{"code": 200, "message": "success", "data": ["手机", "手表", "手环"]}` |
| `/api/search/hot` | `GET` | `SearchController` | 热门搜索 | N/A | `{"code": 200, "message": "success", "data": ["手机", "电脑", "耳机"]}` |
| `/api/search/history` | `GET` | `SearchController` | 搜索历史 | N/A | `{"code": 200, "message": "success", "data": ["手机", "电脑"]}` |

## 7. 核心配置

### 7.1 应用配置 (`application.yml`)

```yaml
spring:
  application:
    name: product-service
  datasource:
    url: jdbc:mysql://localhost:3306/pdd_product?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
    password:
    database: 1
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml
  elasticsearch:
    uris: http://localhost:9200
    username: elastic
    password: changeme

server:
  port: 8082
  servlet:
    context-path: /product

# Sentinel配置
sentinel:
  transport:
    dashboard: localhost:8080
  eager: true

# 日志配置
logging:
  level:
    com.pdd.product: info
  config:
    classpath: logback-spring.xml
```

### 7.2 Elasticsearch配置

```java
@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }

    @Bean
    public RestClientBuilder restClientBuilder(
            @Value("${spring.elasticsearch.uris}") String uris,
            @Value("${spring.elasticsearch.username}") String username,
            @Value("${spring.elasticsearch.password}") String password) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
        );

        return RestClient.builder(HttpHost.create(uris))
                .setHttpClientConfigCallback(httpClientBuilder -> 
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                );
    }
}
```

## 8. 核心代码

### 8.1 商品服务

```java
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private RestHighLevelClient esClient;

    /**
     * 获取商品详情
     */
    public ProductVO getProductDetail(Long productId) {
        // 1. 从数据库获取商品信息
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 2. 获取商品SKU信息
        List<ProductSku> skus = productSkuMapper.selectByProductId(productId);

        // 3. 构建返回结果
        ProductVO productVO = ProductVO.fromEntity(product);
        productVO.setSkus(ProductSkuVO.fromEntities(skus));
        return productVO;
    }

    /**
     * 商品上下架
     */
    public void updateProductStatus(Long productId, Integer status) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        productMapper.updateById(product);

        // 如果上架，同步到ES
        if (status == 1) {
            syncProductToEs(productId);
        } else {
            // 如果下架，从ES删除
            deleteProductFromEs(productId);
        }
    }

    /**
     * 同步商品到ES
     */
    private void syncProductToEs(Long productId) {
        // 实现商品同步到ES的逻辑
    }

    /**
     * 从ES删除商品
     */
    private void deleteProductFromEs(Long productId) {
        // 实现从ES删除商品的逻辑
    }
}
```

### 8.2 库存服务

```java
@Service
public class InventoryService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 扣减库存
     */
    @Transactional
    public boolean deductStock(Long productId, Integer quantity) {
        // 1. 获取分布式锁
        String lockKey = "lock:product:stock:" + productId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 3, TimeUnit.SECONDS);
        if (!locked) {
            throw new BusinessException("系统繁忙，请稍后重试");
        }

        try {
            // 2. 查询商品库存
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new BusinessException("商品不存在");
            }

            // 3. 检查库存是否充足
            if (product.getStock() < quantity) {
                throw new BusinessException("商品库存不足");
            }

            // 4. 扣减库存
            product.setStock(product.getStock() - quantity);
            productMapper.updateById(product);

            // 5. 更新Redis缓存
            redisTemplate.opsForValue().set("product:stock:" + productId, product.getStock());
            return true;
        } finally {
            // 6. 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 回滚库存
     */
    @Transactional
    public void rollbackStock(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            product.setStock(product.getStock() + quantity);
            productMapper.updateById(product);
            // 更新Redis缓存
            redisTemplate.opsForValue().set("product:stock:" + productId, product.getStock());
        }
    }

    /**
     * 获取商品库存
     */
    public Integer getStock(Long productId) {
        // 先从Redis获取
        Integer stock = (Integer) redisTemplate.opsForValue().get("product:stock:" + productId);
        if (stock != null) {
            return stock;
        }

        // 从数据库获取
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return 0;
        }

        // 缓存到Redis
        redisTemplate.opsForValue().set("product:stock:" + productId, product.getStock());
        return product.getStock();
    }
}
```

### 8.3 搜索服务

```java
@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 商品搜索
     */
    public SearchResult search(String keyword, Integer page, Integer size) {
        // 1. 构建搜索请求
        SearchRequest searchRequest = new SearchRequest("product_index");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 2. 设置搜索条件
        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, "name", "description"));
        sourceBuilder.from((page - 1) * size);
        sourceBuilder.size(size);

        // 3. 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse;
        try {
            searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("搜索失败", e);
        }

        // 4. 处理搜索结果
        SearchResult result = new SearchResult();
        result.setTotal(searchResponse.getHits().getTotalHits().value);

        List<ProductSearchVO> products = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            ProductSearchVO product = new ProductSearchVO();
            product.setId(Long.valueOf(hit.getId()));
            product.setName((String) sourceAsMap.get("name"));
            product.setPrice((Double) sourceAsMap.get("price"));
            product.setMainImage((String) sourceAsMap.get("mainImage"));
            product.setSales((Integer) sourceAsMap.get("sales"));
            products.add(product);
        }
        result.setProducts(products);

        // 5. 记录搜索历史
        recordSearchHistory(keyword);

        return result;
    }

    /**
     * 记录搜索历史
     */
    private void recordSearchHistory(String keyword) {
        // 简单实现：使用Redis记录热门搜索词
        redisTemplate.opsForValue().increment("search:hot:" + keyword);
    }

    /**
     * 获取热门搜索
     */
    public List<String> getHotSearch() {
        // 从Redis获取热门搜索词
        Set<String> keys = redisTemplate.keys("search:hot:*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        // 排序并返回前10个
        return keys.stream()
                .map(key -> key.substring(11)) // 去掉前缀 "search:hot:"
                .sorted((a, b) -> {
                    Integer countA = (Integer) redisTemplate.opsForValue().get("search:hot:" + a);
                    Integer countB = (Integer) redisTemplate.opsForValue().get("search:hot:" + b);
                    return countB.compareTo(countA);
                })
                .limit(10)
                .collect(Collectors.toList());
    }
}
```

## 9. 集成方案

### 9.1 服务注册与发现

- 使用Nacos作为服务注册中心，商品服务启动时自动注册到Nacos
- 其他服务通过服务名调用商品服务

### 9.2 网关集成

- API网关通过Nacos发现商品服务
- 网关统一处理认证、限流、日志等横切关注点
- 路由规则配置：将`/product/**`路径路由到商品服务

### 9.3 订单服务集成

- 订单服务调用商品服务的库存扣减接口
- 使用分布式事务确保订单创建和库存扣减的一致性

### 9.4 搜索集成

- 商品服务负责将商品数据同步到Elasticsearch
- 搜索服务直接查询Elasticsearch获取搜索结果

## 10. 部署方案

### 10.1 容器化部署

- 使用Docker容器化商品服务
- 配置Docker Compose实现多服务编排

### 10.2 集群部署

- 部署多个商品服务实例
- 通过Nacos实现服务发现和负载均衡
- Elasticsearch部署为集群模式，提高搜索性能和可靠性

## 11. 注意事项

1. **防超卖**：使用Redis分布式锁确保库存扣减的原子性
2. **数据一致性**：确保数据库和Redis缓存的库存数据一致性
3. **搜索性能**：合理设计Elasticsearch索引，优化搜索查询
4. **并发处理**：考虑高并发场景下的性能优化
5. **接口限流**：对敏感接口（如库存扣减）进行限流保护
6. **数据同步**：确保商品数据在MySQL和Elasticsearch之间的同步
7. **异常处理**：妥善处理各种异常情况，避免服务崩溃
8. **监控告警**：对库存异常、搜索服务异常等情况进行监控

## 12. 扩展计划

1. **商品评论**：集成商品评论功能
2. **商品推荐**：基于用户行为的商品推荐
3. **商品规格**：支持更复杂的商品规格管理
4. **多维度搜索**：支持按价格、销量、评分等维度搜索
5. **图片搜索**：支持基于图片的商品搜索
6. **库存预警**：实现库存低于阈值时的预警功能
7. **商品审核**：集成商品审核流程
8. **价格变动**：记录商品价格变动历史

---

**文档版本**：v1.0
**编写日期**：2026-02-24
**编写人**：System Designer