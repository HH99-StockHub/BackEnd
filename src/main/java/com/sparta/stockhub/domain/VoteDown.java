package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VoteDown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteDownId;

    @Column(nullable = false)
    private Long articleId;

    @Column(nullable = false)
    private Long userId;

    public VoteDown(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
    }
}