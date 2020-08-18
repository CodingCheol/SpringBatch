package com.springbatch.jpa.repository;

import com.springbatch.jpa.entity.SenderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderRepository extends JpaRepository<SenderEntity, Long> {
}
