package com.sparta.stockhub.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.stockhub.dto.responseDto.UserResponseDto;
import com.sparta.stockhub.security.UserDetailsImpl;
import com.sparta.stockhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public UserResponseDto getRank(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            return userService.getRank(userDetails.getUser());
        } else throw new IllegalArgumentException("로그인이 필요합니다.");
    }

    // 유저: 닉네임 변경
    @PutMapping("/user/nickname")
    public void changeNickname(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String newNickname) {
        if (userDetails != null) {
            userService.changeNickname(userDetails.getUser(), newNickname);
        }
    }
}
