package com.spring.batch.jpa.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class SenderEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private List<MoneyEntity> moneyEntityList;

    @Builder
    public SenderEntity(String name, List<MoneyEntity> moneyEntityList){
        this.name = name;
        this.moneyEntityList = moneyEntityList;
    }
}
