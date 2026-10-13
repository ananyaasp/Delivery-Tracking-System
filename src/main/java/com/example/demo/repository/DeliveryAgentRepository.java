package com.example.demo.repository;

import com.example.demo.model.DeliveryAgent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM DeliveryAgent a WHERE a.available = true ORDER BY a.id ASC")
    List<DeliveryAgent> findAvailableAgentsWithLock();

    List<DeliveryAgent> findByAvailableTrue();
}