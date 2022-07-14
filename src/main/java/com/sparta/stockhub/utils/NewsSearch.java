package com.sparta.stockhub.utils;

import com.sparta.stockhub.dto.responseDto.NewsListResponseDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class NewsSearch {

    // 게시글: 종목 뉴스 검색
    public List<NewsListResponseDto> search(String stockName) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "TEEr4FDLHYQSYKwO5xgo");
        headers.add("X-Naver-Client-Secret", "Olq7SqMUnv");
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/news.json?query=" + stockName, HttpMethod.GET, requestEntity, String.class);
        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();

        return fromJSONtoItems(response); // response를 리스트로 변환하여 controller에 반환
    }

    // 뉴스 검색 결과 리스트로 변환
    public List<NewsListResponseDto> fromJSONtoItems(String response) {
        JSONObject rjson = new JSONObject(response); // 문자열을 JSONObject로 변환
        JSONArray items = rjson.getJSONArray("items"); // rjson에서 JSONArray를 추출
        List<NewsListResponseDto> newsListResponseDtoList = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {
            JSONObject itemJson = items.getJSONObject(i);
            NewsListResponseDto newsDto = new NewsListResponseDto(itemJson);
            newsListResponseDtoList.add(newsDto);
        }

        return newsListResponseDtoList;
    }
}
