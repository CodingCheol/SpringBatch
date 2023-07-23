package com.spring.batch.jpa.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class MoneyEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String value;
    @Builder
    public MoneyEntity(String value){
        this.value=value;
    }
}
