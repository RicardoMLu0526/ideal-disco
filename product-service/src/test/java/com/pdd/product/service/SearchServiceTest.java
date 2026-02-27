package com.pdd.product.service;

import com.pdd.product.service.impl.SearchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private org.elasticsearch.client.RestHighLevelClient esClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    public void testGetHotSearch() {
        when(redisTemplate.keys(anyString())).thenReturn(java.util.Collections.emptySet());
        java.util.List<String> result = searchService.getHotSearch();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
