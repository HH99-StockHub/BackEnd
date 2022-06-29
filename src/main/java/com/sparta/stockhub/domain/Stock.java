package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    @Column(nullable = false)
    String stockCode;

    @Column(nullable = false)
    String stockName;

    @Column(nullable = false)
    int stockPrice;

    public Stock(String stockCode, String stockName, int stockPrice) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.stockPrice = stockPrice;
    }
}
