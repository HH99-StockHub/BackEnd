package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.Index;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IndexRepository extends MongoRepository<Index, String> {
}
