package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Article;
import com.sparta.stockhub.domain.Comment;
import com.sparta.stockhub.domain.User;
import com.sparta.stockhub.dto.requestDto.CommentRequestDto;
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
    private final UserService userService;

    // 게시글: 댓글 목록 조회
    public List<CommentResponseDto> readComments(Long articleId) {
        List<CommentResponseDto> responseDtoList = new ArrayList<>();
        List<Comment> commentList = commentRepository.findAllByArticleIdOrderByCreatedAtDesc(articleId);
        for (int i = 0; i < commentList.size(); i++) {
            User user = userRepository.findById(commentList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            CommentResponseDto responseDto = new CommentResponseDto(commentList.get(i), user);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 게시글: 댓글 작성
    @Transactional
    public void createComment(UserDetailsImpl userDetails, Long articleId, CommentRequestDto requestDto) {
        String comments = requestDto.getComments();
        Long loginId = userDetails.getUser().getUserId();
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
        );
        if (comments.equals("")) throw new CustomException(ErrorCode.BAD_REQUEST_NOTWRITE);
        if (comments.length() > 300) throw new CustomException(ErrorCode.BAD_REQUEST_COMMENTLENGTH);
        Comment newComment = new Comment(loginId, articleId, comments);
        commentRepository.save(newComment);

        User user = userDetails.getUser(); // 경험치 5점 획득
        user.setExperience(user.getExperience() + 5);
        userRepository.save(user);
        userService.updateRank(user);
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

        User user = userDetails.getUser(); // 경험치 5점 감소
        user.setExperience(user.getExperience() - 5);
        userRepository.save(user);
        userService.updateRank(user);
    }

    // 댓글 욕설 필터링
    public boolean cleanCommnet(CommentRequestDto requestDto) {
        // 필터링 대상 욕설
        String[] words = {
                "개걸레", "개보지", "개씨발", "개좆", "개지랄", "걸레년",
                "느검마", "느금", "니기미", "니애미", "니애비", "닝기미",
                "미친년", "미친놈", "미친새끼", "백보지", "보지털", "보짓물", "빠구리",
                "썅년", "썅놈", "씨발", "씹년", "씹보지", "씹새끼", "씹자지", "씹창",
                "잠지털", "잡년", "잡놈", "젓같은", "젖같은", "좆", "창녀", "창년"
        };

        for (String word : words)
            if (requestDto.getComments().contains(word)) return false;

        return true;
    }
}
