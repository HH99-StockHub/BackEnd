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

    private int stockPrice;

    private boolean registered;

    public Stock(String stockCode, String stockName, int stockPrice, boolean registered) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.stockPrice = stockPrice;
        this.registered = registered;
    }
}
