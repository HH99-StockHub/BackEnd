package com.sparta.stockhub.domain;

import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
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
    LocalDateTime deadline;

    @Column
    int targetReturn;

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

    public Article(Long userId, ArticleRequestDto requestDto, LocalDateTime deadline, int stockPriceFirst, int stockPriceLast, double stockReturn) {
        this.userId = userId;
        this.articleTitle = requestDto.getArticleTitle();
        this.stockName = requestDto.getStockName();
        this.stockPriceFirst = stockPriceFirst;
        this.stockPriceLast = stockPriceLast;
        this.stockReturn = stockReturn;
        this.point1 = requestDto.getPoint1();
        this.content1 = requestDto.getContent1();
        this.point2 = requestDto.getPoint2();
        this.content2 = requestDto.getContent2();
        this.point3 = requestDto.getPoint3();
        this.content3 = requestDto.getContent3();
        this.deadline = deadline;
        this.targetReturn = requestDto.getTargetReturn();
        this.voteUpCount = 0;
        this.voteDownCount = 0;
        this.viewCount = 0;
        this.popularList = false;
        this.richList = false;
        this.popularRegTime = null;
        this.richRegTime = null;
    }




}
