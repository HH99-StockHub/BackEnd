package com.sparta.stockhub.dto.responseDto;

import com.sparta.stockhub.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private Long kakaoId;
    private String username;
    private String nickname;
    private String profileImage;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.kakaoId = user.getKakaoId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
    }
}
