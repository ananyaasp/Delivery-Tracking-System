package com.example.demo.repository;

import com.example.demo.model.StatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusLogRepository extends JpaRepository<StatusLog, Long> {

    List<StatusLog> findByShipmentId(Long shipmentId);
}