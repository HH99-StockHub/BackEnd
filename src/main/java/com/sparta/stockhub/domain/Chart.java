package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "charts")
@Getter
@Setter
@NoArgsConstructor
public class Chart {

    @Id
    private String id;

    private String stockCode;

    private String stockName;

    private ArrayList chart;

    public Chart(String id, String stockCode, String stockName, ArrayList chart) {
        this.id = id;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.chart = chart;
    }
}
