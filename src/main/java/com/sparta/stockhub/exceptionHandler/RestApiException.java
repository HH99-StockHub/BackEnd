package com.sparta.stockhub.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RestApiException { //담아서 보내줄 형태
    private String errorMessage;
    private HttpStatus httpStatus;
}
