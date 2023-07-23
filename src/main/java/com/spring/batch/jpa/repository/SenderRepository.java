package com.spring.batch.jpa.repository;

import com.spring.batch.jpa.entity.SenderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderRepository extends JpaRepository<SenderEntity, Long> {
}
