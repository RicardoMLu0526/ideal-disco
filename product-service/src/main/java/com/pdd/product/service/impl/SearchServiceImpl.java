package com.pdd.product.service.impl;

import com.pdd.product.service.SearchService;
import com.pdd.product.vo.ProductSearchVO;
import com.pdd.product.vo.SearchResultVO;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public SearchResultVO search(String keyword, Integer page, Integer size) {
        SearchRequest searchRequest = new SearchRequest("product_index");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, "name", "description"));
        sourceBuilder.from((page - 1) * size);
        sourceBuilder.size(size);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse;
        try {
            searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("搜索失败", e);
        }

        SearchResultVO result = new SearchResultVO();
        result.setTotal(searchResponse.getHits().getTotalHits().value);

        List<ProductSearchVO> products = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            ProductSearchVO product = new ProductSearchVO();
            product.setId(Long.valueOf(hit.getId()));
            product.setName((String) hit.getSourceAsMap().get("name"));
            product.setPrice((Double) hit.getSourceAsMap().get("price"));
            product.setMainImage((String) hit.getSourceAsMap().get("mainImage"));
            product.setSales((Integer) hit.getSourceAsMap().get("sales"));
            products.add(product);
        }
        result.setProducts(products);

        recordSearchHistory(keyword);

        return result;
    }

    @Override
    public List<String> getHotSearch() {
        Set<String> keys = redisTemplate.keys("search:hot:*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        return keys.stream()
                .map(key -> key.substring(11))
                .sorted((a, b) -> {
                    Integer countA = (Integer) redisTemplate.opsForValue().get("search:hot:" + a);
                    Integer countB = (Integer) redisTemplate.opsForValue().get("search:hot:" + b);
                    return countB.compareTo(countA);
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getSearchSuggestions(String keyword) {
        // 简单实现，实际项目中可以使用ES的suggest功能
        List<String> suggestions = new ArrayList<>();
        suggestions.add(keyword + "手机");
        suggestions.add(keyword + "电脑");
        suggestions.add(keyword + "耳机");
        return suggestions;
    }

    private void recordSearchHistory(String keyword) {
        redisTemplate.opsForValue().increment("search:hot:" + keyword);
    }
}
