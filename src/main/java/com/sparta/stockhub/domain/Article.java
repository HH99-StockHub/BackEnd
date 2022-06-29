package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false) // 글 작성 당시의 주식 가격
    int stockPriceFirst;

    @Column(nullable = false) // 조회 시점의 주식 가격
    int stockPriceLast;

    @Column(nullable = false) // 글 작성 이후의 주식 수익률
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
    @OneToMany(cascade = CascadeType.ALL)
    private List<VoteUp> voteUpList;

    @Column
    @OneToMany(cascade = CascadeType.ALL)
    private List<VoteDown> voteDownList;

    @Column
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> commentList;

    @Column // 조회수
    int viewCount;

    @Column // 인기글 게시판 등록 여부
    boolean popularList;

    @Column // 수익왕 게시판 등록 여부
    boolean richList;

    public Article(Long userId, String articleTitle, String stockName, int stockPriceFirst, int stockPriceLast,
                   String point1, String content1, String point2, String content2, String point3, String content3) {
        this.userId = userId;
        this.articleTitle = articleTitle;
        this.stockName = stockName;
        this.stockPriceFirst = stockPriceFirst;
        this.stockPriceLast = stockPriceLast;
        this.stockReturn = 0.0;
        this.point1 = point1;
        this.content1 = content1;
        this.point2 = point2;
        this.content2 = content2;
        this.point3 = point3;
        this.content3 = content3;
        this.voteUpList = new ArrayList<>();
        this.voteDownList = new ArrayList<>();
        this.commentList = new ArrayList<>();
        this.viewCount = 0;
        this.popularList = false;
        this.richList = false;
    }
}
