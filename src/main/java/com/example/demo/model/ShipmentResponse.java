package com.example.demo.model;

public class ShipmentResponse {

    private Long id;
    private String sourceAddress;
    private String destinationAddress;
    private String status;
    private String agentName;
    private String agentPhone;

    public ShipmentResponse(Long id, String sourceAddress, String destinationAddress, String status, String agentName,
            String agentPhone) {
        this.id = id;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.status = status;
        this.agentName = agentName;
        this.agentPhone = agentPhone;
    }

    public Long getId() {
        return id;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public String getStatus() {
        return status;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getAgentPhone() {
        return agentPhone;
    }
}