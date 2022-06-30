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
    private String username; // 카카오 로그인 정보에 담긴 "id"

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profileImage;

    public User(String username, String password, String nickname, String profileImage) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
