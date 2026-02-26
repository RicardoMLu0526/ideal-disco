package com.pdd.product.service;

import com.pdd.product.vo.SearchResultVO;
import java.util.List;

public interface SearchService {
    SearchResultVO search(String keyword, Integer page, Integer size);
    List<String> getHotSearch();
    List<String> getSearchSuggestions(String keyword);
}
