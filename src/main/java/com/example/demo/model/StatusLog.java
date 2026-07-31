package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class StatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    @JsonBackReference
    private Shipment shipment;

    public StatusLog() {}

    public StatusLog(String status, LocalDateTime timestamp, Shipment shipment) {
        this.status = status;
        this.timestamp = timestamp;
        this.shipment = shipment;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
}