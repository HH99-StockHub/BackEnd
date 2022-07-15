package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VoteUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteUpId;

    @Column(nullable = false)
    private Long articleId;

    @Column(nullable = false)
    private Long userId;

    public VoteUp(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
    }
}