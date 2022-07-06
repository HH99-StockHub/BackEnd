package com.sparta.stockhub.dto.responseDto;

import com.sparta.stockhub.domain.Article;
import com.sparta.stockhub.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleListResponseDto {

    private Long articleId;
    private LocalDateTime createdAt;
    private Long userId;
    private String nickname;
    private String profileImage;
    private String articleTitle;
    private String stockName;
    private double stockReturn;
    private int voteUpCount;
    private int voteDownCount;
    private int commentCount;
    private int viewCount;

    public ArticleListResponseDto(Article article, User user, int commentCount) {
        this.articleId = article.getArticleId();
        this.createdAt = article.getCreatedAt();
        this.userId = article.getUserId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.articleTitle = article.getArticleTitle();
        this.stockName = article.getStockName();
        this.stockReturn = article.getStockReturn();
        this.voteUpCount = article.getVoteUpCount();
        this.voteDownCount = article.getVoteDownCount();
        this.commentCount = commentCount;
        this.viewCount = article.getViewCount();
    }
}
