package com.sparta.stockhub.dto.responseDto;

import com.sparta.stockhub.domain.Article;
import com.sparta.stockhub.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleResponseDto {

    private Long articleId;
    private LocalDateTime createdAt;
    private Long userId;
    private String nickname;
    private String profileImage;
    private String rank;
    private String articleTitle;
    private String stockName;
    private int stockPriceFirst;
    private int stockPriceLast;
    private double stockReturn;
    private String point1;
    private String content1;
    private String point2;
    private String content2;
    private String point3;
    private String content3;
    private LocalDateTime deadline;
    private int targetReturn;
    private int voteUpCount;
    private int voteDownCount;
    private int commentCount;
    private int viewCount;

    public ArticleResponseDto(Article article, User user, int commentCount) {
        this.articleId = article.getArticleId();
        this.createdAt = article.getCreatedAt();
        this.userId = article.getUserId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.rank = user.getRank();
        this.articleTitle = article.getArticleTitle();
        this.stockName = article.getStockName();
        this.stockPriceFirst = article.getStockPriceFirst();
        this.stockPriceLast = article.getStockPriceLast();
        this.stockReturn = article.getStockReturn();
        this.point1 = article.getPoint1();
        this.content1 = article.getContent1();
        this.point2 = article.getPoint2();
        this.content2 = article.getContent2();
        this.point3 = article.getPoint3();
        this.content3 = article.getContent3();
        this.deadline = article.getDeadline();
        this.targetReturn = article.getTargetReturn();
        this.voteUpCount = article.getVoteUpCount();
        this.voteDownCount = article.getVoteDownCount();
        this.commentCount = commentCount;
        this.viewCount = article.getViewCount();
    }
}
