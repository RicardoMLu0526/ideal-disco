# 支付服务设计文档

## 1. 服务概述

支付服务是电商系统的核心服务之一，负责处理各种支付相关的功能，包括支付订单、退款处理、支付状态查询等。该服务采用微服务架构设计，与其他服务通过RESTful API进行通信。

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
| 缓存 | Redis | 7.0+ | 缓存支付单 |
| 日志 | Logback + SkyWalking | - | 日志收集与链路追踪 |
| 第三方支付 | 微信支付、支付宝 | - | 第三方支付SDK |

## 3. 目录结构

```plaintext
payment-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── pdd/
│   │   │           └── payment/
│   │   │               ├── PaymentServiceApplication.java   # 应用启动类
│   │   │               ├── config/                           # 配置类
│   │   │               │   ├── RedisConfig.java              # Redis配置
│   │   │               │   ├── SeataConfig.java              # Seata配置
│   │   │               │   ├── SentinelConfig.java           # Sentinel配置
│   │   │               │   ├── WechatPayConfig.java          # 微信支付配置
│   │   │               │   └── AlipayConfig.java             # 支付宝配置
│   │   │               ├── controller/                       # 控制器
│   │   │               │   ├── PaymentController.java        # 支付管理
│   │   │               │   ├── RefundController.java         # 退款管理
│   │   │               │   └── NotifyController.java         # 支付回调
│   │   │               ├── dto/                              # 数据传输对象
│   │   │               │   ├── PaymentDTO.java               # 支付DTO
│   │   │               │   ├── RefundDTO.java                # 退款DTO
│   │   │               │   └── NotifyDTO.java                # 回调DTO
│   │   │               ├── entity/                           # 实体类
│   │   │               │   ├── Payment.java                  # 支付实体
│   │   │               │   └── Refund.java                   # 退款实体
│   │   │               ├── mapper/                           # MyBatis映射
│   │   │               │   ├── PaymentMapper.java             # 支付映射
│   │   │               │   └── RefundMapper.java              # 退款映射
│   │   │               ├── service/                          # 业务逻辑
│   │   │               │   ├── PaymentService.java            # 支付服务
│   │   │               │   ├── RefundService.java             # 退款服务
│   │   │               │   ├── WechatPayService.java          # 微信支付
│   │   │               │   ├── AlipayService.java             # 支付宝
│   │   │               │   └── NotifyService.java             # 回调服务
│   │   │               ├── util/                             # 工具类
│   │   │               │   ├── SignatureUtil.java            # 签名工具
│   │   │               │   └── PaymentUtil.java              # 支付工具
│   │   │               └── vo/                               # 视图对象
│   │   │                   ├── PaymentVO.java                 # 支付VO
│   │   │                   └── RefundVO.java                  # 退款VO
│   │   └── resources/
│   │       ├── application.yml                              # 应用配置
│   │       ├── application-dev.yml                          # 开发环境配置
│   │       └── mapper/                                       # MyBatis映射文件
│   │           ├── PaymentMapper.xml                        # 支付映射文件
│   │           └── RefundMapper.xml                         # 退款映射文件
│   └── test/                                                 # 测试代码
├── pom.xml                                                   # Maven依赖
└── Dockerfile                                                # Docker构建文件
```

## 4. 核心功能

### 4.1 支付管理

- **支付创建**：创建支付订单，生成支付链接
- **支付查询**：查询支付状态、支付详情
- **支付回调**：处理第三方支付平台的回调通知
- **支付关闭**：关闭未支付的支付订单

### 4.2 退款管理

- **退款申请**：创建退款申请
- **退款查询**：查询退款状态、退款详情
- **退款回调**：处理第三方支付平台的退款回调

### 4.3 第三方支付集成

- **微信支付**：集成微信支付SDK，支持扫码支付、H5支付等
- **支付宝**：集成支付宝SDK，支持扫码支付、手机网站支付等
- **支付方式管理**：管理各种支付方式的配置

### 4.4 分布式事务

- **支付处理**：确保支付操作与订单状态更新的一致性
- **退款处理**：确保退款操作与订单状态更新的一致性

### 4.5 缓存管理

- **支付单缓存**：使用Redis缓存支付单信息，提高查询性能
- **支付令牌缓存**：缓存支付令牌，防止重复支付

### 4.6 限流熔断

- **接口限流**：使用Sentinel对接口进行限流
- **服务熔断**：当服务异常时进行熔断保护

## 5. 数据库设计

### 5.1 支付表 (`payment`)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 支付ID |
| `payment_no` | `VARCHAR(32)` | `UNIQUE NOT NULL` | 支付单号 |
| `order_id` | `BIGINT` | `NOT NULL` | 订单ID |
| `order_no` | `VARCHAR(32)` | `NOT NULL` | 订单号 |
| `user_id` | `BIGINT` | `NOT NULL` | 用户ID |
| `amount` | `DECIMAL(10,2)` | `NOT NULL` | 支付金额 |
| `payment_method` | `VARCHAR(20)` | `NOT NULL` | 支付方式（wechat, alipay） |
| `payment_status` | `TINYINT` | `DEFAULT 0` | 支付状态（0:待支付,1:已支付,2:支付失败,3:已退款） |
| `transaction_id` | `VARCHAR(100)` | | 第三方交易ID |
| `create_time` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `pay_time` | `TIMESTAMP` | | 支付时间 |
| `expire_time` | `TIMESTAMP` | | 过期时间 |
| `notify_time` | `TIMESTAMP` | | 回调时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 5.2 退款表 (`refund`)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 退款ID |
| `refund_no` | `VARCHAR(32)` | `UNIQUE NOT NULL` | 退款单号 |
| `payment_id` | `BIGINT` | `NOT NULL` | 支付ID |
| `order_id` | `BIGINT` | `NOT NULL` | 订单ID |
| `user_id` | `BIGINT` | `NOT NULL` | 用户ID |
| `refund_amount` | `DECIMAL(10,2)` | `NOT NULL` | 退款金额 |
| `refund_reason` | `VARCHAR(255)` | | 退款原因 |
| `refund_status` | `TINYINT` | `DEFAULT 0` | 退款状态（0:待处理,1:退款成功,2:退款失败） |
| `transaction_id` | `VARCHAR(100)` | | 第三方退款交易ID |
| `create_time` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `refund_time` | `TIMESTAMP` | | 退款时间 |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

## 6. API设计

### 6.1 支付相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/payments` | `POST` | `PaymentController` | 创建支付 | `{"orderId": 1, "amount": 99.99, "paymentMethod": "wechat"}` | `{"code": 200, "message": "success", "data": {"paymentId": 1, "paymentNo": "P20260224000001", "paymentUrl": "..."}}` |
| `/api/payments/{id}` | `GET` | `PaymentController` | 获取支付详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/payments/{id}/status` | `GET` | `PaymentController` | 获取支付状态 | N/A | `{"code": 200, "message": "success", "data": {"status": 1, "message": "已支付"}}` |
| `/api/payments/{id}/close` | `POST` | `PaymentController` | 关闭支付 | N/A | `{"code": 200, "message": "success"}` |
| `/api/payments/notify/wechat` | `POST` | `NotifyController` | 微信支付回调 | XML格式 | `{"code": "SUCCESS", "message": "成功"}` |
| `/api/payments/notify/alipay` | `POST` | `NotifyController` | 支付宝回调 | 表单格式 | `success` |

### 6.2 退款相关

| API路径 | 方法 | 模块/类 | 功能描述 | 请求体 (JSON) | 成功响应 (200 OK) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `/api/refunds` | `POST` | `RefundController` | 创建退款 | `{"paymentId": 1, "refundAmount": 99.99, "refundReason": "商品质量问题"}` | `{"code": 200, "message": "success", "data": {"refundId": 1, "refundNo": "R20260224000001"}}` |
| `/api/refunds/{id}` | `GET` | `RefundController` | 获取退款详情 | N/A | `{"code": 200, "message": "success", "data": {...}}` |
| `/api/refunds/{id}/status` | `GET` | `RefundController` | 获取退款状态 | N/A | `{"code": 200, "message": "success", "data": {"status": 1, "message": "退款成功"}}` |
| `/api/refunds/notify/wechat` | `POST` | `NotifyController` | 微信退款回调 | XML格式 | `{"code": "SUCCESS", "message": "成功"}` |
| `/api/refunds/notify/alipay` | `POST` | `NotifyController` | 支付宝退款回调 | 表单格式 | `success` |

## 7. 核心配置

### 7.1 应用配置 (`application.yml`)

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

### 7.2 微信支付配置

```java
@Configuration
public class WechatPayConfig {

    @Value("${wechat.pay.app-id}")
    private String appId;

    @Value("${wechat.pay.mch-id}")
    private String mchId;

    @Value("${wechat.pay.mch-key}")
    private String mchKey;

    @Value("${wechat.pay.notify-url}")
    private String notifyUrl;

    @Bean
    public WXPay wxPay() {
        WXPayConfig config = new WXPayConfig() {
            @Override
            public String getAppID() {
                return appId;
            }

            @Override
            public String getMchID() {
                return mchId;
            }

            @Override
            public String getKey() {
                return mchKey;
            }

            @Override
            public InputStream getCertStream() {
                return null;
            }

            @Override
            public int getHttpConnectTimeoutMs() {
                return 8000;
            }

            @Override
            public int getHttpReadTimeoutMs() {
                return 10000;
            }
        };
        return new WXPay(config);
    }

    @Bean
    public String wechatNotifyUrl() {
        return notifyUrl;
    }
}
```

### 7.3 支付宝配置

```java
@Configuration
public class AlipayConfig {

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.app-private-key}")
    private String appPrivateKey;

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    @Value("${alipay.return-url}")
    private String returnUrl;

    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                appId,
                appPrivateKey,
                "json",
                "UTF-8",
                alipayPublicKey,
                "RSA2"
        );
    }

    @Bean
    public String alipayNotifyUrl() {
        return notifyUrl;
    }

    @Bean
    public String alipayReturnUrl() {
        return returnUrl;
    }
}
```

## 8. 核心代码

### 8.1 支付服务

```java
@Service
public class PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 创建支付
     */
    @GlobalTransactional(name = "create-payment", rollbackFor = Exception.class)
    public PaymentCreateResult createPayment(PaymentCreateDTO createDTO) {
        // 1. 生成支付ID和支付单号
        Long paymentId = idGenerator.nextId();
        String paymentNo = "P" + OrderNoGenerator.generate();

        // 2. 创建支付记录
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setPaymentNo(paymentNo);
        payment.setOrderId(createDTO.getOrderId());
        payment.setOrderNo(createDTO.getOrderNo());
        payment.setUserId(createDTO.getUserId());
        payment.setAmount(createDTO.getAmount());
        payment.setPaymentMethod(createDTO.getPaymentMethod());
        payment.setPaymentStatus(0); // 待支付
        payment.setExpireTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000)); // 30分钟过期
        paymentMapper.insert(payment);

        // 3. 生成支付链接
        String paymentUrl;
        if ("wechat".equals(createDTO.getPaymentMethod())) {
            paymentUrl = wechatPayService.createPayment(payment);
        } else if ("alipay".equals(createDTO.getPaymentMethod())) {
            paymentUrl = alipayService.createPayment(payment);
        } else {
            throw new BusinessException("不支持的支付方式");
        }

        // 4. 缓存支付信息到Redis
        redisTemplate.opsForValue().set("payment:" + paymentNo, payment, 30, TimeUnit.MINUTES);

        // 5. 构建返回结果
        PaymentCreateResult result = new PaymentCreateResult();
        result.setPaymentId(paymentId);
        result.setPaymentNo(paymentNo);
        result.setPaymentUrl(paymentUrl);
        return result;
    }

    /**
     * 获取支付详情
     */
    public PaymentVO getPaymentDetail(Long paymentId) {
        // 1. 先从Redis缓存获取
        Payment payment = (Payment) redisTemplate.opsForValue().get("payment:id:" + paymentId);
        if (payment == null) {
            // 2. 从数据库获取
            payment = paymentMapper.selectById(paymentId);
            if (payment == null) {
                throw new BusinessException("支付记录不存在");
            }
            // 3. 缓存到Redis
            redisTemplate.opsForValue().set("payment:id:" + paymentId, payment, 1, TimeUnit.HOURS);
        }
        return PaymentVO.fromEntity(payment);
    }

    /**
     * 处理支付回调
     */
    @GlobalTransactional(name = "handle-payment-notify", rollbackFor = Exception.class)
    public boolean handlePaymentNotify(String paymentMethod, Map<String, String> params) {
        // 1. 验证回调签名
        boolean verified;
        String paymentNo;
        String transactionId;
        
        if ("wechat".equals(paymentMethod)) {
            verified = wechatPayService.verifyNotify(params);
            paymentNo = params.get("out_trade_no");
            transactionId = params.get("transaction_id");
        } else if ("alipay".equals(paymentMethod)) {
            verified = alipayService.verifyNotify(params);
            paymentNo = params.get("out_trade_no");
            transactionId = params.get("trade_no");
        } else {
            throw new BusinessException("不支持的支付方式");
        }

        if (!verified) {
            return false;
        }

        // 2. 更新支付状态
        Payment payment = paymentMapper.selectByPaymentNo(paymentNo);
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }

        payment.setPaymentStatus(1); // 已支付
        payment.setTransactionId(transactionId);
        payment.setPayTime(new Date());
        payment.setNotifyTime(new Date());
        paymentMapper.updateById(payment);

        // 3. 更新Redis缓存
        redisTemplate.opsForValue().set("payment:" + paymentNo, payment, 1, TimeUnit.HOURS);
        redisTemplate.opsForValue().set("payment:id:" + payment.getId(), payment, 1, TimeUnit.HOURS);

        // 4. 调用订单服务更新订单状态
        // 这里应该通过Feign调用订单服务

        return true;
    }
}
```

### 8.2 退款服务

```java
@Service
public class RefundService {

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 创建退款
     */
    @GlobalTransactional(name = "create-refund", rollbackFor = Exception.class)
    public RefundCreateResult createRefund(RefundCreateDTO createDTO) {
        // 1. 生成退款ID和退款单号
        Long refundId = idGenerator.nextId();
        String refundNo = "R" + OrderNoGenerator.generate();

        // 2. 查询支付记录
        Payment payment = paymentMapper.selectById(createDTO.getPaymentId());
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }

        // 3. 检查退款金额
        if (createDTO.getRefundAmount().compareTo(payment.getAmount()) > 0) {
            throw new BusinessException("退款金额不能大于支付金额");
        }

        // 4. 创建退款记录
        Refund refund = new Refund();
        refund.setId(refundId);
        refund.setRefundNo(refundNo);
        refund.setPaymentId(createDTO.getPaymentId());
        refund.setOrderId(payment.getOrderId());
        refund.setUserId(payment.getUserId());
        refund.setRefundAmount(createDTO.getRefundAmount());
        refund.setRefundReason(createDTO.getRefundReason());
        refund.setRefundStatus(0); // 待处理
        refundMapper.insert(refund);

        // 5. 调用第三方支付平台退款
        boolean success;
        String transactionId;
        
        if ("wechat".equals(payment.getPaymentMethod())) {
            success = wechatPayService.refund(payment, refund);
            transactionId = "微信退款交易ID"; // 实际应该从微信退款响应中获取
        } else if ("alipay".equals(payment.getPaymentMethod())) {
            success = alipayService.refund(payment, refund);
            transactionId = "支付宝退款交易ID"; // 实际应该从支付宝退款响应中获取
        } else {
            throw new BusinessException("不支持的支付方式");
        }

        // 6. 更新退款状态
        if (success) {
            refund.setRefundStatus(1); // 退款成功
            refund.setRefundTime(new Date());
            refund.setTransactionId(transactionId);
        } else {
            refund.setRefundStatus(2); // 退款失败
        }
        refundMapper.updateById(refund);

        // 7. 更新支付状态
        if (success) {
            payment.setPaymentStatus(3); // 已退款
            paymentMapper.updateById(payment);
        }

        // 8. 构建返回结果
        RefundCreateResult result = new RefundCreateResult();
        result.setRefundId(refundId);
        result.setRefundNo(refundNo);
        result.setSuccess(success);
        return result;
    }

    /**
     * 获取退款详情
     */
    public RefundVO getRefundDetail(Long refundId) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException("退款记录不存在");
        }
        return RefundVO.fromEntity(refund);
    }
}
```

### 8.3 微信支付服务

```java
@Service
public class WechatPayService {

    @Autowired
    private WXPay wxPay;

    @Value("${wechat.pay.notify-url}")
    private String notifyUrl;

    /**
     * 创建微信支付
     */
    public String createPayment(Payment payment) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("body", "商品支付");
            data.put("out_trade_no", payment.getPaymentNo());
            data.put("total_fee", String.valueOf(payment.getAmount().multiply(BigDecimal.valueOf(100)).intValue()));
            data.put("spbill_create_ip", "127.0.0.1");
            data.put("notify_url", notifyUrl);
            data.put("trade_type", "NATIVE");

            Map<String, String> result = wxPay.unifiedOrder(data);
            String returnCode = result.get("return_code");
            String resultCode = result.get("result_code");

            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
                return result.get("code_url");
            } else {
                throw new BusinessException("微信支付创建失败: " + result.get("return_msg"));
            }
        } catch (Exception e) {
            throw new BusinessException("微信支付创建失败", e);
        }
    }

    /**
     * 验证微信支付回调
     */
    public boolean verifyNotify(Map<String, String> params) {
        try {
            return WXPayUtil.isSignatureValid(params, "商户密钥");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 微信退款
     */
    public boolean refund(Payment payment, Refund refund) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("out_trade_no", payment.getPaymentNo());
            data.put("out_refund_no", refund.getRefundNo());
            data.put("total_fee", String.valueOf(payment.getAmount().multiply(BigDecimal.valueOf(100)).intValue()));
            data.put("refund_fee", String.valueOf(refund.getRefundAmount().multiply(BigDecimal.valueOf(100)).intValue()));

            Map<String, String> result = wxPay.refund(data);
            String returnCode = result.get("return_code");
            String resultCode = result.get("result_code");

            return "SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode);
        } catch (Exception e) {
            throw new BusinessException("微信退款失败", e);
        }
    }
}
```

### 8.4 支付宝服务

```java
@Service
public class AlipayService {

    @Autowired
    private AlipayClient alipayClient;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    @Value("${alipay.return-url}")
    private String returnUrl;

    /**
     * 创建支付宝支付
     */
    public String createPayment(Payment payment) {
        try {
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(notifyUrl);
            request.setReturnUrl(returnUrl);

            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", payment.getPaymentNo());
            bizContent.put("total_amount", payment.getAmount());
            bizContent.put("subject", "商品支付");
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

            request.setBizContent(bizContent.toString());
            return alipayClient.pageExecute(request).getBody();
        } catch (Exception e) {
            throw new BusinessException("支付宝支付创建失败", e);
        }
    }

    /**
     * 验证支付宝回调
     */
    public boolean verifyNotify(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(params, "支付宝公钥", "UTF-8", "RSA2");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 支付宝退款
     */
    public boolean refund(Payment payment, Refund refund) {
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", payment.getPaymentNo());
            bizContent.put("refund_amount", refund.getRefundAmount());
            bizContent.put("out_request_no", refund.getRefundNo());
            bizContent.put("refund_reason", refund.getRefundReason());

            request.setBizContent(bizContent.toString());
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            return response.isSuccess();
        } catch (Exception e) {
            throw new BusinessException("支付宝退款失败", e);
        }
    }
}
```

## 9. 集成方案

### 9.1 服务注册与发现

- 使用Nacos作为服务注册中心，支付服务启动时自动注册到Nacos
- 其他服务通过服务名调用支付服务

### 9.2 网关集成

- API网关通过Nacos发现支付服务
- 网关统一处理认证、限流、日志等横切关注点
- 路由规则配置：将`/payment/**`路径路由到支付服务

### 9.3 订单服务集成

- 订单服务调用支付服务创建支付
- 支付服务回调通知订单服务更新订单状态
- 使用Seata确保支付操作与订单状态更新的一致性

### 9.4 监控集成

- 使用SkyWalking进行链路追踪
- 使用Sentinel Dashboard监控服务状态
- 使用ELK Stack进行日志收集和分析

## 10. 部署方案

### 10.1 容器化部署

- 使用Docker容器化支付服务
- 配置Docker Compose实现多服务编排

### 10.2 集群部署

- 部署多个支付服务实例
- 通过Nacos实现服务发现和负载均衡
- 第三方支付回调通过负载均衡分发到不同实例

## 11. 注意事项

1. **安全问题**：支付服务涉及资金交易，必须确保数据传输和存储的安全性
2. **签名验证**：严格验证第三方支付平台的回调签名，防止伪造回调
3. **幂等性**：确保支付和退款操作的幂等性，防止重复操作
4. **超时处理**：妥善处理支付超时、退款超时等情况
5. **异常处理**：妥善处理各种异常情况，确保服务稳定性
6. **日志记录**：详细记录支付和退款操作的日志，便于问题排查
7. **监控告警**：对支付异常、退款异常等情况进行监控和告警
8. **性能优化**：考虑高并发场景下的性能优化，如使用Redis缓存

## 12. 扩展计划

1. **更多支付方式**：集成更多支付方式，如银联支付、苹果支付等
2. **支付分账**：实现支付分账功能，支持多商家分账
3. **支付对账**：实现自动对账功能，确保资金安全
4. **支付报表**：实现支付数据统计和报表功能
5. **国际化支持**：支持国际支付方式，如PayPal、Stripe等
6. **支付风控**：集成支付风控系统，防止欺诈交易
7. **数字货币支付**：支持数字货币支付（如比特币、以太坊等）
8. **生物识别支付**：支持指纹支付、面容支付等生物识别支付方式

---

**文档版本**：v1.0
**编写日期**：2026-02-24
**编写人**：System Designer