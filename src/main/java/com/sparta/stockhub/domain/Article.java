package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Article extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    String articleTitle;

    @Column(nullable = false)
    String stockName;

    @Column
    int stockPriceFirst; // 글 작성 시점의 주식 가격

    @Column
    int stockPriceLast; // 조회 시점의 주식 가격

    @Column
    double stockReturn;

    @Column(nullable = false)
    String point1;

    @Column(nullable = false)
    String content1;

    @Column
    String point2;

    @Column
    String content2;

    @Column
    String point3;

    @Column
    String content3;

    @Column
    int voteUpCount;

    @Column
    int voteDownCount;

    @Column
    int viewCount;

    @Column
    boolean popularList; // 인기글 게시판 등록 여부

    @Column
    LocalDateTime popularRegTime; // 인기글 게시판 등록 시간

    @Column
    boolean richList; // 수익왕 게시판 등록 여부

    @Column
    LocalDateTime richRegTime; // 수익왕 게시판 등록 시간

    public Article(Long userId, String articleTitle, String stockName, int stockPriceFirst, int stockPriceLast, double stockReturn,
                   String point1, String content1, String point2, String content2, String point3, String content3) {
        this.userId = userId;
        this.articleTitle = articleTitle;
        this.stockName = stockName;
        this.stockPriceFirst = stockPriceFirst;
        this.stockPriceLast = stockPriceLast;
        this.stockReturn = stockReturn;
        this.point1 = point1;
        this.content1 = content1;
        this.point2 = point2;
        this.content2 = content2;
        this.point3 = point3;
        this.content3 = content3;
        this.voteUpCount = 0;
        this.voteDownCount = 0;
        this.viewCount = 0;
        this.popularList = false;
        this.richList = false;
        this.popularRegTime = null;
        this.richRegTime = null;
    }




}
