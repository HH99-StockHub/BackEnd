package com.sparta.stockhub.exceptionHandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 Bad Request.
    BAD_REQUEST_NOTWRITE(HttpStatus.BAD_REQUEST, "400_1", "제목 작성이 필요합니다."),
    BAD_REQUEST_STOCKNAME(HttpStatus.BAD_REQUEST, "400_2", "종목 선택이 필요합니다."),

    // 401 Unauthorized.
    UNAUTHORIZED_NOTUSER(HttpStatus.UNAUTHORIZED, "401_1", "삭제 권한이 없습니다."),
    UNAUTHORIZED_LOGIN(HttpStatus.UNAUTHORIZED, "401_2", "로그인이 필요합니다."),

    // 403 Forbidden.
    FORBIDDEN_MYARTICLEVOTE(HttpStatus.FORBIDDEN, "403_1", "본인 게시글에 투표할 수 없습니다."),
    FORBIDDEN_OLDVOTEUP(HttpStatus.FORBIDDEN, "403_2", "이미 찬성 투표를 하였습니다."),
    FORBIDDEN_OLDVOTEDOWN(HttpStatus.FORBIDDEN, "403_3", "이미 반대 투표를 하였습니다."),

    // 404 Not Found.
    NOT_FOUND_ARTICLE(HttpStatus.NOT_FOUND, "404_1", "게시글이 존재하지 않습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "404_2", "댓글이 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404_3", "사용자가 존재하지 않습니다"),
    NOT_FOUND_STOCKNAME(HttpStatus.NOT_FOUND, "404_4", "종목이 존재하지 않습니다"),

    // 406 Not Acceptable.
    NOT_ACCEPTABLE_NICKNAME(HttpStatus.NOT_ACCEPTABLE, "406_1", "유효하지 않은 형식의 닉네임입니다."),
    NOT_ACCEPTABLE_CURSEWORDS(HttpStatus.NOT_ACCEPTABLE, "406_2", "욕설을 포함할 수 없습니다."),

    // 411 Length Required.
    BAD_REQUEST_TITLELENGTH(HttpStatus.LENGTH_REQUIRED, "411_1", "제목은 40자 이내로 작성해 주세요."),
    BAD_REQUEST_POINTLENGTH(HttpStatus.LENGTH_REQUIRED, "411_2", "투자 포인트는 40자 이내로 작성해 주세요."),
    BAD_REQUEST_CONTENTLENGTH(HttpStatus.LENGTH_REQUIRED, "411_3", "세부 내용은 800자 이내로 작성해 주세요."),
    BAD_REQUEST_COMMENTLENGTH(HttpStatus.LENGTH_REQUIRED, "411_4", "댓글 내용은 300자 이내로 작성해 주세요."),
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
