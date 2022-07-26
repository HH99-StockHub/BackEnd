package com.sparta.stockhub.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.stockhub.security.UserDetailsImpl;
import com.sparta.stockhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        userService.kakaoLogin(code, response);
    }

    // 유저: 사용자 랭크 조회
    @PostMapping("/user/rank")
    public String getRank(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUser().getRank();
    }
}
