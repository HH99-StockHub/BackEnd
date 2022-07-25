package com.sparta.stockhub.chat;


import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Column;

@Getter
@AllArgsConstructor
public class NoticeResponseDto {

    private Long noticeId;

    private Long userId;

    private Long articleId;

    private String message;

    private boolean check;


}
