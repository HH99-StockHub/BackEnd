package com.sparta.stockhub;

import com.sparta.stockhub.repository.StockRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableMongoRepositories(basePackageClasses = StockRepository.class)
public class StockHubApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockHubApplication.class, args);
	}
}
