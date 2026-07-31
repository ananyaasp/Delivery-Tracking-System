package com.example.demo.service;

import com.example.demo.model.Shipment;
import com.example.demo.model.ShipmentResponse;
import com.example.demo.model.DeliveryAgent;
import com.example.demo.model.StatusLog;

import com.example.demo.repository.ShipmentRepository;
import com.example.demo.repository.DeliveryAgentRepository;
import com.example.demo.repository.StatusLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private DeliveryAgentRepository agentRepository;

    @Autowired
    private StatusLogRepository statusLogRepository;

    // ✅ CREATE SHIPMENT + LOG
    public Shipment createShipment(Shipment shipment) {
        shipment.setStatus("Created");
        Shipment saved = shipmentRepository.save(shipment);

        StatusLog log = new StatusLog(
                "Created",
                LocalDateTime.now(),
                saved);
        statusLogRepository.save(log);

        return saved;
    }

    // ✅ GET ALL
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    // ✅ GET SHIPMENT BY ID (for tracking) ← ADD THIS
    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id).orElse(null);
    }

    // ✅ UPDATE STATUS + AUTO-ASSIGN AGENT ON PICKEDUP
    public Shipment updateStatus(Long id, String status) {
        Shipment shipment = shipmentRepository.findById(id).orElse(null);

        if (shipment != null) {
            // 🔑 AUTO-ASSIGN available agent when status becomes PickedUp
            if ("PickedUp".equalsIgnoreCase(status) && shipment.getAgent() == null) {
                List<DeliveryAgent> availableAgents = agentRepository.findByAvailableTrue();

                if (!availableAgents.isEmpty()) {
                    DeliveryAgent agent = availableAgents.get(0);
                    agent.setAvailable(false);
                    agentRepository.save(agent);
                    shipment.setAgent(agent);
                } else {
                    // 🚫 No agents available — throw exception
                    throw new RuntimeException("No delivery agents available. Please try again later.");
                }
            }

            shipment.setStatus(status);
            Shipment updated = shipmentRepository.save(shipment);

            StatusLog log = new StatusLog(
                    status,
                    LocalDateTime.now(),
                    updated);
            statusLogRepository.save(log);

            return updated;
        }
        return null;
    }

    // ✅ ASSIGN AGENT MANUALLY (UNCHANGED)
    public ShipmentResponse assignAgent(Long shipmentId, Long agentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId).orElse(null);
        DeliveryAgent agent = agentRepository.findById(agentId).orElse(null);

        if (shipment != null && agent != null && agent.isAvailable()) {

            shipment.setAgent(agent);
            agent.setAvailable(false);

            agentRepository.save(agent);
            shipmentRepository.save(shipment);

            return new ShipmentResponse(
                    shipment.getId(),
                    shipment.getSourceAddress(),
                    shipment.getDestinationAddress(),
                    shipment.getStatus(),
                    agent.getName(),
                    agent.getPhone());
        }
        return null;
    }
}