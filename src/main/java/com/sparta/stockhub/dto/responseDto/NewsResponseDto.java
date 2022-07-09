package com.sparta.stockhub.dto.responseDto;

import org.json.JSONObject;
import lombok.Getter;

@Getter
public class NewsResponseDto {

    private String title;

    private String link;

    private String description;

    private String pubDate;

    public NewsResponseDto(JSONObject newsJson) {
        this.title = newsJson.getString("title");
        this.link = newsJson.getString("link");
        this.description = newsJson.getString("description");
        this.pubDate = newsJson.getString("pubDate");

    }
}
