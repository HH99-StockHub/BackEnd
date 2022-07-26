package com.sparta.stockhub.security.jwt;

import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@Component
public class HeaderTokenExtractor {

    public final String HEADER_PREFIX = "Bearer ";

    public String extract(String header, HttpServletRequest request) {

        if (header == null || header.equals("") || header.length() < HEADER_PREFIX.length()) {
            System.out.println("error request : " + request.getRequestURI());
            throw new NoSuchElementException("No JWT");
        }

        return header.substring(HEADER_PREFIX.length());
    }

    //주희 추가
    public String extract(String header) {
        if (header == null || header.equals("") || header.length() < HEADER_PREFIX.length()) {
            throw new NoSuchElementException("No JWT");
        }
        return header.substring(HEADER_PREFIX.length());
    }
}