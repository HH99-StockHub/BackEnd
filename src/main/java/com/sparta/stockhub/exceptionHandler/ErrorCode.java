package com.sparta.stockhub.exceptionHandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 BAD_REQUEST
    BAD_REQUEST_NOTWRITE(HttpStatus.BAD_REQUEST, "400", "제목 작성이 필요합니다."),
    BAD_REQUEST_STOCKNAME(HttpStatus.BAD_REQUEST, "400", "종목 선택이 필요합니다."),
    BAD_REQUEST_TITLELENGTH(HttpStatus.BAD_REQUEST, "400", "제목은 40자 이내로 작성해 주세요."),
    BAD_REQUEST_POINTLENGTH(HttpStatus.BAD_REQUEST, "400", "투자 포인트는 40자 이내로 작성해 주세요."),
    BAD_REQUEST_CONTENTLENGTH(HttpStatus.BAD_REQUEST, "400", "세부 내용은 800자 이내로 작성해 주세요."),
    BAD_REQUEST_COMMENTLENGTH(HttpStatus.BAD_REQUEST, "400", "댓글 내용은 300자 이내로 작성해 주세요."),

    // 401 Unauthorized
    UNAUTHORIZED_NOTUSER(HttpStatus.UNAUTHORIZED, "401", "삭제 권한이 없습니다."),

    // 403 FORBIDDEN
    FORBIDDEN_MYARTICLEVOTE(HttpStatus.FORBIDDEN, "403", "본인 게시글에 투표할 수 없습니다."),
    FORBIDDEN_OLDVOTEUP(HttpStatus.FORBIDDEN, "403", "이미 찬성 투표를 하였습니다."),
    FORBIDDEN_OLDVOTEDOWN(HttpStatus.FORBIDDEN, "403", "이미 반대 투표를 하였습니다."),

    // 404 NOT_FOUND
    NOT_FOUND_ARTICLE(HttpStatus.NOT_FOUND, "404", "게시글이 존재하지 않습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "404", "댓글이 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404", "사용자가 존재하지 않습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
