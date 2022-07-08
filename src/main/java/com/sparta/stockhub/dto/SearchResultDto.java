package com.sparta.stockhub.dto;

import com.sparta.stockhub.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResultDto {
    private Long articleId;
    private LocalDateTime createdAt;
    private Long userId;
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
    private int voteUpCount;
    private int voteDownCount;
    private int viewCount;

//    List<Article> articleList;

    public SearchResultDto(Article article, int voteUpCount, int voteDownCount) {
        this.articleId = article.getArticleId();
        this.createdAt = article.getCreatedAt();
        this.userId = article.getUserId();
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
        this.voteUpCount = voteUpCount;
        this.voteDownCount = voteDownCount;
        this.viewCount = article.getViewCount();
    }
}
