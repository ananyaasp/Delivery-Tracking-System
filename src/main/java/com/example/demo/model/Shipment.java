package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceAddress;
    private String destinationAddress;

    @Convert(converter = ShipmentStatusConverter.class)
    private ShipmentStatus status;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<StatusLog> logs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "agent_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DeliveryAgent agent;

    public Shipment() {}

    public Shipment(String sourceAddress, String destinationAddress, ShipmentStatus status) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.status = status;
    }

    public Shipment(String sourceAddress, String destinationAddress, String status) {
        this(sourceAddress, destinationAddress, status != null ? ShipmentStatus.fromString(status) : ShipmentStatus.CREATED);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status != null ? ShipmentStatus.fromString(status) : null;
    }

    public List<StatusLog> getLogs() {
        return logs;
    }

    public void setLogs(List<StatusLog> logs) {
        this.logs = logs;
    }

    public DeliveryAgent getAgent() {
        return agent;
    }

    public void setAgent(DeliveryAgent agent) {
        this.agent = agent;
    }
}