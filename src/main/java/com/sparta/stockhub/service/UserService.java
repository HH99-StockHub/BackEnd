package com.sparta.stockhub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.stockhub.domain.User;
import com.sparta.stockhub.dto.responseDto.UserResponseDto;
import com.sparta.stockhub.exceptionHandler.CustomException;
import com.sparta.stockhub.exceptionHandler.ErrorCode;
import com.sparta.stockhub.repository.UserRepository;
import com.sparta.stockhub.security.UserDetailsImpl;
import com.sparta.stockhub.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    // 카카오 로그인
    public void kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);
        // 2. "액세스 토큰"으로 카카오 로그인 정보 호출
        User user = getKakaoUserInfo(accessToken);
        // 3. JWT 형식의 토큰 생성
        createJwt(user, response);
    }

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getAccessToken(String code) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders header = new HttpHeaders();
        header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "12c4e96969c4b50ad263268577cdcb76");
        body.add("redirect_uri", "http://stockhub.co.kr.s3-website.ap-northeast-2.amazonaws.com/user/kakao/callback");
//        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
        body.add("code", code);

        // HTTP 요청
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, header);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답에서 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 2. "액세스 토큰"으로 카카오 로그인 정보 호출
    private User getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + accessToken);
        header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(header);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String username = jsonNode.get("id").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String profileImage = jsonNode.get("properties").get("profile_image").asText();

        // 카카오로그인을 처음 한 경우 UserRepository에 저장
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            String password = UUID.randomUUID().toString();
            user = new User(username, password, nickname, profileImage);
            userRepository.save(user);
        }
        return user;
    }

    // 3. JWT 형식의 토큰 생성
    private void createJwt(User user, HttpServletResponse response) throws UnsupportedEncodingException {

        UserDetails userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetailsJwt = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetailsJwt);

        String nicknameEncoded = URLEncoder.encode(user.getNickname(), "utf-8");
        String rankEncoded = URLEncoder.encode(user.getRank(), "utf-8");

        response.addHeader("Authorization", "BEARER " + token);
        response.addHeader("userId", String.valueOf(user.getUserId()));
        response.addHeader("profileImage", user.getProfileImage());
        response.addHeader("nickname", nicknameEncoded);
        response.addHeader("rank", rankEncoded);
        response.addHeader("experience", String.valueOf(user.getExperience()));
    }

    // 유저 랭크 업데이트
    @Transactional
    public void updateRank(User user) {

        int exp = user.getExperience();
        if (exp < 10) user.setRank("신입");
        else if (exp < 100) user.setRank("초보");
        else if (exp < 200) user.setRank("중수");
        else if (exp < 500) user.setRank("고수");
        else user.setRank("지존");

        userRepository.save(user);
    }

    // 유저: 사용자 랭크 조회
    public UserResponseDto getRank(User user) {
        UserResponseDto responseDto = new UserResponseDto(user);
        return responseDto;
    }

    // 유저: 닉네임 변경
    @Transactional
    public void changeNickname(User user, String newNickname) {

        String pattern = "^(?=.*[a-zA-Z0-9가-힣])[a-zA-Z0-9가-힣]{2,12}$"; // 유효성 검사: 영문, 한글, 숫자 조합하여 2~12자리
        boolean regex = Pattern.matches(pattern, newNickname);
        if (regex == false) throw new CustomException(ErrorCode.NOT_ACCEPTABLE_NICKNAME);

        String[] curseWords = { // 욕설 검사
                "개걸레", "개보지", "개씨발", "개좆", "개지랄", "걸레년",
                "느검마", "느금", "니기미", "니애미", "니애비", "닝기미",
                "미친년", "미친놈", "미친새끼", "백보지", "보지털", "보짓물", "빠구리",
                "썅년", "썅놈", "씨발", "씹년", "씹보지", "씹새끼", "씹자지", "씹창",
                "잠지털", "잡년", "잡놈", "젓같은", "젖같은", "좆", "창녀", "창년"
        };
        for (String curseWord : curseWords)
            if (newNickname.contains(curseWord)) throw new CustomException(ErrorCode.NOT_ACCEPTABLE_CURSEWORDS);

        user.setNickname(newNickname);
        userRepository.save(user);
    }
}
