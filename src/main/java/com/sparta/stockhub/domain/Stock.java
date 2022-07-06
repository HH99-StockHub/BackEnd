package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "stocks")
@Getter
@Setter
@NoArgsConstructor
public class Stock {

    @Id
    private String id;

    private String stockCode;

    private String stockName;

    private int price;

    public Stock(String stockCode, String stockName, int price) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.price = price;
    }
}
