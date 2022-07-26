package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.Chart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChartRepository extends MongoRepository<Chart, String> {

    Optional<Chart> findByStockName(String stockName);
}
