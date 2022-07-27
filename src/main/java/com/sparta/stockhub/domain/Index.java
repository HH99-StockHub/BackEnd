package com.sparta.stockhub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "indices")
@Getter
@Setter
@NoArgsConstructor
public class Index {

    @Id
    private String id;

    private String indexName;

    private String lastPrice;

    private String change;

    private String changeRate;

    public Index(String id, String indexName, String lastPrice, String change, String changeRate) {
        this.id = getId();
        this.indexName = getIndexName();
        this.lastPrice = getLastPrice();
        this.change = getChange();
        this.changeRate = getChangeRate();
    }
}
