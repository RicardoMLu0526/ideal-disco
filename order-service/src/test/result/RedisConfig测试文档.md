```bash
F:\Program\service>type order-service\src\main\java\com\pdd\order\config\RedisConfig.java
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

        // 浣跨敤StringRedisSerializer浣滀负key鐨勫簭鍒楀寲鏂瑰紡
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 浣跨敤GenericJackson2JsonRedisSerializer浣滀负value鐨勫簭鍒楀寲鏂瑰紡
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}

F:\Program\service>cd order-service

F:\Program\service\order-service>./gradlew test --tests "com.pdd.order.config.RedisConfigTest"

F:\Program\service\order-service>cd ..

F:\Program\service>
```