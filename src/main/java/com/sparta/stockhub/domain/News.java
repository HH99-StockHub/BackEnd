package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class News extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long newsId;

    @Column
    private String title;

    @Column
    private String link;

    @Column
    private String description;

    @Column
    private String pubDate;
}
