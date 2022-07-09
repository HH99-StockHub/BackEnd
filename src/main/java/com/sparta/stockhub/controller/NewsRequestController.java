package com.sparta.stockhub.controller;

import com.sparta.stockhub.domain.News;
import com.sparta.stockhub.dto.responseDto.NewsResponseDto;
import com.sparta.stockhub.utils.NewsSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class NewsRequestController {

    private final NewsSearch newsSearch;

    @GetMapping("/search")
    public List<NewsResponseDto> getNews(@RequestParam String query) {
        String resultString = newsSearch.search(query);
        return newsSearch.fromJSONtoItems(resultString);
    }
}
