package com.sparta.stockhub.dto.responseDto;

import com.sparta.stockhub.domain.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private String nickname;
    private String profileImage;
    private String rank;
    private int experience;

    public UserResponseDto(User user) {
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.rank = user.getRank();
        this.experience = user.getExperience();
    }
}
