package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Article;
import com.sparta.stockhub.domain.Comment;
import com.sparta.stockhub.domain.User;
import com.sparta.stockhub.dto.responseDto.CommentResponseDto;
import com.sparta.stockhub.repository.ArticleRepository;
import com.sparta.stockhub.repository.CommentRepository;
import com.sparta.stockhub.repository.UserRepository;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // 댓글 목록 조회
    public List<CommentResponseDto> getComments(Long articleId) {
        List<CommentResponseDto> responseDtoList = new ArrayList<>();
        List<Comment> commentList = commentRepository.findByCommentIdOrderByCreatedAtDesc(articleId);
        for (int i = 0; i < commentList.size(); i++) {
            User user = userRepository.findById(commentList.get(i).getUserId()).orElseThrow(
                    ()-> new NullPointerException("사용자가 존재하지 않습니다")
            );
            CommentResponseDto responseDto = new CommentResponseDto(commentList.get(i), user);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 댓글 작성
    public void createComment(UserDetailsImpl userDetails, Long articleId, String comment) {
        Long loginId = userDetails.getUser().getUserId();
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );
        if (comment.equals("")) throw new IllegalArgumentException("댓글 내용 작성이 필요합니다.");
        if (comment.length() > 300) throw new IllegalArgumentException("댓글 내용은 300자 이내로 작성해주세요.");
        Comment newComment = new Comment(loginId, articleId, comment);
        commentRepository.save(newComment);
    }

    // 댓글 삭제
    public void deleteComment(UserDetailsImpl userDetails, Long commentId) {
        Long loginId = userDetails.getUser().getUserId();
        Comment oldComment = commentRepository.findByCommentId(commentId).orElseThrow(
                () -> new NullPointerException("댓글이 존재하지 않습니다.")
        );
        if (loginId != oldComment.getUserId()) throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");

        commentRepository.deleteByCommentId(commentId);
    }
}
