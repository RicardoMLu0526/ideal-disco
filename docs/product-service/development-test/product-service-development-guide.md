# 产品服务开发与测试指南

## 1. 开发顺序与规划

### 1.1 开发步骤

#### 步骤1：项目初始化与配置
- **创建项目结构**：使用Spring Initializr创建Spring Boot项目
- **添加依赖**：在build.gradle中添加必要的依赖
- **配置文件**：创建application.yml和application-dev.yml
- **配置类**：创建RedisConfig、SentinelConfig等配置类

#### 步骤2：数据库设计与初始化
- **数据库表结构**：创建product、category、sku表
- **实体类**：创建Product、Category、Sku实体类
- **Mapper接口**：创建ProductMapper、CategoryMapper、SkuMapper
- **Mapper XML**：创建对应的XML映射文件
- **数据库初始化**：编写数据库初始化脚本

#### 步骤3：工具类开发
- **ProductUtil**：实现产品相关的工具方法
- **ImageUtil**：实现图片处理相关功能
- **测试**：为工具类编写单元测试

#### 步骤4：DTO与VO开发
- **DTO类**：创建ProductDTO、CategoryDTO、SkuDTO
- **VO类**：创建ProductVO、CategoryVO、SkuVO
- **测试**：为DTO和VO编写单元测试

#### 步骤5：服务层开发
- **CategoryService**：实现分类管理相关功能
- **ProductService**：实现产品管理相关功能
- **SkuService**：实现SKU管理相关功能
- **测试**：为每个服务类编写单元测试

#### 步骤6：控制器开发
- **CategoryController**：实现分类相关接口
- **ProductController**：实现产品相关接口
- **SkuController**：实现SKU相关接口
- **测试**：为每个控制器编写单元测试

#### 步骤7：主应用类开发
- **ProductServiceApplication**：创建主应用类
- **测试**：编写集成测试

### 1.2 开发与测试并行策略
- **测试驱动开发**：先编写测试用例，再实现功能
- **单元测试**：每个类开发完成后立即编写单元测试
- **集成测试**：服务层和控制器开发完成后编写集成测试
- **持续集成**：配置CI/CD流程，自动运行测试

## 2. 测试文件夹结构

```plaintext
product-service/
├── src/
│   ├── main/
│   │   ├── java/...  # 主代码
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── pdd/
│       │           └── product/
│       │               ├── config/          # 配置类测试
│       │               │   └── RedisConfigTest.java
│       │               ├── controller/      # 控制器测试
│       │               │   ├── CategoryControllerTest.java
│       │               │   ├── ProductControllerTest.java
│       │               │   └── SkuControllerTest.java
│       │               ├── service/         # 服务层测试
│       │               │   ├── CategoryServiceTest.java
│       │               │   ├── ProductServiceTest.java
│       │               │   └── SkuServiceTest.java
│       │               ├── util/            # 工具类测试
│       │               │   ├── ProductUtilTest.java
│       │               │   └── ImageUtilTest.java
│       │               ├── dto/             # DTO测试
│       │               │   ├── ProductDTOTest.java
│       │               │   └── CategoryDTOTest.java
│       │               ├── integration/      # 集成测试
│       │               │   └── ProductServiceIntegrationTest.java
│       │               └── ProductServiceApplicationTests.java  # 应用测试
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
    
    // 数据库
    implementation 'com.baomidou:mybatis-plus-boot-starter:3.5.5'
    implementation 'mysql:mysql-connector-java:8.3.0'
    
    // 工具
    implementation 'org.apache.commons:commons-lang3:3.14.0'
    implementation 'org.projectlombok:lombok:1.18.30'
    implementation 'com.alibaba:fastjson:2.0.45'
    
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
    database: 0
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml

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

# 产品服务配置
product:
  image:
    base-url: http://localhost:8082/product/images
    upload-path: /data/images
```

#### 3.1.3 配置类开发
- **RedisConfig.java**：配置RedisTemplate
- **SentinelConfig.java**：配置Sentinel限流规则

### 3.2 步骤2：数据库设计与初始化

#### 3.2.1 数据库表结构
```sql
-- 分类表
CREATE TABLE `category` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `parent_id` BIGINT DEFAULT 0,
  `level` TINYINT DEFAULT 1,
  `sort` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 产品表
CREATE TABLE `product` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `category_id` BIGINT NOT NULL,
  `brand` VARCHAR(50),
  `description` TEXT,
  `price` DECIMAL(10,2) NOT NULL,
  `stock` INT DEFAULT 0,
  `sales` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`category_id`) REFERENCES `category`(`id`)
);

-- SKU表
CREATE TABLE `sku` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `attributes` JSON NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `stock` INT DEFAULT 0,
  `sales` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
);

-- 产品图片表
CREATE TABLE `product_image` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `sort` INT DEFAULT 0,
  `is_main` TINYINT DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
);
```

#### 3.2.2 实体类开发
- **Category.java**：分类实体
- **Product.java**：产品实体
- **Sku.java**：SKU实体
- **ProductImage.java**：产品图片实体

#### 3.2.3 Mapper开发
- **CategoryMapper.java**：分类数据访问接口
- **ProductMapper.java**：产品数据访问接口
- **SkuMapper.java**：SKU数据访问接口
- **ProductImageMapper.java**：产品图片数据访问接口
- **对应的XML映射文件**

### 3.3 步骤3：工具类开发

#### 3.3.1 ProductUtil.java
```java
public class ProductUtil {
    public static String generateProductCode() {
        // 实现产品编码生成
    }
    
    public static BigDecimal calculateDiscountPrice(BigDecimal originalPrice, double discount) {
        // 实现折扣价格计算
    }
}
```

#### 3.3.2 ImageUtil.java
```java
public class ImageUtil {
    public static String uploadImage(MultipartFile file, String uploadPath) {
        // 实现图片上传
    }
    
    public static void deleteImage(String imagePath) {
        // 实现图片删除
    }
}
```

#### 3.3.3 工具类测试
- **ProductUtilTest.java**：测试产品工具类
- **ImageUtilTest.java**：测试图片工具类

### 3.4 步骤4：DTO与VO开发

#### 3.4.1 DTO类
- **CategoryDTO.java**：分类数据传输对象
- **ProductDTO.java**：产品数据传输对象
- **SkuDTO.java**：SKU数据传输对象

#### 3.4.2 VO类
- **CategoryVO.java**：分类视图对象
- **ProductVO.java**：产品视图对象
- **SkuVO.java**：SKU视图对象

#### 3.4.3 DTO与VO测试
- **CategoryDTOTest.java**：测试分类DTO
- **ProductDTOTest.java**：测试产品DTO
- **SkuDTOTest.java**：测试SKU DTO

### 3.5 步骤5：服务层开发

#### 3.5.1 CategoryService.java
```java
@Service
public class CategoryService {
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // 实现创建分类
    }
    
    public List<CategoryDTO> getCategoryList() {
        // 实现获取分类列表
    }
    
    public CategoryDTO getCategoryById(Long categoryId) {
        // 实现获取分类详情
    }
    
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        // 实现更新分类
    }
    
    public void deleteCategory(Long categoryId) {
        // 实现删除分类
    }
}
```

#### 3.5.2 ProductService.java
```java
@Service
public class ProductService {
    public ProductDTO createProduct(ProductDTO productDTO) {
        // 实现创建产品
    }
    
    public List<ProductDTO> getProductList(ProductQueryDTO queryDTO) {
        // 实现获取产品列表
    }
    
    public ProductDTO getProductById(Long productId) {
        // 实现获取产品详情
    }
    
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        // 实现更新产品
    }
    
    public void deleteProduct(Long productId) {
        // 实现删除产品
    }
    
    public void updateProductStock(Long productId, Integer stock) {
        // 实现更新产品库存
    }
}
```

#### 3.5.3 SkuService.java
```java
@Service
public class SkuService {
    public SkuDTO createSku(Long productId, SkuDTO skuDTO) {
        // 实现创建SKU
    }
    
    public List<SkuDTO> getSkuList(Long productId) {
        // 实现获取SKU列表
    }
    
    public SkuDTO getSkuById(Long skuId) {
        // 实现获取SKU详情
    }
    
    public SkuDTO updateSku(Long skuId, SkuDTO skuDTO) {
        // 实现更新SKU
    }
    
    public void deleteSku(Long skuId) {
        // 实现删除SKU
    }
    
    public void updateSkuStock(Long skuId, Integer stock) {
        // 实现更新SKU库存
    }
}
```

#### 3.5.4 服务层测试
- **CategoryServiceTest.java**：测试分类服务
- **ProductServiceTest.java**：测试产品服务
- **SkuServiceTest.java**：测试SKU服务

### 3.6 步骤6：控制器开发

#### 3.6.1 CategoryController.java
```java
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @PostMapping
    public Result<CategoryVO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        // 实现创建分类接口
    }
    
    @GetMapping
    public Result<List<CategoryVO>> getCategoryList() {
        // 实现获取分类列表接口
    }
    
    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        // 实现获取分类详情接口
    }
    
    @PutMapping("/{id}")
    public Result<CategoryVO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        // 实现更新分类接口
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        // 实现删除分类接口
    }
}
```

#### 3.6.2 ProductController.java
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @PostMapping
    public Result<ProductVO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        // 实现创建产品接口
    }
    
    @GetMapping
    public Result<List<ProductVO>> getProductList(ProductQueryDTO queryDTO) {
        // 实现获取产品列表接口
    }
    
    @GetMapping("/{id}")
    public Result<ProductVO> getProductById(@PathVariable Long id) {
        // 实现获取产品详情接口
    }
    
    @PutMapping("/{id}")
    public Result<ProductVO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        // 实现更新产品接口
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        // 实现删除产品接口
    }
    
    @PostMapping("/{id}/images")
    public Result<String> uploadProductImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        // 实现上传产品图片接口
    }
}
```

#### 3.6.3 SkuController.java
```java
@RestController
@RequestMapping("/api/products/{productId}/skus")
public class SkuController {
    @PostMapping
    public Result<SkuVO> createSku(@PathVariable Long productId, @Valid @RequestBody SkuDTO skuDTO) {
        // 实现创建SKU接口
    }
    
    @GetMapping
    public Result<List<SkuVO>> getSkuList(@PathVariable Long productId) {
        // 实现获取SKU列表接口
    }
    
    @GetMapping("/{id}")
    public Result<SkuVO> getSkuById(@PathVariable Long productId, @PathVariable Long id) {
        // 实现获取SKU详情接口
    }
    
    @PutMapping("/{id}")
    public Result<SkuVO> updateSku(@PathVariable Long productId, @PathVariable Long id, @Valid @RequestBody SkuDTO skuDTO) {
        // 实现更新SKU接口
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteSku(@PathVariable Long productId, @PathVariable Long id) {
        // 实现删除SKU接口
    }
}
```

#### 3.6.4 控制器测试
- **CategoryControllerTest.java**：测试分类控制器
- **ProductControllerTest.java**：测试产品控制器
- **SkuControllerTest.java**：测试SKU控制器

### 3.7 步骤7：主应用类开发

#### 3.7.1 ProductServiceApplication.java
```java
@SpringBootApplication
@MapperScan("com.pdd.product.mapper")
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
```

#### 3.7.2 集成测试
- **ProductServiceIntegrationTest.java**：测试完整的产品服务流程

## 4. 测试策略

### 4.1 单元测试
- **工具类测试**：测试ProductUtil和ImageUtil的核心方法
- **服务层测试**：测试CategoryService、ProductService和SkuService的业务逻辑
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

### 7.2 图片上传失败
- **解决方案**：检查文件路径权限和配置

### 7.3 产品库存更新问题
- **解决方案**：使用分布式事务或乐观锁

### 7.4 测试数据问题
- **解决方案**：使用测试数据隔离和清理机制

---

**文档版本**：v1.0
**编写日期**：2026-02-26
**编写人**：System Designer