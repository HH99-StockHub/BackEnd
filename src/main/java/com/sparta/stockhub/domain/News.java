package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class News extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long newsId;  //newsId

    @Column
    private String title;   // news 제목

    @Column
    private String link;    // news 링크

    @Column
    private String description; // news 내용

    @Column
    private String pubDate; // news 발행 날짜
}
