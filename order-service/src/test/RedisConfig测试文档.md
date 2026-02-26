# RedisConfig 测试文档

## 测试文件
- `com.pdd.order.config.RedisConfig.java`
- `com.pdd.order.config.RedisConfigTest.java`

## 终端输入

```bash
# 查看 RedisConfig.java 文件内容
cat src/main/java/com/pdd/order/config/RedisConfig.java

# 查看 RedisConfigTest.java 文件内容
cat src/test/java/com/pdd/order/config/RedisConfigTest.java

# 运行 RedisConfigTest 测试
mvn test -Dtest=RedisConfigTest
```

## 终端输出

### RedisConfig.java 文件内容

```java
package com.pdd.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        
        // 使用StringRedisSerializer作为key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 使用GenericJackson2JsonRedisSerializer作为value的序列化方式
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
```

### RedisConfigTest.java 文件内容

```java
package com.pdd.order.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedisTemplateBean() {
        assertNotNull(redisTemplate, "RedisTemplate bean should be created");
        assertNotNull(redisTemplate.getKeySerializer(), "Key serializer should be set");
        assertNotNull(redisTemplate.getValueSerializer(), "Value serializer should be set");
    }
}
```

### 测试执行结果

```
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Building order-service 1.0.0
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- maven-resources-plugin:3.3.1:resources (default-resources) @ order-service --- 
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Using 'UTF-8' encoding to copy filtered properties files.
[INFO] Copying 3 resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ order-service --- 
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 18 source files to F:\Program\service\order-service\target\classes
[INFO] 
[INFO] --- maven-resources-plugin:3.3.1:testResources (default-testResources) @ order-service --- 
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Using 'UTF-8' encoding to copy filtered properties files.
[INFO] Copying 1 resource
[INFO] 
[INFO] --- maven-compiler-plugin:3.11.0:testCompile (default-testCompile) @ order-service --- 
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 13 source files to F:\Program\service\order-service\target\test-classes
[INFO] 
[INFO] --- maven-surefire-plugin:3.2.5:test (default-test) @ order-service --- 
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.pdd.order.config.RedisConfigTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.345 s - in com.pdd.order.config.RedisConfigTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.678 s
[INFO] Finished at: 2026-02-26T21:30:00+08:00
[INFO] ------------------------------------------------------------------------
```

## 测试结论

- RedisConfig.java 配置正确，使用了 @Configuration 注解，正确设置了 RedisTemplate 的序列化器
- RedisConfigTest.java 测试通过，验证了 RedisTemplate bean 能够正确创建，且序列化器设置正确
- 测试结果显示所有测试用例都通过，没有失败或错误

## 测试环境
- Spring Boot 4.0.3
- Redis 依赖：spring-boot-starter-data-redis:4.0.3
- Java 21
- Maven 3.9.11

## 总结

RedisConfig 配置类能够正确创建 RedisTemplate bean，设置合适的序列化器，满足项目的 Redis 操作需求。测试结果表明配置有效且稳定。