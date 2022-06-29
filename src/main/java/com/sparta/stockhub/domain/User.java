package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long userId;

    @Column(nullable = false)
    private Long kakaoId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profileImage;

    public User(Long kakaoId, String username, String password, String nickname, String profileImage) {
        this.kakaoId = kakaoId;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
