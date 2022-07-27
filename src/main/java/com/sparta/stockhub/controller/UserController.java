package com.sparta.stockhub.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.stockhub.dto.responseDto.UserResponseDto;
import com.sparta.stockhub.exceptionHandler.CustomException;
import com.sparta.stockhub.exceptionHandler.ErrorCode;
import com.sparta.stockhub.security.UserDetailsImpl;
import com.sparta.stockhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        userService.kakaoLogin(code, response);
    }

    // 유저: 사용자 상세정보 조회
    @PostMapping("/userDetails")
    public UserResponseDto getRank(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            return userService.getRank(userDetails.getUser());
        } else throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
    }

    // 유저: 닉네임 변경
    @PutMapping("/user/nickname")
    public void changeNickname(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String newNickname) {
        if (userDetails != null) {
            userService.changeNickname(userDetails.getUser(), newNickname);
        }
    }
}
