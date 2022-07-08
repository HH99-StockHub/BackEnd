package com.sparta.stockhub.controller;

import com.sparta.stockhub.domain.Article;
import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
import com.sparta.stockhub.dto.responseDto.ArticleResponseDto;
import com.sparta.stockhub.security.UserDetailsImpl;
import com.sparta.stockhub.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    // 게시글 작성
    @PostMapping("/article")
    public boolean createArticle(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ArticleRequestDto requestDto) {
        if(articleService.cleanArticle(requestDto)==false){
            return false;
        }
        if (userDetails != null) {
            articleService.createArticle(userDetails, requestDto);
        }return true;
    }

    // 게시글 내용 조회
    @GetMapping("/articles/{articleId}")
    public ArticleResponseDto readArticle(@PathVariable Long articleId) {
        return articleService.readArticle(articleId);
    }

    // 게시글 찬성 투표
    @PostMapping("/articles/{articleId}/up")
    public void voteUp(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId) {
        if (userDetails != null) {
            articleService.voteUp(userDetails, articleId);
        }
    }

    // 게시글 반대 투표
    @PostMapping("/articles/{articleId}/down")
    public void voteDown(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId) {
        if (userDetails != null) {
            articleService.voteDown(userDetails, articleId);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/articles/{articleId}")
    public void deleteArticle(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId) {
        articleService.deleteArticle(userDetails, articleId);
    }

    //게시글 검색
    @GetMapping("/articles/{keywords}/{searchtype}/search")
    public List<Article> searchArticle(@PathVariable String keywords, @PathVariable Long searchtype) {
        return articleService.searchArticle(keywords, searchtype);


    }
}
