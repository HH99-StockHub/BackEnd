package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StockRepository extends MongoRepository<Stock, String> {
    Optional<Stock> findByStockName(String stockName);
}
