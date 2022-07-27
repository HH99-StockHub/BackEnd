package com.sparta.stockhub.chat;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ResponseMessage {
    private String noticeMessage;

    private Long NoticeArticleId;

    private Long noticeId;

    private LocalDateTime noticeCreatedAt;

    public ResponseMessage(String noticeMessage, Long NoticeArticleId, Long noticeId, LocalDateTime noticeCreatedAt) {
        this.noticeMessage = noticeMessage;
        this.NoticeArticleId = NoticeArticleId;
        this.noticeId = noticeId;
        this.noticeCreatedAt = noticeCreatedAt;
    }
}
