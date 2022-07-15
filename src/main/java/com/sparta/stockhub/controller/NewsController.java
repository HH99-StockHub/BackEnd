package com.sparta.stockhub.controller;

import com.sparta.stockhub.dto.responseDto.NewsListResponseDto;
import com.sparta.stockhub.utils.NewsSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsSearch newsSearch;

    // 게시글: 종목 뉴스 검색
    @GetMapping("/article/news")
    public List<NewsListResponseDto> getNews(@RequestParam String stockName) {
        return newsSearch.search(stockName);
    }
}
