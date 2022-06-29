package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Stock;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    Stock stock = new Stock("005930", "삼성전자", 58000);
}
