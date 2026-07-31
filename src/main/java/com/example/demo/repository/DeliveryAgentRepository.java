package com.example.demo.repository;

import com.example.demo.model.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {

    List<DeliveryAgent> findByAvailableTrue();
}