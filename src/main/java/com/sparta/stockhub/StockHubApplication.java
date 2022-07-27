package com.sparta.stockhub;

import com.sparta.stockhub.repository.StockRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableMongoRepositories(basePackageClasses = StockRepository.class)
public class StockHubApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockHubApplication.class, args);
	}
}
