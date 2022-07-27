package com.sparta.stockhub.dto.responseDto;

import com.sparta.stockhub.domain.Comment;
import com.sparta.stockhub.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;
    private LocalDateTime createdAt;
    private Long userId;
    private String nickname;
    private String profileImage;
    private String rank;
    private String comments;


    public CommentResponseDto(Comment comment, User user) {
        this.commentId = comment.getCommentId();
        this.createdAt = comment.getCreatedAt();
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.rank = user.getRank();
        this.comments = comment.getComments();
    }
}
