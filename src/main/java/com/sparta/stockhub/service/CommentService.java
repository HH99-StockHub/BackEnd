package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Article;
import com.sparta.stockhub.domain.Comment;
import com.sparta.stockhub.domain.User;
import com.sparta.stockhub.dto.responseDto.CommentResponseDto;
import com.sparta.stockhub.exceptionHandler.CustomException;
import com.sparta.stockhub.exceptionHandler.ErrorCode;
import com.sparta.stockhub.repository.ArticleRepository;
import com.sparta.stockhub.repository.CommentRepository;
import com.sparta.stockhub.repository.UserRepository;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // 게시글: 댓글 목록 조회
    public List<CommentResponseDto> readComments(Long articleId) {
        List<CommentResponseDto> responseDtoList = new ArrayList<>();
        List<Comment> commentList = commentRepository.findAllByArticleIdOrderByCreatedAtDesc(articleId);
        for (int i = 0; i < commentList.size(); i++) {
            User user = userRepository.findById(commentList.get(i).getUserId()).orElseThrow(
<<<<<<< HEAD
                    ()-> new CustomException(ErrorCode.NOT_FOUND_USER)
=======
                    ()-> new NullPointerException("유저가 존재하지 않습니다")
>>>>>>> origin/feat/mongoConnect
            );
            CommentResponseDto responseDto = new CommentResponseDto(commentList.get(i), user);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 게시글: 댓글 작성
    public void createComment(UserDetailsImpl userDetails, Long articleId, String comment) {
        Long loginId = userDetails.getUser().getUserId();
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        if (comment.equals("")) throw new CustomException(ErrorCode.BAD_REQUEST_NOTWRITE);
        if (comment.length() > 300) throw new CustomException(ErrorCode.BAD_REQUEST_COMMENTLENGTH);
        Comment newComment = new Comment(loginId, articleId, comment);
        commentRepository.save(newComment);
    }

    // 게시글: 댓글 삭제
    @Transactional
    public void deleteComment(UserDetailsImpl userDetails, Long commentId) {
        Long loginId = userDetails.getUser().getUserId();
        Comment oldComment = commentRepository.findByCommentId(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_COMMENT)
        );
        if (loginId != oldComment.getUserId()) throw new CustomException(ErrorCode.UNAUTHORIZED_NOTUSER);

        commentRepository.deleteById(commentId);
    }
}
