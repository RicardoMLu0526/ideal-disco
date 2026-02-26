package com.pdd.product.controller;

import com.pdd.product.service.SearchService;
import com.pdd.product.vo.Result;
import com.pdd.product.vo.SearchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public Result<SearchResultVO> search(@RequestParam String keyword, @RequestParam Integer page, @RequestParam Integer size) {
        SearchResultVO result = searchService.search(keyword, page, size);
        return Result.success(result);
    }

    @GetMapping("/suggest")
    public Result<List<String>> getSearchSuggestions(@RequestParam String keyword) {
        List<String> suggestions = searchService.getSearchSuggestions(keyword);
        return Result.success(suggestions);
    }

    @GetMapping("/hot")
    public Result<List<String>> getHotSearch() {
        List<String> hotSearch = searchService.getHotSearch();
        return Result.success(hotSearch);
    }
}
