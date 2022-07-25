package com.sparta.stockhub.chat;

import com.sparta.stockhub.domain.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notice extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(nullable = false)
    private Long noticeUserId;

    @Column(nullable = false)
    private Long noticeArticleId;

    @Column(nullable = false)
    private String noticeMessage;

    @Column(nullable = false)
    private boolean noticeCheck;


    public Notice(Long noticeUserId, Long noticeArticleId, String noticeMessage, boolean noticeCheck) {
        this.noticeUserId = noticeUserId;
        this.noticeArticleId = noticeArticleId;
        this.noticeMessage = noticeMessage;
        this.noticeCheck = noticeCheck;
    }
}
