
package com.sparta.stockhub.controller;

import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
import com.sparta.stockhub.dto.responseDto.ArticleListResponseDto;
import com.sparta.stockhub.dto.responseDto.ArticleResponseDto;
import com.sparta.stockhub.security.UserDetailsImpl;
import com.sparta.stockhub.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        if(articleService.cleanArticle(requestDto) == false) return false; // 욕설 필터링 통과 못할 시 false return
        if (userDetails != null) {
            articleService.createArticle(userDetails, requestDto);
        }
        return true;
    }

    // 게시글 검색
    @GetMapping("/articles/{keywords}/search")
    public Page<ArticleListResponseDto> searchArticle(
            @PathVariable String keywords,
            @RequestParam("page") int page,
            @RequestParam("size") int size

    ) {
        page = page - 1;
        return articleService.searchArticle(keywords, page, size);
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
    public Page<ArticleListResponseDto> readAllArticles(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) { page = page - 1;
        return articleService.readAllArticles(page, size); }

    // 인기글 게시판: 게시글 목록 조회
    @GetMapping("/popular/articles")
    public Page<ArticleListResponseDto> readPopularArticles(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) { page = page - 1;
        return articleService.readPopularArticles(page, size); }

    // 수익왕 게시판: 게시글 목록 조회
    @GetMapping("/rich/articles")
    public Page<ArticleListResponseDto> readRichArticles(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) { page = page - 1;
        return articleService.readRichArticles(page, size); }

    // 모아보기 게시판: 게시글 목록 조회
    @GetMapping("/user/{userId}/articles")
    public Page<ArticleListResponseDto> readUserArticles(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @PathVariable Long userId) {
        page = page - 1;
        return articleService.readUserArticles(userId, page, size);}

    // 모아보기 게시판: 내 게시글 목록 조회
    @GetMapping("/user/my/articles")
    public Page<ArticleListResponseDto> readMyArticles(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null){ page = page - 1; return articleService.readUserArticles(userDetails.getUser().getUserId(), page, size);}
        else throw new IllegalArgumentException("로그인이 필요합니다.");
    }

    // 게시글: 게시글 내용 조회
    @GetMapping("/articles/{articleId}")
    public ArticleResponseDto readArticle(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId) {
        if (userDetails != null) return articleService.readArticleLoggedIn(userDetails.getUser(), articleId);
        else return articleService.readArticle(articleId);
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

