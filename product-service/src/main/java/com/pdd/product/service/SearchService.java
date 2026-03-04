package com.pdd.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public SearchResult search(String keyword, Integer page, Integer size) {
        // 模拟搜索结果
        SearchResult result = new SearchResult();
        result.setTotal(100L);
        
        List<ProductSearchVO> products = new ArrayList<>();
        // 暂时返回空列表
        result.setProducts(products);

        // 记录搜索历史
        recordSearchHistory(keyword);

        return result;
    }

    public List<String> getSearchSuggestions(String keyword) {
        // 模拟搜索建议
        List<String> suggestions = new ArrayList<>();
        suggestions.add(keyword + "手机");
        suggestions.add(keyword + "电脑");
        suggestions.add(keyword + "平板");
        return suggestions;
    }

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

    private void recordSearchHistory(String keyword) {
        // 简单实现：使用Redis记录热门搜索词
        redisTemplate.opsForValue().increment("search:hot:" + keyword);
    }

    // 搜索结果类
    public static class SearchResult {
        private Long total;
        private List<ProductSearchVO> products;

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public List<ProductSearchVO> getProducts() {
            return products;
        }

        public void setProducts(List<ProductSearchVO> products) {
            this.products = products;
        }
    }

    // 商品搜索VO类
    public static class ProductSearchVO {
        private Long id;
        private String name;
        private String mainImage;
        private Double price;
        private Integer sales;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMainImage() {
            return mainImage;
        }

        public void setMainImage(String mainImage) {
            this.mainImage = mainImage;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getSales() {
            return sales;
        }

        public void setSales(Integer sales) {
            this.sales = sales;
        }
    }
}
