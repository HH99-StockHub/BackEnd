package com.sparta.stockhub.utils;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sparta.stockhub.dto.responseDto.NewsResponseDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class NewsSearch {

    public String search(String query) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "TEEr4FDLHYQSYKwO5xgo");
        headers.add("X-Naver-Client-Secret", "Olq7SqMUnv");
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/news.json?query=" + query, HttpMethod.GET, requestEntity, String.class);
        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        System.out.println("Response status: " + status);
        System.out.println(response);

        return response;
    }

    public List<NewsResponseDto> fromJSONtoItems(String result) {
        JSONObject rjson = new JSONObject(result);  //문자열을 JSONObject로 만들어준다.
        //System.out.println(rjson);
        JSONArray items = rjson.getJSONArray("items");  //rjson에서 JSONArray를 꺼내겠다.

        List<NewsResponseDto> newsResponseDtoList = new ArrayList<>();

        for (int i=0; i<items.length(); i++) {
            JSONObject itemJson = items.getJSONObject(i);
            NewsResponseDto newsDto = new NewsResponseDto(itemJson);
            newsResponseDtoList.add(newsDto);
        }
        return newsResponseDtoList;
    }
}
