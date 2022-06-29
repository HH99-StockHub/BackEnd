package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Column // 조회수
    int viewCount;

    @Column // 인기글 게시판 등록 여부
    boolean popularList;

    @Column // 수익왕 게시판 등록 여부
    boolean richList;
}
