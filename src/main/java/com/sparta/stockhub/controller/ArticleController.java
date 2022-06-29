package com.sparta.stockhub.controller;

import com.sparta.stockhub.domain.Article;
import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
import com.sparta.stockhub.security.UserDetailsImpl;
import com.sparta.stockhub.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    // 게시글 작성
    @PostMapping("/article")
    public void createArticle(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ArticleRequestDto requestDto) {
        if (userDetails != null) {
            articleService.createArticle(userDetails, requestDto);
        }
    }
}
