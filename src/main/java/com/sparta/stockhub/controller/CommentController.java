package com.sparta.stockhub.controller;

import com.sparta.stockhub.chat.NotificationService;
import com.sparta.stockhub.domain.Article;
import com.sparta.stockhub.dto.requestDto.CommentRequestDto;
import com.sparta.stockhub.dto.responseDto.CommentResponseDto;
import com.sparta.stockhub.exceptionHandler.CustomException;
import com.sparta.stockhub.exceptionHandler.ErrorCode;
import com.sparta.stockhub.repository.ArticleRepository;
import com.sparta.stockhub.security.UserDetailsImpl;
import com.sparta.stockhub.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final NotificationService notificationService;

    private final ArticleRepository articleRepository;


    // 게시글: 댓글 목록 조회
    @GetMapping("/articles/{articleId}/comments")
    public List<CommentResponseDto> readComments(@PathVariable Long articleId) {
        return commentService.readComments(articleId);

    }

    // 게시글: 댓글 작성
    @PostMapping("/articles/{articleId}/comment")
    public boolean createComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId, @RequestBody CommentRequestDto requestDto) {
        if (commentService.cleanCommnet(requestDto) == false) return false; // 욕설 필터링 통과 못할 시 false return
        if (userDetails != null) commentService.createComment(userDetails, articleId, requestDto);

        String userNickname = userDetails.getUser().getNickname();
        Long commentUserId = userDetails.getUser().getUserId();
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );

        Long articleUserId = article.getUserId();
        notificationService.sendPrivateNotificationComment(userNickname, articleUserId, articleId, commentUserId);
        return true;
    }

    // 게시글: 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long commentId) {
        if (userDetails != null) commentService.deleteComment(userDetails, commentId);
    }
}
