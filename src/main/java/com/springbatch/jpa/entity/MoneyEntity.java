package com.springbatch.jpa.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
