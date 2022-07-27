package com.sparta.stockhub.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    private String id;

    private String stockCode;

    private String stockName;

    private boolean registered;

    private int stockPrice;

    private int change;

    private float changeRate;

    private int lastPrice;

    private int startPrice;

    private int highPrice;

    private int lowPrice;

    private int tradeVolume;
}
