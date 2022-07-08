package com.sparta.stockhub.controller;

import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
import com.sparta.stockhub.dto.responseDto.ArticleListResponseDto;
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
    public void createArticle(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ArticleRequestDto requestDto) {
        if (userDetails != null) {
            articleService.createArticle(userDetails, requestDto);
        }
    }

    // 메인: 전체 게시글 목록 조회
    @GetMapping("/main/articles")
    public List<ArticleListResponseDto> readMainArticles() {
        return articleService.readMainArticles();
    }

    // 메인: 명예의 전당 인기글 목록 조회
    @GetMapping("/main/fame/popular/articles")
    public List<ArticleListResponseDto> readMainFamePopularArticles() { return articleService.readMainFamePopularArticles(); }

    // 메인: 명예의 전당 수익왕 목록 조회
    @GetMapping("/main/fame/rich/articles")
    public List<ArticleListResponseDto> readMainFameRichArticles() { return articleService.readMainFameRichArticles(); }

    // 메인: 인기글 목록 조회
    @GetMapping("/main/popular/articles")
    public List<ArticleListResponseDto> readMainPopularArticles() { return articleService.readMainPopularArticles(); }

    // 메인: 수익왕 목록 조회
    @GetMapping("/main/rich/articles")
    public List<ArticleListResponseDto> readMainRichArticles() { return articleService.readMainRichArticles(); }

    // 전체 게시판: 게시글 목록 조회
    @GetMapping("/all/articles")
    public List<ArticleListResponseDto> readAllArticles() { return articleService.readAllArticles(); }

    // 인기글 게시판: 게시글 목록 조회
    @GetMapping("/popular/articles")
    public List<ArticleListResponseDto> readPopularArticles() { return articleService.readPopularArticles(); }

    // 수익왕 게시판: 게시글 목록 조회
    @GetMapping("/rich/articles")
    public List<ArticleListResponseDto> readRichArticles() { return articleService.readRichArticles(); }

    // 모아보기 게시판: 게시글 목록 조회
    @GetMapping("/user/{userId}/articles")
    public List<ArticleListResponseDto> readUserArticles(@PathVariable Long userId) { return articleService.readUserArticles(userId);}

    // 모아보기 게시판: 내 게시글 목록 조회 //////////////////// 타인 게시글 목록 조회와 같은 함수 사용
    @GetMapping("/user/my/articles")
    public List<ArticleListResponseDto> readMyArticles(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) return articleService.readUserArticles(userDetails.getUser().getUserId());
        else throw new IllegalArgumentException("로그인이 필요합니다.");
    }

    // 게시글: 게시글 내용 조회
    @GetMapping("/articles/{articleId}")
    public ArticleResponseDto readArticle(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId) {
        return articleService.readArticle(userDetails, articleId);
    }

    // 게시글: 찬성 투표
    @PostMapping("/articles/{articleId}/up")
    public void voteUp(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId) {
        if (userDetails != null) {
            articleService.voteUp(userDetails, articleId);
        }
    }

    // 게시글: 반대 투표
    @PostMapping("/articles/{articleId}/down")
    public void voteDown(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId) {
        if (userDetails != null) {
            articleService.voteDown(userDetails, articleId);
        }
    }

    // 게시글: 게시글 삭제
    @DeleteMapping("/articles/{articleId}")
    public void deleteArticle(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId) {
        articleService.deleteArticle(userDetails, articleId);
    }
}
