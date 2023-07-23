package com.spring.batch.jpa.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class HistoryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Long senderId;

    @Column
    private String moneyList;

    @Builder
    public HistoryEntity(Long senderId, List<MoneyEntity> moneyEntityList){
        this.senderId = senderId;
        this.moneyList = moneyEntityList.stream().map(moneyEntity -> String.valueOf(moneyEntity.getId())).collect(Collectors.joining(","));
    }
}
