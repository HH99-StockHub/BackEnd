package com.sparta.stockhub.controller;

import com.sparta.stockhub.dto.responseDto.CommentResponseDto;
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

    // 댓글 목록 조회
    @GetMapping("/articles/{articleId}/comments")
    public List<CommentResponseDto> getComments(@PathVariable Long articleId) {
        return commentService.getComments(articleId);
    }

    // 댓글 작성
    @PostMapping("/articles/{articleId}/comment")
    public void createComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long articleId, @RequestBody String comment) {
        if (userDetails != null) commentService.createComment(userDetails, articleId, comment);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commendId}")
    public void deleteComment(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long commentId) {
        if (userDetails != null) commentService.deleteComment(userDetails, commentId);
    }
}
